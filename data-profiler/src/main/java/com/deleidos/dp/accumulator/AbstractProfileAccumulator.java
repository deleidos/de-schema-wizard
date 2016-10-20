package com.deleidos.dp.accumulator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.deleidos.dp.beans.Interpretation;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.calculations.MetricsCalculationsFacade;
import com.deleidos.dp.enums.DetailType;
import com.deleidos.dp.enums.MainType;
import com.deleidos.dp.exceptions.MainTypeException;
import com.deleidos.dp.exceptions.MainTypeRuntimeException;
import com.deleidos.dp.profiler.api.ProfilingProgressUpdateHandler;

/**
 * Accumulator class that abstracts accumulation logic.  Provides most functionality for subclasses, but delegates
 * all initialization to a subclass.  This class provides a lot of functionality, but it could be optimized.  There
 * are a lot of checks because the profiling process is different at each stage (1st sample pass, 2nd sample pass, 
 * and schema pass).  This class encapsulates most of that complexity, but subclasses do need to be somewhat aware
 * of the general profiling process.
 * @author leegc
 *
 */
public abstract class AbstractProfileAccumulator<T> implements Accumulator.TypeInsensitivePresenceAwareAccumulator<Profile> {
	protected enum Stage {
		UNINITIALIZED, SAMPLE_AWAITING_FIRST_VALUE, SAMPLE_FIRST_PASS, SAMPLE_SECOND_PASS, SCHEMA_AWAITING_FIRST_VALUE, SCHEMA_PASS, FINISHED
	}
	public static final MathContext DEFAULT_CONTEXT = MathContext.DECIMAL128;
	private static Logger logger = Logger.getLogger(AbstractProfileAccumulator.class);
	protected String fieldName;
	protected int[] detailTypeTracker;
	protected List<T> distinctValues;
	public static int MAX_NUM_DISTINCT_VALUES = 10000;
	protected boolean isHighCardinality;
	protected boolean accumulateHashes;
	private boolean hasGeoSpatialData;
	protected int presenceCount;
	protected Profile profile;
	private Stage accumulationStage;

	// fields for progress updating
	private int transientWalkingCount;
	private Optional<ProfilingProgressUpdateHandler> optionalCallback = Optional.empty();

	@Override
	public Profile getState() {
		return profile;
	}

	protected AbstractProfileAccumulator(String key, MainType mainType) {
		accumulateHashes = true;
		hasGeoSpatialData = false;
		isHighCardinality = false;
		fieldName = key;
		accumulationStage = Stage.UNINITIALIZED;
		profile = new Profile();
		profile.setInterpretation(Interpretation.UNKNOWN);
		profile.setMainType(mainType.toString());
		detailTypeTracker = new int[DetailType.values().length]; 
		presenceCount = 0;
		distinctValues = new ArrayList<T>();
	}

	protected void initializeDetailFields(String knownDetailType) {
		initializeDetailFields(knownDetailType, Stage.SAMPLE_AWAITING_FIRST_VALUE);
	}

	public boolean isEmpty() {
		return getState().getPresence() < 0;
	}


	/**
	 * Accumulate data into the Accumulator's metric and use the default accumulate presence boolean -- true.
	 * @param value The value to accumulate into the metric.
	 */
	@Override
	public void accumulate(Object value) throws MainTypeException {
		accumulate(value, true);
	}

	/**
	 * Accumulate data into the Accumulator's metric.  The intention is to do minimal calculations until they are 
	 * required (e.g. don't calculate the average until it is required)
	 * @param value The value to accumulate into the metric.
	 */
	@Override
	public void accumulate(Object object, Boolean accumulatePresence) throws MainTypeException {
		presenceCount += (accumulatePresence) ? 1 : 0; 
		T value = createAppropriateObject(object);
		if(value == null) {
			return;
		}
		transientWalkingCount++;

		switch(accumulationStage) {
		case UNINITIALIZED: throw new MainTypeRuntimeException("Accumulator called but has not been initialized.");
		case SAMPLE_FIRST_PASS: 
			accumulate(accumulationStage, value);
			return;
		case SAMPLE_SECOND_PASS: 
			handleProgressUpdate(transientWalkingCount);
			accumulate(accumulationStage, value);
			return;
		case SCHEMA_PASS: 
			handleProgressUpdate(transientWalkingCount);
			accumulate(accumulationStage, value);
			return;
		case SAMPLE_AWAITING_FIRST_VALUE: { 
			initializeFirstValue(accumulationStage, value);
			accumulationStage = Stage.SAMPLE_FIRST_PASS;
			accumulate(accumulationStage, value);
			return;
		}
		case SCHEMA_AWAITING_FIRST_VALUE: {
			initializeFirstValue(accumulationStage, value);
			accumulationStage = Stage.SCHEMA_PASS;
			accumulate(accumulationStage, value);
			return;
		}
		default: throw new MainTypeRuntimeException("Accumulator stuck in unknown stage.");
		}

	}

