package com.deleidos.dp.accumulator;

import java.util.List;
import java.util.Map;

import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.enums.DetailType;
import com.deleidos.dp.enums.MainType;
import com.deleidos.dp.exceptions.MainTypeException;

public class ObjectProfileAccumulator extends AbstractProfileAccumulator<Map<String, Object>> {
	private Map<String, AbstractProfileAccumulator<?>> children;
	
	protected ObjectProfileAccumulator(String key, MainType mainType) {
		super(key, mainType);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected DetailType determineDetailType(Profile existingSchemaProfile, List<Profile> sampleProfiles) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void initializeDetailFields(String knownDetailType,
			com.deleidos.dp.accumulator.AbstractProfileAccumulator.Stage resultingStage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected AbstractProfileAccumulator<Map<String, Object>> initializeFirstValue(
			com.deleidos.dp.accumulator.AbstractProfileAccumulator.Stage stage, Map<String, Object> value)
			throws MainTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractProfileAccumulator<Map<String, Object>> initializeForSecondPassAccumulation(Profile profile)
			throws MainTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractProfileAccumulator<Map<String, Object>> initializeForSchemaAccumulation(Profile schemaProfile,
			int recordsInSchema, List<Profile> sampleProfiles) throws MainTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void accumulate(com.deleidos.dp.accumulator.AbstractProfileAccumulator.Stage accumulationStage,
			Map<String, Object> value) throws MainTypeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Profile finish(com.deleidos.dp.accumulator.AbstractProfileAccumulator.Stage accumulationStage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, Object> createAppropriateObject(Object object) throws MainTypeException {
		// TODO Auto-generated method stub
		return null;
	}

}
