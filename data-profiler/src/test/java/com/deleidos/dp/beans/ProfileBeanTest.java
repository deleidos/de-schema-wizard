package com.deleidos.dp.beans;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.deleidos.dp.accumulator.BundleProfileAccumulator;
import com.deleidos.dp.deserializors.SerializationUtility;
import com.deleidos.dp.exceptions.MainTypeException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ProfileBeanTest {
	private Logger logger = Logger.getLogger(ProfileBeanTest.class);

	@Test
	public void testOutputProfileNumber() throws JsonProcessingException, MainTypeException {
		List<Object> values = new ArrayList<Object>();
		values.add(15);
		//NumberProfileAccumulator nma = new NumberProfileAccumulator("testField", 15);
		values.add(15);
		values.add(20);
		values.add(11);
		values.add(80);
		Profile profile = BundleProfileAccumulator.generateProfile("testField", values);

		String s = SerializationUtility.serialize(profile);
		logger.debug(s);
		assertTrue(s != null);
		logger.info("Number profile serialized.");

	}

	@Test
	public void testOutputProfileStrings() throws JsonProcessingException, MainTypeException {
		List<Object> values = new ArrayList<Object>();
		//StringProfileAccumulator sma = new StringProfileAccumulator("test-field", "hey");
		values.add("hey");
		values.add("hey");
		values.add("hi");
		values.add("hello");
		values.add("what's up");

		Profile profile = BundleProfileAccumulator.generateProfile("testField", values);		
		String s = null;

		s = SerializationUtility.serialize(profile);
		logger.debug(s);


		assertTrue(s != null);
		logger.info("String profile serialized.");
	}
	
	@Test
	public void testDeserializeInterpretations() throws MainTypeException {
		List<Object> values = new ArrayList<Object>();
		//StringProfileAccumulator sma = new StringProfileAccumulator("test-field", "hey");
		values.add("hey");
		values.add("hey");
		values.add("hi");
		values.add("hello");
		values.add("what's up");

		Profile profile = BundleProfileAccumulator.generateProfile("testField", values);
		Interpretation i1 = new Interpretation();
		i1.setiName("testI1");
		Interpretation i2 = new Interpretation();
		i2.setiName("testI2");
		profile.setInterpretations(Interpretations.newInstance(i1,i2));
		String serialized = SerializationUtility.serialize(profile);
		logger.info(serialized);
		Profile deserialized = SerializationUtility.deserialize(serialized, Profile.class);
		assertTrue(deserialized.getInterpretations().containsInterpretation(Interpretation.UNKNOWN));
		assertTrue(deserialized.getInterpretations().getAvailableOptions().size() == 3);
	}
}