	public void accumulateNumDistinctValues(T object) {
		if(!distinctValues.contains(object) && !isHighCardinality) {
			distinctValues.add(object);

			// stop storing distinct values at MAX_NUM_DISTINCT_VALUES
			if (distinctValues.size() >= MAX_NUM_DISTINCT_VALUES) {
				isHighCardinality = true;
			}
		}
	}

	public void accumulateWalkingCount() {
		profile.getDetail().setWalkingCount(profile.getDetail().getWalkingCount().add(BigDecimal.ONE));
	}

	public void setHasGeoSpatialData(boolean hasGeoSpatialData) {
		if(hasGeoSpatialData) {
			getState().getDetail().getHistogramOptional().ifPresent(x->x.setType("map"));
		}
		this.hasGeoSpatialData = hasGeoSpatialData;
	}

	public List<Object> getDistinctValuesAsExampleValuesList() {
		final int maxExampleListSize = 50;
		List<Object> exampleValues = getState().getExampleValues();
		if(distinctValues != null) {
			exampleValues = new ArrayList<Object>();
			//purposely lose precision
			int exampleListSize = (distinctValues.size() > maxExampleListSize) ? maxExampleListSize : distinctValues.size();
			float valueChooserRange = (float)distinctValues.size()/(float)exampleListSize;
			for(int i = 0; i < exampleListSize; i++) {
				exampleValues.add(distinctValues.get((int)valueChooserRange*i));
			}
			return exampleValues;
		} 
		if(exampleValues == null) {
			logger.error("Null distinct values object while trying to generate example values.");
			return exampleValues;
		} else {
			return exampleValues;
		}
	}

	public void handleProgressUpdate(final int progress) {
		optionalCallback.ifPresent(x->optionalCallback.get().handleProgressUpdate(progress));
	}

	@Override
	public Profile finish() {
		if (this.getState().getPresence() >= 0) {
			profile = finish(accumulationStage);
		}

		if (accumulationStage.equals(Stage.SAMPLE_FIRST_PASS) || accumulationStage.equals(Stage.SCHEMA_PASS)) {
			String distinctValuesString = MetricsCalculationsFacade.stringifyNumDistinctValues(distinctValues.size(), MAX_NUM_DISTINCT_VALUES);
			profile.getDetail().setNumDistinctValues(distinctValuesString);
		}
		if (profile.getDetail().getDetailType() == null) try {
			String detailTypeString = MetricsCalculationsFacade.getDetailTypeFromDistribution(
					getState().getMainType(), getDetailTypeTracker()).toString();
			profile.getDetail().setDetailType(detailTypeString);
		} catch (MainTypeException e) {
			logger.error(e);
			profile.getDetail().setDetailType(DetailType.TEXT.toString());
		}

		if (profile.getExampleValues() == null) {
			getState().setExampleValues(getDistinctValuesAsExampleValuesList());
		}

		if (accumulationStage.equals(Stage.FINISHED)) {
			return profile;
		} else {
			accumulationStage = Stage.FINISHED;
		}
		return profile;
	}

	/**
	 * 
	 * @param optionalCallback
	 * @throws NullPointerException if optionalCallback is null
	 */
	public void setCallback(ProfilingProgressUpdateHandler optionalCallback, boolean restartCount) {
		this.optionalCallback = Optional.of(optionalCallback);
		if(restartCount) {
			transientWalkingCount = 0;
		}
	}

	public void removeCallback() {
		this.optionalCallback = Optional.empty();
	}

	public Optional<ProfilingProgressUpdateHandler> getOptionalCallback() {
		return optionalCallback;
	}

	public boolean hasGeoSpatialData() {
		return hasGeoSpatialData;
	}

	public int getPresenceCount() {
		return presenceCount;
	}

	public void setPresenceCount(int presenceCount) {
		this.presenceCount = presenceCount;
	}

