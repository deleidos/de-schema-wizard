package com.deleidos.dp.accumulator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.deleidos.dp.beans.NumberDetail;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.calculations.MetricsCalculationsFacade;
import com.deleidos.dp.calculations.TypeDetermination;
import com.deleidos.dp.enums.DetailType;
import com.deleidos.dp.enums.MainType;
import com.deleidos.dp.exceptions.MainTypeException;
import com.deleidos.dp.histogram.AbstractNumberBucketList;

/**
 * Accumulator for number profiles.
 * @author leegc
 *
 */
public class NumberProfileAccumulator extends AbstractProfileAccumulator<Number> {
	private static Logger logger = Logger.getLogger(NumberProfileAccumulator.class);
	protected AbstractNumberBucketList numberHistogram;

	protected NumberProfileAccumulator(String key) {
		super(key, MainType.NUMBER);
	}
	
	@Override
	protected void initializeDetailFields(String knownDetailType, Stage resultingStage) {
		setNumberDetail(new NumberDetail());
		if(knownDetailType != null) {
			getNumberDetail().setDetailType(knownDetailType);
		}
		getNumberDetail().setWalkingSum(BigDecimal.ZERO);
		getNumberDetail().setWalkingCount(BigDecimal.ZERO);
		getNumberDetail().setWalkingSquareSum(BigDecimal.ZERO);
	}

	public NumberDetail getNumberDetail() {
		return Profile.getNumberDetail(profile);
	}

	protected void setNumberDetail(NumberDetail numberDetail) {
		profile.setDetail(numberDetail);
		accumulateHashes = false;
	}

	public void accumulateDetailType(Object value) {
		detailTypeTracker[TypeDetermination.determineNumberDetailType(value).getIndex()]++;
	}

	public void accumulateMin(BigDecimal value) {
		if(value.compareTo(getNumberDetail().getMin()) == -1) getNumberDetail().setMin(value);
	}

	public void accumulateMax(BigDecimal value) {
		if(value.compareTo(getNumberDetail().getMax()) == 1) getNumberDetail().setMax(value);
	}

	public void accumulateWalkingFields(BigDecimal value) {
		getNumberDetail().setWalkingCount(getNumberDetail().getWalkingCount().add(BigDecimal.ONE));
		getNumberDetail().setWalkingSum(getNumberDetail().getWalkingSum().add(value));
		BigDecimal adderSquare = value.pow(2, MetricsCalculationsFacade.DEFAULT_CONTEXT);
		getNumberDetail().setWalkingSquareSum(getNumberDetail().getWalkingSquareSum().add(adderSquare));
	}

	public void accumulateBucketCount(Object value) {
		if (!numberHistogram.putValue(value)) {
			logger.warn("Did not successfully put " + value + " into histogram.");
		}
	}

	@Override
	protected NumberProfileAccumulator initializeForSecondPassAccumulation(Profile profile) {
		//only things needed is histogram stuff
		numberHistogram = AbstractNumberBucketList.newNumberBucketList(getNumberDetail());
		return this;
	}

	@Override
	protected NumberProfileAccumulator initializeForSchemaAccumulation(Profile schemaProfile, int schemaRecords, List<Profile> sampleProfiles) throws MainTypeException {
		NumberDetail schemaNumberDetail = (schemaProfile != null) ? Profile.getNumberDetail(schemaProfile) : null;
		List<NumberDetail> sampleNumberDetails = new ArrayList<NumberDetail>();
		for(Profile sampleProfile : sampleProfiles) {
			NumberDetail numberDetail = Profile.getNumberDetail(sampleProfile);
			sampleNumberDetails.add(numberDetail);
		}
		numberHistogram = AbstractNumberBucketList.newNumberBucketList(
				schemaNumberDetail, sampleNumberDetails);
		return this;
	}

	@Override
	protected Profile finish(Stage accumulationStage) {

		if(accumulationStage.equals(Stage.SAMPLE_FIRST_PASS) 
				|| accumulationStage.equals(Stage.SCHEMA_PASS)) {
			BigDecimal average = getNumberDetail().getWalkingSum().divide(getNumberDetail().getWalkingCount(), MetricsCalculationsFacade.DEFAULT_CONTEXT);

			getNumberDetail().setAverage(average);
			BigDecimal twiceAverage = average.multiply(BigDecimal.valueOf(2), DEFAULT_CONTEXT);
			BigDecimal summations = getNumberDetail().getWalkingSquareSum().subtract(twiceAverage.multiply(getNumberDetail().getWalkingSum(), DEFAULT_CONTEXT), DEFAULT_CONTEXT);
			BigDecimal finalNumerator = summations.add(getNumberDetail().getWalkingCount().multiply(average.pow(2), DEFAULT_CONTEXT), DEFAULT_CONTEXT);
			BigDecimal withDivision = finalNumerator.divide(getNumberDetail().getWalkingCount(), DEFAULT_CONTEXT);
			double stdDev = Math.sqrt(withDivision.doubleValue());
			getNumberDetail().setStdDev(stdDev);
			
		}
		
		if(getAccumulationStage().equals(Stage.SAMPLE_SECOND_PASS) || 
				getAccumulationStage().equals(Stage.SCHEMA_PASS)) {
			getNumberDetail().setFreqHistogram(this.numberHistogram.finish());
		}
		return profile;
	}

	@Override
	protected NumberProfileAccumulator initializeFirstValue(Stage stage, Number value)
			throws MainTypeException {
		BigDecimal bigDecimalValue = BigDecimal.valueOf(value.doubleValue());
		getNumberDetail().setMin(bigDecimalValue);
		getNumberDetail().setMax(bigDecimalValue);
		getNumberDetail().setAverage(bigDecimalValue);
		getNumberDetail().setStdDev(0);
		accumulateHashes = true;
		return this;
	}

	@Override
	protected void accumulate(Stage accumulationStage, Number value) throws MainTypeException {
		BigDecimal bigDecimalValue = BigDecimal.valueOf(value.doubleValue());
		switch(accumulationStage) {
		case UNINITIALIZED: throw new MainTypeException("Accumulator called but has not been initialized.");
		case SAMPLE_AWAITING_FIRST_VALUE: { 
			break;
		}
		case SCHEMA_AWAITING_FIRST_VALUE: {
			break;
		}
		case SAMPLE_FIRST_PASS: {
			accumulateMin(bigDecimalValue);
			accumulateMax(bigDecimalValue);
			accumulateWalkingFields(bigDecimalValue);
			accumulateDetailType(bigDecimalValue);
			accumulateNumDistinctValues(value);
			break;
		}
		case SAMPLE_SECOND_PASS: {
			accumulateBucketCount(value);
			break;
		}
		case SCHEMA_PASS: {
			accumulateMin(bigDecimalValue);
			accumulateMax(bigDecimalValue);
			accumulateWalkingFields(bigDecimalValue);
			accumulateNumDistinctValues(value);
			accumulateBucketCount(bigDecimalValue);
			break;
		}
		default: throw new MainTypeException("Accumulator stuck in unknown stage.");
		}
	}

	@Override
	protected Number createAppropriateObject(Object object) throws MainTypeException {
		return MainType.NUMBER.createNumber(object);
	}

	@Override
	protected DetailType determineDetailType(Profile existingSchemaProfile, List<Profile> sampleProfiles) {
		if (existingSchemaProfile != null) {
			return existingSchemaProfile.getDetail().getDetailTypeClass();
		} else {
			return sampleProfiles.get(0).getDetail().getDetailTypeClass();
		}
	}
}
