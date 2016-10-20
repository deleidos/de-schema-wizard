package com.deleidos.dp.profiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.deleidos.dp.accumulator.AbstractProfileAccumulator;
import com.deleidos.dp.enums.GroupingBehavior;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.interpretation.IEConfig;
import com.deleidos.dp.interpretation.InterpretationEngineFacade;
import com.deleidos.dp.profiler.api.AbstractProfiler;
import com.deleidos.dp.reversegeocoding.CoordinateProfile;
import com.deleidos.dp.reversegeocoding.ReverseGeocoder;
import com.deleidos.dp.reversegeocoding.ReverseGeocoder.ReverseGeocoderCallbackListener;

/**
 * 
 * @author leegc
 *
 * @param <B> The type of the bean that should be returned
 */
public abstract class AbstractReverseGeocodingProfiler<T> extends AbstractProfiler<T> implements ReverseGeocoderCallbackListener {
	protected GroupingBehavior groupingBehavior = GroupingBehavior.GROUP_ARRAY_VALUES;
	private static final Logger logger = Logger.getLogger(AbstractReverseGeocodingProfiler.class);
	protected final int maxGeoCalls;
	protected Map<String, AbstractProfileAccumulator<?>> accumulatorMapping;
	private Map<String, QueryTrackingData> queryTrackingMapping;
	protected List<CoordinateProfile> coordinateProfiles;
	protected ReverseGeocoder reverseGeocoder;
	protected volatile int numberASynchronousReverseGeocodingCallbacks = 0;
	protected volatile int reverseGeocodingAnswers = 0;
	protected int unaffiliatedGeoCount = 0;
	protected int bufferedQueries = 0;
	protected int reverseGeocodeQueries = 0;
	protected int queriesWithheld = 0;
	protected int minimumBatchSize = 500;

	protected static List<String> emptyCoordinatePair() {
		return new ArrayList<String>(Arrays.asList(null, null));
	}

	public AbstractReverseGeocodingProfiler() {
		if (InterpretationEngineFacade.getHttpInterpretationEngine().isPresent()) {
			maxGeoCalls = InterpretationEngineFacade.getHttpInterpretationEngine().get().getConfig().getMaxGeoCalls();
		} else {
			maxGeoCalls = IEConfig.BUILTIN_CONFIG.getMaxGeoCalls();
		}
		coordinateProfiles = new ArrayList<CoordinateProfile>();
		queryTrackingMapping = new HashMap<String, QueryTrackingData>();
		try {
			reverseGeocoder = new ReverseGeocoder();
		} catch (Exception e) {
			logger.error("Geocoder not ready.");
			logger.error(e);
		}
		reverseGeocoder.setCallbackListener(this);
	}

	protected boolean isOtherIndexNull(String[] coordinatePair, int index) {
		return true;
	}

	protected void sendCoordinateProfileBatchesToReverseGeocoder() throws DataAccessException {
		waitForCallbacks();
		numberASynchronousReverseGeocodingCallbacks = coordinateProfiles.size();
		for(CoordinateProfile coordinateProfile : coordinateProfiles) {
			if (!queryTrackingMapping.containsKey(coordinateProfile.getLatitude())) {
				queryTrackingMapping.put(coordinateProfile.getLatitude(), new QueryTrackingData());
			}
			QueryTrackingData queryTrackingData = queryTrackingMapping.get(coordinateProfile.getLatitude());

			int maxNumReverseGeoEntries = maxGeoCalls - queryTrackingData.count;
			if (maxNumReverseGeoEntries < coordinateProfile.getUndeterminedCoordinateBuffer().size()) {
				if (maxNumReverseGeoEntries == 0) {
					// drop all of the unprocessed reverse geo entries
					queryTrackingData.droppedCount += coordinateProfile.getUndeterminedCoordinateBuffer().size();
					coordinateProfile.setUndeterminedCoordinateBuffer(new ArrayList<Double[]>());
				} else {
					int numToRemove = coordinateProfile.getUndeterminedCoordinateBuffer().size() - maxNumReverseGeoEntries;
					queryTrackingData.droppedCount += numToRemove;
					for (int i = 0; i < numToRemove; i++) {
						coordinateProfile.getUndeterminedCoordinateBuffer().remove(
								coordinateProfile.getUndeterminedCoordinateBuffer().size() - 1);
					}
				}
				
				if (!queryTrackingData.reported) {
					logger.info("Maximum reverse geocoding queries ("+ 
							maxGeoCalls+") reached for " + coordinateProfile + ".");
					queryTrackingData.reported = true;
				}
			} 

			queryTrackingData.count += coordinateProfile.getUndeterminedCoordinateBuffer().size();
			reverseGeocoder.getCountriesFromLatLngsASync(
					coordinateProfile.getIndex(), coordinateProfile.getUndeterminedCoordinateBuffer());

			coordinateProfile.getUndeterminedCoordinateBuffer().clear();
		}
	}

