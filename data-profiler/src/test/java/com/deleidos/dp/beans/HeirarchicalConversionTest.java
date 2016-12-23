package com.deleidos.dp.beans;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.deleidos.dp.deserializors.ConversionUtility;
import com.deleidos.dp.deserializors.SerializationUtility;
import com.deleidos.dp.enums.Tolerance;
import com.deleidos.dp.environ.DPMockUpEnvironmentTest;
import com.deleidos.dp.environ.TestUtils;
import com.deleidos.dp.environ.TestUtils.RecordGeneratorFunction;
import com.deleidos.dp.environ.TestUtils.SampleIngestionUtility;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.exceptions.H2DataAccessException;
import com.deleidos.dp.profiler.DefaultProfilerRecord;
import com.deleidos.dp.profiler.SampleProfiler;
import com.deleidos.dp.profiler.api.ProfilerRecord;

public class HeirarchicalConversionTest extends DPMockUpEnvironmentTest {
	private static final Logger logger = Logger.getLogger(HeirarchicalConversionTest.class);

	private static Object randomVal() {
		List<Object> values = Arrays.asList("a","b","c","d", 1, 2, 3, 4);
		return values.get((int)(Math.random()*values.size()));
	}
	
	private static String randomKey() {
		String letters = "abcdefghijklmnopqrstuvwxyz";
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < Math.random()*10; i++) {
			sb.append(letters.charAt(((int)(Math.random()*letters.length()))));
		}
		return sb.toString();
	}
	
	@Test
	public void testSortLambdaImprovement() throws H2DataAccessException {
		SampleIngestionUtility ingestUtility = new SampleIngestionUtility();
		ingestUtility.addSampleIngestion(10, new HierarchicalRecordGenerator());
		Map<String, Profile> profiles = TestUtils.processSamples(ingestUtility, false).get(0).getDsProfile();
		profiles = ConversionUtility.addObjectProfiles(profiles);
		List<String> a = 
				ConversionUtility.sortChildren(
						profiles, 
						profiles.keySet().stream()
							.filter(ConversionUtility.ROOT_LEVEL_KEYS_PREDICATE)
							.collect(Collectors.toList()),	
						ConversionUtility.SORT_STRATEGY);
		List<String> b = 
				ConversionUtility.oldSortChildren(
						profiles, 
						profiles.keySet().stream()
							.filter(ConversionUtility.ROOT_LEVEL_KEYS_PREDICATE)
							.collect(Collectors.toList()),	
						ConversionUtility.SORT_STRATEGY);
		assertTrue(a.equals(b));
	}
	
	@Test
	public void testGetRootKeysLambdaImprovement() throws H2DataAccessException {
		SampleIngestionUtility ingestUtility = new SampleIngestionUtility();
		ingestUtility.addSampleIngestion(10, new TestUtils.SimpleRecordGenerator());
		Map<String, Profile> profiles = TestUtils.processSamples(ingestUtility, false).get(0).getDsProfile();
		assertTrue(
				ConversionUtility.getRootKeys(profiles)
				.equals(ConversionUtility.oldGetRootKeys(profiles)));
	}
	
	@Test
	public void testLambdaImprovement() throws H2DataAccessException {
		SampleIngestionUtility ingestUtility = new SampleIngestionUtility();
		ingestUtility.addSampleIngestion(10, new TestUtils.SimpleRecordGenerator());
		Map<String, Profile> profiles = TestUtils.processSamples(ingestUtility, false).get(0).getDsProfile();
		assertTrue(
				ConversionUtility.addObjectProfiles(profiles)
				.equals(ConversionUtility.oldAddObjectProfiles(profiles, true)));
	}
	
	@Test
	public void a() {
		List<String> testList = ConversionUtility.generateParentKeys("hello.there.who.are.you");
		assertTrue(testList.contains("hello"));
		assertTrue(testList.contains("hello.there"));
		assertTrue(testList.contains("hello.there.who"));
		assertTrue(testList.contains("hello.there.who.are"));
	}
	
	@Test
	public void generateParentKeyTest() {
		String[] parentKeys = {"hello","there","who","are","you"};
		String ans = ConversionUtility.generateParentKey(parentKeys, 1);
		try {
			assertTrue(new String("hello"+DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER+"there")
				.equals(ans));
		} catch (AssertionError e) {
			logger.error("Parent key is " + ans);
			fail();
		}
	}

	@Test
	public void testFlatToHeirarchical() throws DataAccessException {
		List<ProfilerRecord> records = new ArrayList<ProfilerRecord>();
		List<String> someStrings = Arrays.asList("root", "depth1", "depth2", "depth3", "nestedName");
		for(int i = 0; i < 100; i++) {
			DefaultProfilerRecord record = new DefaultProfilerRecord();
			Map<String, Object> heirarchicalMap = new HashMap<String, Object>();
			Map<String, Object> nestedMap1 = new HashMap<String, Object>();
			nestedMap1.put(someStrings.get(2), randomVal());
			nestedMap1.put(someStrings.get(4), randomVal());
			for(int j = 0; j < 3; j++) {
				nestedMap1.put(randomKey(), randomVal());
			}
			Map<String, Object> nestedMap2 = new HashMap<String, Object>();
			nestedMap2.put(someStrings.get(3), randomVal());
			nestedMap2.put(someStrings.get(4), randomVal());
			for(int j = 0; j < 3; j++) {
				nestedMap2.put(randomKey(), randomVal());
			}

			heirarchicalMap.put(someStrings.get(1), nestedMap1);
			heirarchicalMap.put(someStrings.get(1)+"a", nestedMap2);

			record.put("root-val", randomVal());
			record.put(someStrings.get(0), heirarchicalMap);
			records.add(record);
		}
		DataSample sample = SampleProfiler.generateDataSampleFromProfilerRecords("Transportation", Tolerance.STRICT, records);
		try {
			assertTrue(sample.isDsContainsStructuredData());
		} catch (AssertionError e) {
			logger.error("Contains structured data flag not set.");
			throw e;
		}
		List <StructuredNode> heirarchicalProfiles = sample.getDsStructuredProfile();
		logger.debug(SerializationUtility.serialize(heirarchicalProfiles));
		//Map<String, Profile> backwardsConversion = ConversionUtility.convertToFlattenedMap(heirarchicalProfiles);
		//logger.debug(SerializationUtility.serialize(backwardsConversion));

		for(StructuredNode node : heirarchicalProfiles) {
			String key = node.getField();
			try {
				assertTrue(!key.contains(DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER));
				if(key.equals("root")) {
					assertTrue(node.getChildren().size() > 0);
				}
				assertTrue(verifyOrderedChildren(node));
			} catch (AssertionError e) {
				logger.error("Found structured object appender: " + key + ".");
				throw e;
			}
		}
	}

	private boolean verifyOrderedChildren(StructuredNode structuredNode) {
		final int LEAF = 0;
		final int TREE = 1;
		int currentType = (structuredNode.getChildren().size() > 0) ? TREE : LEAF;
		logger.debug((currentType == LEAF) ? "leaf" : "tree");
		if(currentType == LEAF) {
			return true;
		} else {
			int numDifTypeSwitches = 0;
			int previousType = (structuredNode.getChildren().size() > 0) ? TREE : LEAF;
			for(StructuredNode child : structuredNode.getChildren()) {
				int type = (child.getChildren().size() > 0) ? TREE : LEAF;
				if(verifyOrderedChildren(child)) {
					if(type != previousType) {
						numDifTypeSwitches++;
						if(numDifTypeSwitches > 1) {
							logger.info("Children of " + child.getField() + " were not sorted.");
							return false;
						}
					} 
					previousType = type;
				} else {
					return false;
				}
			}
			return true;
		}
	}
	
	public static class HierarchicalRecordGenerator implements RecordGeneratorFunction {
		Optional<Integer> defaultValue;

		public HierarchicalRecordGenerator() {
			defaultValue = Optional.empty();
		}

		public HierarchicalRecordGenerator(Integer defaultValue) {
			this.defaultValue = Optional.of(defaultValue);
		}

		@Override
		public ProfilerRecord apply(int value) {
			DefaultProfilerRecord profilerRecord = new DefaultProfilerRecord();
			DefaultProfilerRecord root = new DefaultProfilerRecord();
			profilerRecord.put("a", this.defaultValue.orElse(value));
			profilerRecord.put("b", this.defaultValue.orElse(value));
			root.put("nested", profilerRecord);
			root.put("const", "C");
			return root;
		}

	}

}
