package com.deleidos.dp.profiler;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.deserializors.SerializationUtility;
import com.deleidos.dp.enums.Tolerance;
import com.deleidos.dp.environ.DPMockUpEnvironmentTest;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.exceptions.MainTypeException;
import com.deleidos.dp.exceptions.MainTypeRuntimeException;
import com.deleidos.dp.profiler.api.ProfilerRecord;

/**
 * Test for samples that have fields with the same name but different data types.
 * @author leegc
 *
 */
public class ConflictingDataTypesSameNameMergeTest extends DPMockUpEnvironmentTest {
	private static final Logger logger = Logger.getLogger(ConflictingDataTypesSameNameMergeTest.class);

	/**
	 * The front end automatically merges fields with the same name.  Need to make sure this doesn't break
	 * everything.
	 * @throws DataAccessException 
	 */
	@Test
	public void sameNameDifferentDataTypeMerge() throws DataAccessException {
		String fieldName = "whatami";
		List<ProfilerRecord> records1 = someStringRecords(fieldName);
		List<ProfilerRecord> records2 = someNumberRecords(fieldName);

		DataSample sample1 = SampleProfiler.generateDataSampleFromProfilerRecords(
				"Transportation", Tolerance.STRICT, records1);
		sample1.getDsProfile().get(fieldName).setUsedInSchema(true);
		DataSample sample2 = SampleProfiler.generateDataSampleFromProfilerRecords(
				"Transportation", Tolerance.STRICT, records2);
		sample2.getDsProfile().get(fieldName).setMergedInto(true);

		logger.info(SerializationUtility.serialize(sample1));
		logger.info(SerializationUtility.serialize(sample2));

		List<DataSample> samples = Arrays.asList(sample1, sample2);
		Map<String, List<ProfilerRecord>> sampleToRecordsMapping = new HashMap<String, List<ProfilerRecord>>();
		sampleToRecordsMapping.put(sample1.getDsGuid(), records1);
		sampleToRecordsMapping.put(sample2.getDsGuid(), records2);

		try {
			SchemaProfiler.generateSchema(samples, sampleToRecordsMapping);
		} catch (MainTypeRuntimeException e) {
			// just making sure a MainTypeRuntimeException is not thrown
			logger.error(e);
			fail();
		}
	}

	private List<ProfilerRecord> someNumberRecords(String fieldName) {
		List<ProfilerRecord> records = new ArrayList<ProfilerRecord>();
		List<Integer> nums = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
		for (int i = 0; i < 10; i++) {
			DefaultProfilerRecord defaultRecord = new DefaultProfilerRecord();
			defaultRecord.put(fieldName, nums.get((int)(Math.random()*nums.size())));
			records.add(defaultRecord);
		}
		return records;
	}

	private List<ProfilerRecord> someStringRecords(String fieldName) {
		List<ProfilerRecord> records = new ArrayList<ProfilerRecord>();
		List<String> nums = Arrays.asList("one","two","three","four","five","six","seven","eight","nine","ten");
		for (int i = 0; i < 10; i++) {
			DefaultProfilerRecord defaultRecord = new DefaultProfilerRecord();
			defaultRecord.put(fieldName, nums.get((int)(Math.random()*nums.size())));
			records.add(defaultRecord);
		}
		return records;
	}



}
