package com.deleidos.dp.profiler;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.deleidos.dp.accumulator.BundleProfileAccumulator;
import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.deserializors.ConversionUtility;
import com.deleidos.dp.enums.Tolerance;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.exceptions.MainTypeException;
import com.deleidos.dp.interpretation.InterpretationEngineFacade;
import com.deleidos.dp.profiler.api.AbstractProfiler;
import com.deleidos.dp.profiler.api.ProfilerRecord;
import com.deleidos.dp.profiler.api.ProfilingProgressUpdateHandler;

/**
 * Profiler class for sample data sets.  Takes in objects and loads them into a BundleAccumulator.  Every object key
 * has an associated BundleAccumulator.  This accumulator is a group of all three metrics types 
 * (number, string, binary) that will push the object's values into them as long as they are able to be parsed into that
 * form.  Most metrics (not histograms, though) are accumulated on the first pass 8/25/16.
 * 
 * @author leegc
 *
 */
public class SampleProfiler extends AbstractProfiler<DataSample> {
	private ProfilingProgressUpdateHandler progressUpdateListener;
	private static Logger logger = Logger.getLogger(SampleProfiler.class);
	private Tolerance tolerance;
	protected Map<String, BundleProfileAccumulator> fieldMapping;

	public SampleProfiler(Tolerance tolerance) {
		setTolerance(tolerance);
		fieldMapping = new LinkedHashMap<String, BundleProfileAccumulator>();
	}

	@Override
	public void accumulateBinaryRecord(BinaryProfilerRecord binaryRecord) {
		// binary profiler records only have one key and do not affect presence
		String key = binaryRecord.getBinaryName();
		List<Object> values = binaryRecord.normalizeRecord().get(key);
		fieldMapping.putIfAbsent(key, new BundleProfileAccumulator(key, tolerance));
		accumulateNormalizedValues(fieldMapping.get(key), key, values);

		// if the record binary, use get the detail type from the profiler record
		// this is a special case because we need Tika (in dmf) to determine the detail type
		// for binary
		BundleProfileAccumulator.getBinaryProfileAccumulator(
				fieldMapping.get(key).getState()).ifPresent(binAccumulator->
				binAccumulator.getDetailTypeTracker()[binaryRecord.getDetailType().getIndex()]++);

	}

	@Override
	public void accumulateRecord(ProfilerRecord record) {
		record.normalizeRecord().forEach((key, values)->{
			fieldMapping.putIfAbsent(key, new BundleProfileAccumulator(key, tolerance));
			accumulateNormalizedValues(fieldMapping.get(key), key, values);
		});
		recordsLoaded++;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public BundleProfileAccumulator getMetricsBundle(String key) {
		return fieldMapping.get(key);
	}

	public Set<String> keySet() {
		return fieldMapping.keySet();
	}

	public Tolerance getTolerance() {
		return tolerance;
	}

	public void setTolerance(Tolerance tolerance) {
		this.tolerance = tolerance;
	}

	public int getRecordsParsed() {
		return recordsLoaded;
	}

	/*public int getNumGeoSpatialQueries() {
		return numGeoSpatialQueries;
	}

	public void setNumGeoSpatialQueries(int numGeoSpatialQueries) {
		this.numGeoSpatialQueries = numGeoSpatialQueries;
	}*/

	public ProfilingProgressUpdateHandler getProgressUpdateListener() {
		return progressUpdateListener;
	}

	public void setProgressUpdateListener(ProfilingProgressUpdateHandler progressUpdateListener) {
		this.progressUpdateListener = progressUpdateListener;
	}

	/**
	 * Convenience method for generating a data sample based on profiler records.
	 * @param records
	 * @return
	 * @throws DataAccessException 
	 * @throws MainTypeException 
	 */
	public static DataSample generateDataSampleFromProfilerRecords(String domain, Tolerance tolerance, List<ProfilerRecord> records) throws DataAccessException {
		SampleProfiler sampleProfiler = new SampleProfiler(tolerance);
		records.forEach(record->sampleProfiler.accumulate(record));
		DataSample bean = sampleProfiler.finish();
		InterpretationEngineFacade.interpretInline(bean, domain, null);
		SampleSecondPassProfiler secondPassProfiler = new SampleSecondPassProfiler(bean);
		records.forEach(record->secondPassProfiler.accumulate(record));
		DataSample sample = secondPassProfiler.finish();
		sample.setDsName(UUID.randomUUID().toString());
		sample.setDsGuid(sample.getDsName());
		sample.setDsLastUpdate(Timestamp.from(Instant.now()));
		return sample;
	}

	@Override
	public DataSample finish() {
		DataSample dataSample = new DataSample();
		dataSample.setRecordsParsedCount(recordsLoaded);
		final Map<String, Profile> dsProfile = new LinkedHashMap<String, Profile>();

		// put any recognizable profiles in the dsProfile map
		fieldMapping.forEach((k,v)-> 
		v.getBestGuessProfile(getRecordsParsed()).ifPresent(profile->dsProfile.put(k, profile)));

		dsProfile.putAll(DisplayNameHelper.determineDisplayNames(dsProfile));
		dataSample.setDsProfile(dsProfile);

		return dataSample;
	}

}
