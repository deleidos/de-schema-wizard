package com.deleidos.dp.accumulator;

import static com.deleidos.dp.calculations.MetricsCalculationsFacade.createString;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;

import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.StringDetail;
import com.deleidos.dp.calculations.MetricsCalculationsFacade;
import com.deleidos.dp.calculations.TypeDetermination;
import com.deleidos.dp.exceptions.MainTypeException;
import com.deleidos.dp.histogram.CharacterBucketList;
import com.deleidos.dp.histogram.ShortStringBucketList;
import com.deleidos.hd.enums.DetailType;
import com.deleidos.hd.enums.MainType;

/**
 * Accumulator for string profiles.
 * @author leegc
 *
 */
public class StringProfileAccumulator extends AbstractProfileAccumulator<String> {
	private static final Logger logger = Logger.getLogger(StringProfileAccumulator.class);
	protected ShortStringBucketList shortStringBucketList;
	protected CharacterBucketList charBucketList;

	protected StringProfileAccumulator(String key) {
		super(key, MainType.STRING);
	}
	
	@Override
	protected void initializeDetailFields(String knownDetailType, Stage resultingStage) {
		setStringDetail(new StringDetail());
		if(knownDetailType != null) {
			getStringDetail().setDetailType(knownDetailType);
		}
		getStringDetail().setWalkingSum(BigDecimal.ZERO);
		getStringDetail().setWalkingCount(BigDecimal.ZERO);
		getStringDetail().setWalkingSquareSum(BigDecimal.ZERO);
	}

	public StringDetail getStringDetail() {
		return Profile.getStringDetail(profile);
	}

	protected void setStringDetail(StringDetail stringDetail) {
		profile.setDetail(stringDetail);
		accumulateHashes = false;
	}

	public void accumulateDetailType(Object value) {
		detailTypeTracker[TypeDetermination.determineStringDetailType(value).getIndex()]++;
	}

	public void accumulateMinLength(String objectString) {
		int value = objectString.length();
		if(value < getStringDetail().getMinLength()) getStringDetail().setMinLength(value);
	}

	public void accumulateMaxLength(String objectString) {
		int value = objectString.length();
		if(value > getStringDetail().getMaxLength()) getStringDetail().setMaxLength(value);
	}

	public void accumulateWalkingFields(String objectString) {
		BigDecimal lengthValue = BigDecimal.valueOf(objectString.length());
		getStringDetail().setWalkingCount(getStringDetail().getWalkingCount().add(BigDecimal.ONE));
		getStringDetail().setWalkingSum(getStringDetail().getWalkingSum().add(lengthValue));
		BigDecimal adderSquare = lengthValue.pow(2, MetricsCalculationsFacade.DEFAULT_CONTEXT);
		getStringDetail().setWalkingSquareSum(getStringDetail().getWalkingSquareSum().add(adderSquare));
	}

	public void accumulateShortStringFreqHistogram(String objectString) {
		String currentDetailType = getStringDetail().getDetailType();
		if(!currentDetailType.equals(DetailType.TEXT.toString())) {
			shortStringBucketList.putValue(objectString);
		}
	}

	@Override
	protected StringProfileAccumulator initializeForSecondPassAccumulation(Profile profile) {
		shortStringBucketList = new ShortStringBucketList();
		return this;
	}
	
	@Override
	protected StringProfileAccumulator initializeForSchemaAccumulation(Profile schemaProfile, int recordsInSchema, List<Profile> sampleProfiles) throws MainTypeException {
		shortStringBucketList = new ShortStringBucketList();
		return this;
	}

	@Override
	protected Profile finish(Stage accumulationStage) {

		if(accumulationStage.equals(Stage.SAMPLE_FIRST_PASS) 
				|| accumulationStage.equals(Stage.SCHEMA_PASS)) {
			BigDecimal average = getStringDetail().getWalkingSum()
					.divide(getStringDetail().getWalkingCount(), MetricsCalculationsFacade.DEFAULT_CONTEXT);

			getStringDetail().setAverageLength(average.doubleValue());

			BigDecimal twiceAverage = average.multiply(BigDecimal.valueOf(2), DEFAULT_CONTEXT);
			BigDecimal summations = getStringDetail().getWalkingSquareSum()
					.subtract(twiceAverage
							.multiply(getStringDetail().getWalkingSum(), DEFAULT_CONTEXT), DEFAULT_CONTEXT);
			BigDecimal finalNumerator = summations
					.add(getStringDetail().getWalkingCount()
							.multiply(average.pow(2), DEFAULT_CONTEXT), DEFAULT_CONTEXT);
			BigDecimal withDivision = finalNumerator.divide(getStringDetail().getWalkingCount(), DEFAULT_CONTEXT);
			double stdDev = Math.sqrt(withDivision.doubleValue());
			getStringDetail().setStdDevLength(stdDev);

		}
		if (accumulationStage.equals(Stage.SAMPLE_SECOND_PASS) 
				|| accumulationStage.equals(Stage.SCHEMA_PASS)) {
			getStringDetail().setTermFreqHistogram(shortStringBucketList.finish());
		}
		return profile;
	}

	@Override
	protected StringProfileAccumulator initializeFirstValue(Stage stage, String value) throws MainTypeException {
		getStringDetail().setMinLength(value.length());
		getStringDetail().setMaxLength(value.length());
		getStringDetail().setAverageLength(value.length());
		getStringDetail().setStdDevLength(0);
		accumulateHashes = true;
		return this;
	}

	@Override
	protected void accumulate(Stage accumulationStage, String value) throws MainTypeException {
		switch(accumulationStage) {
		case UNINITIALIZED: throw new MainTypeException("Accumulator called but has not been initialized.");
		case SAMPLE_AWAITING_FIRST_VALUE: { 
			break;
		}
		case SCHEMA_AWAITING_FIRST_VALUE: {
			break;
		}
		case SAMPLE_FIRST_PASS: {
			accumulateDetailType(value);
			accumulateMaxLength(value);
			accumulateMinLength(value);
			accumulateNumDistinctValues(value);
			accumulateWalkingFields(value);
			break;
		}
		case SAMPLE_SECOND_PASS: {
			accumulateShortStringFreqHistogram(value);
			break;
		}
		case SCHEMA_PASS: {
			accumulateDetailType(value);
			accumulateMaxLength(value);
			accumulateMinLength(value);
			accumulateNumDistinctValues(value);
			accumulateWalkingFields(value);
			accumulateShortStringFreqHistogram(value);
			break;
		}
		default: throw new MainTypeException("Accumulator stuck in unknown stage.");
		}
	}

	@Override
	protected String createAppropriateObject(Object object) throws MainTypeException {
		return createString(object);
	}
}
