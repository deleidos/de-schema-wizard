package com.deleidos.dp.h2;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.deleidos.dp.environ.TestUtils;
import com.deleidos.dp.environ.TestUtils.SampleIngestionUtility;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.exceptions.H2DataAccessException;
import com.deleidos.dp.integration.DataProfilerIntegrationEnvironment;

public class H2DataAccessIT extends DataProfilerIntegrationEnvironment {
	public static final Logger logger = Logger.getLogger(H2DataAccessIT.class);

	@Test
	public void testStartup() throws SQLException, DataAccessException {
		for(int i = 0; i < 100; i++) {
			if (!H2DataAccessObject.getInstance().testDefaultConnection()) {
				fail("Connection test failed.");
			}
		}
	}
	
	@Test
	public void testParallism() throws H2DataAccessException {
		SampleIngestionUtility ingestUtil = new SampleIngestionUtility();
		ingestUtil.addSampleIngestion(100, new TestUtils.SimpleRecordGenerator());
		ingestUtil.addSampleIngestion(100, new TestUtils.SimpleRecordGenerator());
		ingestUtil.addSampleIngestion(100, new TestUtils.SimpleRecordGenerator());
		String[] guids = TestUtils.processSamples(ingestUtil, true).stream()
								.map(sample -> sample.getDsGuid())
								.collect(Collectors.toList())
								.toArray(new String[ingestUtil.size()]);
		
		long t1 = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			H2DataAccessObject.getInstance().slowerGetSamplesByGuids(guids);
		}
		long nonOptimized = System.currentTimeMillis() - t1;
		logger.info("Non optimized time: " + nonOptimized);
		
		t1 = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			H2DataAccessObject.getInstance().getSamplesByGuids(guids);
		}
		long optimized = System.currentTimeMillis() - t1;
		logger.info("Optimized time: " + optimized);
		assertTrue(optimized < nonOptimized);
	}

}
