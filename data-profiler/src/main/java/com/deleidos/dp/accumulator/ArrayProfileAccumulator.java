package com.deleidos.dp.accumulator;

import java.util.List;

import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.exceptions.MainTypeException;
import com.deleidos.hd.enums.DetailType;
import com.deleidos.hd.enums.MainType;

public class ArrayProfileAccumulator extends AbstractProfileAccumulator<List<Object>> {
	private List<AbstractProfileAccumulator<?>> children;

	protected ArrayProfileAccumulator(String key, MainType mainType) {
		super(key, mainType);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void initializeDetailFields(String knownDetailType,
			com.deleidos.dp.accumulator.AbstractProfileAccumulator.Stage resultingStage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected AbstractProfileAccumulator<List<Object>> initializeFirstValue(
			com.deleidos.dp.accumulator.AbstractProfileAccumulator.Stage stage, List<Object> value)
			throws MainTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractProfileAccumulator<List<Object>> initializeForSecondPassAccumulation(Profile profile)
			throws MainTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected AbstractProfileAccumulator<List<Object>> initializeForSchemaAccumulation(Profile schemaProfile,
			int recordsInSchema, List<Profile> sampleProfiles) throws MainTypeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void accumulate(com.deleidos.dp.accumulator.AbstractProfileAccumulator.Stage accumulationStage,
			List<Object> value) throws MainTypeException {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Profile finish(com.deleidos.dp.accumulator.AbstractProfileAccumulator.Stage accumulationStage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Object> createAppropriateObject(Object object) throws MainTypeException {
		// TODO Auto-generated method stub
		return null;
	}

}