	private void waitForCallbacks() throws DataAccessException {
		final long TIMEOUT_MILLIS = 60*1000;
		long start = System.currentTimeMillis(); 
		int awaiting = numberASynchronousReverseGeocodingCallbacks;
		if(awaiting == 0) {
			return;
		}
		logger.debug("Awaiting the return of " + numberASynchronousReverseGeocodingCallbacks + " callbacks.");
		while(numberASynchronousReverseGeocodingCallbacks > 0) {
			try {
				Thread.sleep(100);
				synchronized(this) {
					if(numberASynchronousReverseGeocodingCallbacks != awaiting && numberASynchronousReverseGeocodingCallbacks > 0) {
						logger.debug("Awaiting the return of " + numberASynchronousReverseGeocodingCallbacks + " callbacks.");
						awaiting = numberASynchronousReverseGeocodingCallbacks;
					}
				}
				if(System.currentTimeMillis() - start > TIMEOUT_MILLIS) {
					throw new DataAccessException("Timed out waiting for reverse geocoding calls to return.");
				}
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}

	@Override
	public void handleResult(int coordinateIndex, List<String> resultingCountryNames) {
		logger.debug("Handling request " + coordinateProfiles.get(coordinateIndex).getLatitude()+","+coordinateProfiles.get(coordinateIndex).getLongitude());
		CoordinateProfile coordinateProfile = this.coordinateProfiles.get(coordinateIndex);
		for(String country : resultingCountryNames) {
			if(country != null) {
				coordinateProfile.getCountryFrequencyMapping().compute(country, (k, v) -> (v == null) ? 1 : v + 1);
			} else {
				unaffiliatedGeoCount++;
			}
		}
		reverseGeocodingAnswers += resultingCountryNames.size();
		if(numberASynchronousReverseGeocodingCallbacks > 0) {
			numberASynchronousReverseGeocodingCallbacks--;
		}
	}
	
	@Override
	public final T finish() {
		try {
			sendCoordinateProfileBatchesToReverseGeocoder();
			waitForCallbacks(); 
			//need to wait for last batch to come back
		} catch(DataAccessException e) {
			logger.error(e);
			logger.error("Lost "+bufferedQueries+" reverse geocoding queries due to a connection timeout.");
		}
		logger.debug(reverseGeocodeQueries + " total reverse geocoding queries executed.");
		return subclazzFinish();
	}
	
	protected abstract T subclazzFinish();

	public int getReverseGeocodeQueries() {
		return reverseGeocodeQueries;
	}

	protected CoordinateProfile getCoordinateProfile(List<CoordinateProfile> coordinateProfiles, String latitudeKey, String longitudeKey) {
		for(CoordinateProfile coordinateProfile : coordinateProfiles) {
			if(coordinateProfile.getLatitude().equals(latitudeKey) && coordinateProfile.getLongitude().equals(longitudeKey)) {
				return coordinateProfile;
			}
		}
		return null;
	}

	public int getMinimumBatchSize() {
		return minimumBatchSize;
	}

	public void setMinimumBatchSize(int minimumBatchSize) {
		this.minimumBatchSize = minimumBatchSize;
	}

	public int getReverseGeocodingAnswers() {
		return reverseGeocodingAnswers;
	}

	public Map<String, AbstractProfileAccumulator<?>> getAccumulatorMapping() {
		return accumulatorMapping;
	}

	public void setAccumulatorMapping(Map<String, AbstractProfileAccumulator<?>> accumulatorMapping) {
		this.accumulatorMapping = accumulatorMapping;
	}

	private class QueryTrackingData {
		private Integer count = 0;
		private Integer droppedCount = 0;
		private Boolean reported = false;
	}

}
