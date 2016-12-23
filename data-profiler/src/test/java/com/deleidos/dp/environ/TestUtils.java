package com.deleidos.dp.environ;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.log4j.Logger;

import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.enums.Tolerance;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.exceptions.H2DataAccessException;
import com.deleidos.dp.h2.H2DataAccessObject;
import com.deleidos.dp.interpretation.InterpretationEngineFacade;
import com.deleidos.dp.profiler.DefaultProfilerRecord;
import com.deleidos.dp.profiler.SampleProfiler;
import com.deleidos.dp.profiler.SampleSecondPassProfiler;
import com.deleidos.dp.profiler.SchemaProfiler;
import com.deleidos.dp.profiler.api.Profiler;
import com.deleidos.dp.profiler.api.ProfilerRecord;

public class TestUtils {
	private static final Logger logger = Logger.getLogger(TestUtils.class);

	public static Schema schemaPass(Schema existingSchema, List<DataSample> samples, 
			SampleIngestionUtility sampleIngest) {
		return schemaPass(existingSchema, samples, sampleIngest, new MergeAllFilter());
	}
	
	public static Schema schemaPass(Schema existingSchema, List<DataSample> samples, 
			SampleIngestionUtility sampleIngest, Predicate<Profile> mergeFilter) {
		if (existingSchema == null) {
			samples.get(0).getDsProfile().forEach((k,v)->v.setUsedInSchema(true));
		}
		if (mergeFilter == null) {
			mergeFilter = new MergeAllFilter();
		}
		
		samples
			.stream()
			.map(DataSample::getDsProfile)
			.flatMap(map->map.values().stream())
			.filter(mergeFilter)
			.forEach(profile -> profile.setMergedInto(true));
		
		SchemaProfiler schemaProfiler = new SchemaProfiler(existingSchema, samples);
		
		IntStream.range(0, sampleIngest.size())
			.forEachOrdered(sampleIndex -> {
				schemaProfiler.setCurrentDataSampleGuid(samples.get(sampleIndex).getDsGuid());
				loadSomeRecords(schemaProfiler, sampleIngest.get(sampleIndex));
			});
		
		Schema schema = schemaProfiler.finish();
		schema.setsGuid(UUID.randomUUID().toString());
		return schema;
	}

	public static List<DataSample> processSamples(SampleIngestionUtility sampleIngest, boolean persist) throws H2DataAccessException {
		List<DataSample> samples =  IntStream
				.range(0, sampleIngest.size())
				.mapToObj(sampleIngest::get)
				.map(TestUtils::generateSample)
				.collect(Collectors.toList());
		if (persist) {
			List<String> guids = new ArrayList<String>();
			for (DataSample sample : samples) {
				sample.setDsName("sample-"+String.valueOf(System.currentTimeMillis()));
				guids.add(H2DataAccessObject.getInstance().addSample(sample));
			}
			samples = new ArrayList<DataSample>();
			for (String guid : guids) {
				samples.add(H2DataAccessObject.getInstance().getSampleByGuid(guid));
			}
		}
		return samples;
	}

	private static DataSample generateSample(AbstractMap.SimpleEntry<Integer, RecordGeneratorFunction> tuple) {
		SampleProfiler sampleProfiler = new SampleProfiler(Tolerance.STRICT);
		loadSomeRecords(sampleProfiler, tuple);
		DataSample sample = sampleProfiler.finish();
		sample.setDsGuid(UUID.randomUUID().toString());
		
		try {
			InterpretationEngineFacade.interpretInline(sample, "Transportation", null);
		} catch (DataAccessException e) {
			logger.error(e);
		}

		SampleSecondPassProfiler srgProfiler = new SampleSecondPassProfiler(sample);
		srgProfiler.setMinimumBatchSize(500);
		loadSomeRecords(srgProfiler, tuple);
		return srgProfiler.finish();
	}
	
	public static void loadSomeRecords(Profiler<?> profiler, 
			AbstractMap.SimpleEntry<Integer, RecordGeneratorFunction> tuple) {
		loadSomeRecords(profiler, tuple.getKey(), tuple.getValue());
	}

	public static void loadSomeRecords(Profiler<?> profiler, int numRecords,
			RecordGeneratorFunction loaderFunction) {
		IntStream
				.range(0, numRecords)
				.mapToObj(loaderFunction)
				.forEach(profiler::accumulate);
		if (loaderFunction instanceof RandomizedRecordGenerator) {
			((RandomizedRecordGenerator)loaderFunction).reInitialize();
		}
	}
	