	public Stage getAccumulationStage() {
		return accumulationStage;
	}

	public void setAccumulationStage(Stage accumulationStage) {
		this.accumulationStage = accumulationStage;
	}

	public int[] getDetailTypeTracker() {
		return detailTypeTracker;
	}

	public void setDetailTypeTracker(int[] detailTypeTracker) {
		this.detailTypeTracker = detailTypeTracker;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public List<T> getDistinctValues() {
		return distinctValues;
	}

	public void setDistinctValues(List<T> distinctHashes) {
		this.distinctValues = distinctHashes;
	}

	public static NumberProfileAccumulator generateNumberProfileAccumulator(String key) throws MainTypeException {
		return (NumberProfileAccumulator)generateProfileAccumulator(key, MainType.NUMBER);
	}

	public static StringProfileAccumulator generateStringProfileAccumulator(String key) throws MainTypeException {
		return (StringProfileAccumulator)generateProfileAccumulator(key, MainType.STRING);
	}

	public static BinaryProfileAccumulator generateBinaryProfileAccumulator(String key) throws MainTypeException {
		return (BinaryProfileAccumulator)generateProfileAccumulator(key, MainType.BINARY);
	}

	public static AbstractProfileAccumulator<?> generateProfileAccumulator(String key, MainType type) 
			throws MainTypeException {
		AbstractProfileAccumulator<?> abstractProfileAccumulator = null;
		switch(type) {
		case STRING: abstractProfileAccumulator = new StringProfileAccumulator(key); break;
		case NUMBER: abstractProfileAccumulator = new NumberProfileAccumulator(key); break;
		case BINARY: abstractProfileAccumulator = new BinaryProfileAccumulator(key); break;
		case NULL: return null;
		default: throw new MainTypeException("Not number, string, or binary.");
		}
		abstractProfileAccumulator.initializeDetailFields(null);
		abstractProfileAccumulator.setAccumulationStage(Stage.SAMPLE_AWAITING_FIRST_VALUE);
		return abstractProfileAccumulator;
	}

	public static AbstractProfileAccumulator<?> generateProfileAccumulator(String key, Profile profile)
			throws MainTypeException{
		MainType mainType = profile.getMainTypeClass();
		AbstractProfileAccumulator<?> abstractProfileAccumulator = null;
		switch(mainType) {
		case STRING: abstractProfileAccumulator = new StringProfileAccumulator(key); break;
		case NUMBER: abstractProfileAccumulator = new NumberProfileAccumulator(key); break;
		case BINARY: abstractProfileAccumulator = new BinaryProfileAccumulator(key); break;
		case NULL: return null;
		default: throw new MainTypeException("Not number, string, or binary.");
		}
		abstractProfileAccumulator.accumulationStage = Stage.SAMPLE_SECOND_PASS;
		abstractProfileAccumulator.profile = profile;
		abstractProfileAccumulator.initializeForSecondPassAccumulation(profile);
		return abstractProfileAccumulator;
	}

	public static AbstractProfileAccumulator<?> generateProfileAccumulator(String key, Profile schemaProfile, int recordsInSchema, List<Profile> sampleProfiles)
			throws MainTypeException {
		AbstractProfileAccumulator<?> abstractProfileAccumulator = null;
		if(sampleProfiles.size() == 0) {
			return null;
		} else {
			MainType mainType = (schemaProfile != null) ? schemaProfile.getMainTypeClass() : sampleProfiles.get(0).getMainTypeClass();
			switch(mainType) {
			case STRING: {
				abstractProfileAccumulator = new StringProfileAccumulator(key); 
				break;
			}
			case NUMBER: {
				abstractProfileAccumulator = new NumberProfileAccumulator(key); 
				break;
			}
			case BINARY: {
				abstractProfileAccumulator = new BinaryProfileAccumulator(key);
				break;
			}
			case NULL: return null;
			default: throw new MainTypeException("Not number, string, or binary.");
			}
			if(schemaProfile == null && sampleProfiles.size() < 1) {
				throw new MainTypeRuntimeException("No data samples or schema given to accumulator.");
			}
			if(schemaProfile != null) {
				abstractProfileAccumulator.accumulationStage = Stage.SCHEMA_PASS;
				abstractProfileAccumulator.profile = schemaProfile;
				abstractProfileAccumulator.setPresenceCount(
						(int)(recordsInSchema * abstractProfileAccumulator.profile.getPresence()));
			} else {
				abstractProfileAccumulator.profile = sampleProfiles.get(0);
				abstractProfileAccumulator.accumulationStage = Stage.SCHEMA_AWAITING_FIRST_VALUE;
			}
			abstractProfileAccumulator = abstractProfileAccumulator.initializeForSchemaAccumulation(schemaProfile, recordsInSchema, sampleProfiles);
			if(schemaProfile == null) {
				abstractProfileAccumulator.initializeDetailFields(sampleProfiles.get(0).getDetail().getDetailType(), Stage.SCHEMA_AWAITING_FIRST_VALUE);
			}
			return abstractProfileAccumulator;
		}
	}

	public static MainType associatedMainType(AbstractProfileAccumulator<?> accumulator) {
		if(accumulator instanceof NumberProfileAccumulator) {
			return MainType.NUMBER;
		} else if(accumulator instanceof StringProfileAccumulator) {
			return MainType.STRING;
		} else if(accumulator instanceof BinaryProfileAccumulator) {
			return MainType.BINARY;
		} else {
			return null;
		}
	}

	/**
	 * Abstract method requiring that a subclass initial any detail fields that do not require a first value.
	 * An example of this is setting a walking count to 0 because it can be initialized without an initial value.
	 * @param knownDetailType The detail type or null if unknown
	 * @param resultingStage the expected stage after this method is called
	 */
	protected abstract void initializeDetailFields(String knownDetailType, Stage resultingStage);

	/**
	 * Initialize fields of an accumulator based on an initial value.  An example of this is setting a minimum
	 * value because a minimum cannot be initialized without at least one value.
	 * @param stage The stage of accumulation
	 * @param value The value to be accumulated
	 * @return the accumulator with metrics reflecting the first value
	 * @throws MainTypeException thrown if the first value cannot be used to initialize the accumulator
	 */
	protected abstract AbstractProfileAccumulator<T> initializeFirstValue(Stage stage, T value) 
			throws MainTypeException;

	/**
	 * Initialize values necessary for second pass accumulation.  For example, defined number bucket ranges.
	 * @param profile The existing profile
	 * @return The initialized accumulator
	 * @throws MainTypeException thrown if the initialization cannot be completed
	 */
	protected abstract AbstractProfileAccumulator<T> initializeForSecondPassAccumulation(Profile profile) 
			throws MainTypeException;

	/**
	 * Initialize the accumulator for schema analysis.  Subclasses should initialize based on existing schema profiles
	 * and any metrics in samples that are needed.  For example, determining the number histogram based on
	 * unique valued histograms in both schema and samples.
	 * @param schemaProfile the existing schema profile, or null if there is no existing schema
	 * @param recordsInSchema the number of records in the exsitings schema
	 * @param sampleProfiles the sample profiles that are merged together or into the existing schema
	 * @return the instance of the accumulator, initialized
	 * @throws MainTypeException should be thrown if there is no schema or sample profile to initialize the accumulator
	 */
	protected abstract AbstractProfileAccumulator<T> initializeForSchemaAccumulation(
			Profile schemaProfile, int recordsInSchema, List<Profile> sampleProfiles) throws MainTypeException;

	/**
	 * Accumulate a value for a certain stage
	 * @param accumulationStage the current accumulation stage
	 * @param value the value
	 * @return the profile reflecting the addition of values
	 * @throws MainTypeException thrown if there is a type error with the value
	 */
	protected abstract void accumulate(Stage accumulationStage, T value) throws MainTypeException;

	/**
	 * Finish the metrics accumulation and return the resulting profile.  Some fields rely on this method being explicitly
	 * call to have a value. For example, the average is not calculated until this method is called.  The sum and
	 * number of records are accumulated, and average is only set when finish() is called.
	 * and walking count.
	 * @param accumulationStage the stage that is being finished
	 * @return The profile, with all applicable fields set based on the accumulated values
	 */
	protected abstract Profile finish(Stage accumulationStage);

	/**
	 * Turn an object or null into the appropriate generic.  This method should return null if nothing should be done 
	 * with the value.  
	 * @param object the object that should be converted to T, or null
	 * @return T to be processed, or null if nothing else should be done with this value 
	 * @throws MainTypeException thrown if the value cannot be converted into T and an error should be logged
	 */
	protected abstract T createAppropriateObject(Object object) throws MainTypeException;
}
