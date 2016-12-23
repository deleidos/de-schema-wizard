package com.deleidos.dp.profiler;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.calculations.MatchingAlgorithm;
import com.deleidos.dp.calculations.MetricsCalculationsFacade;
import com.deleidos.dp.deserializors.SerializationUtility;
import com.deleidos.dp.environ.DPMockUpEnvironmentTest;
import com.deleidos.dp.environ.TestUtils;
import com.deleidos.dp.environ.TestUtils.RecordGeneratorFunction;
import com.deleidos.dp.environ.TestUtils.SampleIngestionUtility;
import com.deleidos.dp.exceptions.H2DataAccessException;
import com.deleidos.dp.profiler.api.ProfilerRecord;

public class SchemaProfilerInitializationTest extends DPMockUpEnvironmentTest {
	public static final Logger logger = Logger.getLogger(SchemaProfilerInitializationTest.class);
	
	@Test
	public void test() throws H2DataAccessException {
		SampleIngestionUtility sampleIngest = new SampleIngestionUtility();
		sampleIngest.addSampleIngestion(100, i->{
			DefaultProfilerRecord d = new DefaultProfilerRecord();
			d.put("constant", "CONST");
			d.put("var", i);
			return d;
		});
		List<DataSample> samples = TestUtils.processSamples(sampleIngest, false);
		Schema schema = TestUtils.schemaPass(null, samples, sampleIngest);
		logger.info(SerializationUtility.serialize(schema));
	}
	
	@Test
	public void testDoStructuresMatch() throws H2DataAccessException {
		SampleIngestionUtility ingestUtil = new SampleIngestionUtility();
		ingestUtil.addSampleIngestion(100, new StructuredRecordGenerator());
		ingestUtil.addSampleIngestion(100, new StructuredRecordGenerator2());
		List<DataSample> samples = TestUtils.processSamples(ingestUtil, true);
		assertTrue(MatchingAlgorithm.doStructuresMatch(
				"root.nested-obj.nested1", 
				"a-root.a-nested-obj.nested1", 
				samples.get(0).getDsProfile(), samples.get(1).getDsProfile()));
		double result = MatchingAlgorithm.structureMatch(
				"root.nested-obj.nested1", 
				"a-root.a-nested-obj.nested1", 
				samples.get(0).getDsProfile(), samples.get(1).getDsProfile());
		assertTrue(result > .90f && result < 1);
		result = MatchingAlgorithm.structureMatch(
				"root.nested-const", 
				"a-root.a-nested-const", 
				samples.get(0).getDsProfile(), samples.get(1).getDsProfile());
		assertTrue(result > .80f && result < .90f);
	}
	
	class StructuredRecordGenerator implements RecordGeneratorFunction {

		@Override
		public ProfilerRecord apply(int value) {
			DefaultProfilerRecord defaultProfilerRecord = new DefaultProfilerRecord();
			DefaultProfilerRecord nestedProfilerRecord = new DefaultProfilerRecord();
			DefaultProfilerRecord anotherNestedProfilerRecord = new DefaultProfilerRecord();
			DefaultProfilerRecord anotherNestedProfilerRecord2 = new DefaultProfilerRecord();
			anotherNestedProfilerRecord.put("nested1", value);
			anotherNestedProfilerRecord.put("nested2", value+1);
			anotherNestedProfilerRecord2.put("nested3", value+2);
			nestedProfilerRecord.put("nested-obj", anotherNestedProfilerRecord);
			nestedProfilerRecord.put("nested-obj2", anotherNestedProfilerRecord2);
			nestedProfilerRecord.put("nested-const", 1);
			defaultProfilerRecord.put("root", nestedProfilerRecord);
			return defaultProfilerRecord;
		}
		
	}
	
	class StructuredRecordGenerator2 implements RecordGeneratorFunction {

		@Override
		public ProfilerRecord apply(int value) {
			DefaultProfilerRecord defaultProfilerRecord = new DefaultProfilerRecord();
			DefaultProfilerRecord nestedProfilerRecord = new DefaultProfilerRecord();
			DefaultProfilerRecord anotherNestedProfilerRecord = new DefaultProfilerRecord();
			DefaultProfilerRecord anotherNestedProfilerRecord2 = new DefaultProfilerRecord();
			anotherNestedProfilerRecord.put("nested1", value);
			anotherNestedProfilerRecord.put("nested2", value+1);
			anotherNestedProfilerRecord2.put("nested3", value+2);
			nestedProfilerRecord.put("a-nested-obj", anotherNestedProfilerRecord);
			nestedProfilerRecord.put("a-nested-obj2", anotherNestedProfilerRecord2);
			nestedProfilerRecord.put("a-nested-const", 1);
			defaultProfilerRecord.put("a-root", nestedProfilerRecord);
			return defaultProfilerRecord;
		}
		
	}
}