	public static Schema processWorkflowWithDefaultBehavior(String schemaName,
			SampleIngestionUtility ingestUtility, boolean persist) throws H2DataAccessException {
		return processWorkflowWithDefaultBehavior(schemaName, ingestUtility, persist, null);
	}
	
	public static Schema processWorkflowWithAdvancedBehavior(String schemaName,
			SampleIngestionUtility ingestUtility, boolean persist, String previousVersionGuid, 
			Predicate<Profile> mergeFilter, Predicate<Entry<String, Profile>> removeFromSchemaIf) 
					throws H2DataAccessException {
		Schema existingSchema = (previousVersionGuid == null) ? null : 
			H2DataAccessObject.getInstance().getSchemaByGuid(previousVersionGuid, true);
		
		
		Schema schema = schemaPass(existingSchema, processSamples(ingestUtility, persist), ingestUtility, mergeFilter);
		schema.setsName(schemaName);
		if (removeFromSchemaIf != null) {
			schema.getsProfile().entrySet().removeIf(removeFromSchemaIf);
		}
		if (persist) {
			schema.getsProfile().forEach((k,profile)->profile.setExampleValues(null));
			String guid = H2DataAccessObject.getInstance().addSchema(schema);
			return H2DataAccessObject.getInstance().getSchemaByGuid(guid, true);
		}
		return schema;
	}
	
	public static Schema processWorkflowWithDefaultBehavior(String schemaName,
			SampleIngestionUtility ingestUtility, boolean persist, String previousVersionGuid) 
					throws H2DataAccessException {
		return processWorkflowWithAdvancedBehavior(schemaName, ingestUtility, persist, previousVersionGuid, null, null);
	}

	@FunctionalInterface
	public interface RecordGeneratorFunction extends IntFunction<ProfilerRecord> { }
	
	public static class MergeAllFilter implements Predicate<Profile> {

		@Override
		public boolean test(Profile t) {
			return true;
		}
		
	}
	
	public static class SimpleRecordGenerator implements RecordGeneratorFunction {
		Optional<Integer> defaultValue;

		public SimpleRecordGenerator() {
			defaultValue = Optional.empty();
		}

		public SimpleRecordGenerator(Integer defaultValue) {
			this.defaultValue = Optional.of(defaultValue);
		}

		@Override
		public ProfilerRecord apply(int value) {
			DefaultProfilerRecord profilerRecord = new DefaultProfilerRecord();
			profilerRecord.put("a", this.defaultValue.orElse(value));
			profilerRecord.put("b", this.defaultValue.orElse(value));
			return profilerRecord;
		}

	}

	public static abstract class RandomizedRecordGenerator implements RecordGeneratorFunction {
		private final Random random;
		private final Long seed;

		public RandomizedRecordGenerator(Integer randomSeed) {
			this(Long.valueOf(randomSeed));
		}
		
		public RandomizedRecordGenerator(Long randomSeed) {
			this.seed = randomSeed;
			this.random = new Random(this.seed);
		}

		public void reInitialize() {
			random.setSeed(seed);
		}

		@Override
		public ProfilerRecord apply(int value) {
			return randomizedGenerate(value, random.nextDouble());
		}

		public abstract ProfilerRecord randomizedGenerate(int value, double randomValue);

	}

	public static class SampleIngestionUtility {
		private final List<Integer> numRecordsBySample;
		private final List<RecordGeneratorFunction> recordGenerators;

		public SampleIngestionUtility() {
			numRecordsBySample = new ArrayList<Integer>();
			recordGenerators = new ArrayList<RecordGeneratorFunction>();
		}

		public Integer size() {
			return numRecordsBySample.size(); 
		}

		public AbstractMap.SimpleEntry<Integer, RecordGeneratorFunction> get(int index) {
			return new AbstractMap.SimpleEntry<Integer, TestUtils.RecordGeneratorFunction>
			(numRecordsBySample.get(index), recordGenerators.get(index));
		}

		public void addSampleIngestion(Integer numRecords, RecordGeneratorFunction recordGenerator) {
			this.numRecordsBySample.add(numRecords);
			this.recordGenerators.add(recordGenerator);
		}

		protected Integer numRecords(int x) {
			return numRecordsBySample.get(x);
		}

		protected RecordGeneratorFunction generator(int x) {
			return recordGenerators.get(x);
		}

	}

}
