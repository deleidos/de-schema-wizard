package com.deleidos.dp.metrics;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.math.NumberUtils;
import org.junit.Test;

public class CalculationsFacadeTest {
	private boolean exponentsReady = true;

	@Test
	public void testHyphen() {
		assertFalse(NumberUtils.isNumber("4-5"));
	}
	
	@Test
	public void testExponent() {
		boolean exponent = NumberUtils.isNumber("4E5");
		assertTrue(exponentsReady==exponent);
	}
}
