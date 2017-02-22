package com.deleidos.dp.profiler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.deleidos.dp.accumulator.AbstractProfileAccumulator;
import com.deleidos.dp.beans.AliasNameDetails;
import com.deleidos.dp.beans.Attributes;
import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.beans.DataSampleMetaData;
import com.deleidos.dp.beans.Interpretation;
import com.deleidos.dp.beans.Interpretations;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.RegionData;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.calculations.MetricsCalculationsFacade;
import com.deleidos.dp.enums.GroupingBehavior;
import com.deleidos.dp.exceptions.MainTypeException;
import com.deleidos.dp.exceptions.MainTypeRuntimeException;
import com.deleidos.dp.profiler.api.AbstractProfiler;
import com.deleidos.dp.profiler.api.ProfilerRecord;
import com.deleidos.dp.profiler.api.ProfilingProgressUpdateHandler;
import com.deleidos.dp.reversegeocoding.CoordinateProfile;

/**
 * Profiler to generate a schema.  Requires at least one sample.  State based in the sense that it must process each
 *  sample one at a time.  One possible optimization is maintaining an instance of a profiler for each sample and 
 *  merging the results after accumulation.  This would allow for simultaneous processing of samples during the schema
 *  generation phase.
 * @author leegc
 *
 */
public class SchemaProfiler extends AbstractProfiler<Schema> {
	private GroupingBehavior groupingBehavior = GroupingBehavior.GROUP_ARRAY_VALUES;
	private Logger logger = Logger.getLogger(SchemaProfiler.class);
	private ProfilingProgressUpdateHandler progressUpdateListener;

	private final Map<String, AbstractProfileAccumulator<?>> fieldMapping;
	private final Map<String, Profile> staticProfiles;
	private final Map<String, List<AliasNameDetails>> aliasMapping;
	private final Map<String, RegionData> regionDataMapping;
	private Map<String, MergeData> mergeDataMapping; 
	private final List<DataSampleMetaData> dataSampleMetaDataList;
	private final Schema existingSchema;
	private String currentMergeLookupId;

	public SchemaProfiler(Schema existingSchema, List<DataSample> samples) {
		this.existingSchema = existingSchema;
		fieldMapping = new HashMap<String, AbstractProfileAccumulator<?>>();
		dataSampleMetaDataList = new ArrayList<DataSampleMetaData>();
		aliasMapping = new HashMap<String, List<AliasNameDetails>>();
		staticProfiles = new HashMap<String, Profile>();
		mergeDataMapping = new HashMap<String, MergeData>();
		regionDataMapping = new HashMap<String, RegionData>();
		init(existingSchema, samples);
	}

	public void init(Schema existingSchema, List<DataSample> samples) { 
		try {
			initExistingSchema(existingSchema);
			analyzeDataSamples(samples);
			String seedGuid = existingSchema == null ? samples.get(0).getDsGuid() : existingSchema.getsGuid();
			for(String key : fieldMapping.keySet()) {
				try {
					AbstractProfileAccumulator<?> apa = 
							initializeProfile(key, mergeDataMapping, seedGuid);
					if(apa == null) {
						// make it static, don't handle it as a normal accumulator 
						// (manually created and field exclusively in existing schema)
						// also objects
						staticProfiles.put(key, existingSchema.getsProfile().get(key));
					} else {
						fieldMapping.put(key, apa);
					}
				} catch (MainTypeException e) {
					logger.warn("Found " + key + " as non-scalar type.  Not adding.");
				}
			}
			// remove static profiles and nulls
			fieldMapping.entrySet().removeIf(entry->entry.getValue()==null||staticProfiles.containsKey(entry));
		} catch(MainTypeException e) {
			logger.error(e);
			throw new MainTypeRuntimeException("Unexpected main type error while initilizing schema.");
		}
	}

	private AbstractProfileAccumulator<?> initializeProfile(String key, Map<String, MergeData> mergeDataMapping, String seedGuid) throws MainTypeException {
		Profile existingSchemaProfile = null;
		int existingSchemaRecordCount = -1;
		List<Profile> mergedProfiles = new ArrayList<Profile>();
		for(String mergedDataKey : mergeDataMapping.keySet()) {
			if(mergeDataMapping.get(mergedDataKey).getSample() != null) {
				DataSample sample = mergeDataMapping.get(mergedDataKey).getSample();
				if(sample != null && sample.getDsProfile().containsKey(key)) {
					if(sample.getDsProfile().get(key).isUsedInSchema()
							|| sample.getDsProfile().get(key).isMergedInto()) {
						mergedProfiles.add(sample.getDsProfile().get(key));
						if (sample.getDsGuid().equals(seedGuid) && mergedProfiles.size() > 1) {
							// swap the seed profile to the first index
							// need to ensure the seed is first so profile accumulators inherit the
							// seed samples type
							Profile tmp = mergedProfiles.get(0);
							mergedProfiles.set(0, mergedProfiles.get(mergedProfiles.size() - 1));
							mergedProfiles.set(mergedProfiles.size() - 1, tmp);
						}
					}
				}
			} else {
				Schema schema = mergeDataMapping.get(mergedDataKey).getSchema();
				existingSchemaRecordCount = schema.getRecordsParsedCount();
				if(schema.getsProfile().containsKey(key)) {
					Profile profile = schema.getsProfile().get(key);
					existingSchemaProfile = schema.getsProfile().get(key);
					if(profile.getAliasNames() == null || profile.getAliasNames().isEmpty()) {
						// field is manually created, so use it as a static field
						return null;
					} 
				}
			}
		}
		AbstractProfileAccumulator<?> accumulator = AbstractProfileAccumulator.generateProfileAccumulator(
				key, existingSchemaProfile, existingSchemaRecordCount, mergedProfiles);

		return accumulator;
	}

	@Override
	public void accumulateBinaryRecord(BinaryProfilerRecord binaryRecord) {
		String key = binaryRecord.getBinaryName();
		if (fieldMapping.containsKey(key)) {
			List<Object> values = binaryRecord.normalizeRecord().get(key);
			accumulateNormalizedValues(fieldMapping.get(key), key, values);
		}
	}

	@Override
	public void accumulateRecord(ProfilerRecord record) {
		Map<String, List<Object>> normalizedRecord = record.normalizeRecord(groupingBehavior);
		for(String key : normalizedRecord.keySet()) {
			List<Object> values = normalizedRecord.get(key);

			if (getCurrentMergedFieldsMapping().containsKey(key)) {
				String mergedAccumulatorKey = getCurrentMergedFieldsMapping().get(key);
				accumulateNormalizedValues(
						fieldMapping.get(mergedAccumulatorKey), mergedAccumulatorKey, values);
			} else {
				
			}
		}
		recordsLoaded++;
	}

	private DataSample getCurrentDataSample() {
		return mergeDataMapping.get(currentMergeLookupId).getSample();
	}

	private Map<String, String> getCurrentMergedFieldsMapping() {
		return mergeDataMapping.get(currentMergeLookupId).getSampleMergedFieldsMapping();
	}

	private Schema getExistingSchema() {
		for(String key : mergeDataMapping.keySet()) {
			if(mergeDataMapping.get(key).getSchema() != null) {
				return mergeDataMapping.get(key).getSchema();
			}
		}
		return null;
	}

	private List<DataSample> getAllDataSamples() {
		List<DataSample> samples = new ArrayList<DataSample>();
		for(String key : mergeDataMapping.keySet()) {
			if(mergeDataMapping.get(key).sample != null) {
				samples.add(mergeDataMapping.get(key).getSample());
			}
		}
		return samples;
	}

	public List<DataSampleMetaData> getDataSampleMetaDataList() {
		return dataSampleMetaDataList;
	}

	private void initExistingSchema(Schema existingSchema) throws MainTypeException {
		if(existingSchema != null) {
			logger.info("Existing schema detected.  Using existing fields as seed values.");
			for(String existingField : existingSchema.getsProfile().keySet()) {
				existingSchema.getsProfile().get(existingField).setUsedInSchema(true);
				aliasMapping.put(existingField, existingSchema.getsProfile().get(existingField).getAliasNames());
			}
			this.recordsLoaded = existingSchema.getRecordsParsedCount();

			Map<String, String> schemaMergeMap = new HashMap<String, String>();

			for(String key : existingSchema.getsProfile().keySet()) {
				schemaMergeMap.put(key, key);
				String mergedAccumulatorKey = key;
				fieldMapping.put(mergedAccumulatorKey, null);
			}

			MergeData mergedData = new MergeData();
			mergedData.setSchema(existingSchema);
			mergedData.setSample(null);
			mergedData.setSampleMergedFieldsMapping(schemaMergeMap);
			currentMergeLookupId = existingSchema.getsGuid();
			mergeDataMapping.put(existingSchema.getsGuid(), mergedData);
			outputCurrentSampleMergedfieldsMapping(existingSchema.getsName(), getCurrentMergedFieldsMapping());

			if(existingSchema.getsDataSamples().isEmpty()) {
				logger.warn("Empty samples list in existing schema object.");
			} else {
				dataSampleMetaDataList.addAll(existingSchema.getsDataSamples());
				for(DataSampleMetaData dsmd : dataSampleMetaDataList) {
					logger.info("Existing schema contains sample: " + dsmd.getDsName() + ".");
				}
			}

			regionDataMapping.putAll(mergeCoordinateProfiles(regionDataMapping, mergedData.getSampleMergedFieldsMapping(),
					ReverseGeocodingLoader.getCoordinateProfiles(existingSchema.getsProfile())));
		}
	}

	private void analyzeDataSamples(List<DataSample> dataSamples) throws MainTypeException {
		for(DataSample dataSample : dataSamples) {
			try {
				mergeDataMapping.put(dataSample.getDsGuid(),
						initializeSampleMergedData(dataSample.getDsProfile(), dataSample));
			} catch (MainTypeException e) {
				logger.error(e);
			}
			DataSampleMetaData dsmd = new DataSampleMetaData();
			dsmd.setDataSampleId(dataSample.getDataSampleId());
			String dsId = dataSample.getDsGuid();
			dsmd.setDsGuid(dsId);
			dsmd.setDsDescription(dataSample.getDsDescription());
			dsmd.setDsFileName(dataSample.getDsFileName());
			dsmd.setDsFileType(dataSample.getDsFileType());
			dsmd.setDsName(dataSample.getDsName());
			dsmd.setDsVersion(dataSample.getDsVersion());
			dsmd.setDsLastUpdate(dsmd.getDsLastUpdate());
			boolean containsGuid = false;
			if(existingSchema != null) {
				for(DataSampleMetaData d : existingSchema.getsDataSamples()) {
					if(d.getDsGuid().equals(dsmd.getDsGuid())) {
						containsGuid = true;
						break;
					}
				}
			}
			if(!containsGuid) {
				dataSampleMetaDataList.add(dsmd);
			}

		}
	}

	public void setCurrentDataSampleGuid(String dsGuid) {
		if(mergeDataMapping.get(currentMergeLookupId) != null && mergeDataMapping.get(currentMergeLookupId).getSample() != null) {
			logger.info("Finished profiling sample with GUID: " + getCurrentDataSample().getDsGuid());
		}
		this.currentMergeLookupId = dsGuid;

		logger.info("Profiling sample with GUID " + getCurrentDataSample().getDsGuid());

		outputCurrentSampleMergedfieldsMapping(getCurrentDataSample().getDsName(), getCurrentMergedFieldsMapping());
	}
	
	private MergeData newInitializeSampleMergedData(Map<String, Profile> profileMap, DataSample dataSample) 
			throws MainTypeException {
		Map<String, String> currentSampleMergedFieldsMapping = new HashMap<String, String>();
		profileMap.entrySet()
			.stream()
			.filter(entry->entry.getValue().isUsedInSchema())
			.forEach(entry->
				// map the original name (in sample) to the key in the profile
				currentSampleMergedFieldsMapping.put(entry.getValue().getOriginalName(), entry.getKey()));
		
		MergeData mergeData = new MergeData();
		mergeData.setSchema(null);
		mergeData.setSample(dataSample);
		mergeData.setSampleMergedFieldsMapping(currentSampleMergedFieldsMapping);

		regionDataMapping.putAll(mergeCoordinateProfiles(regionDataMapping, mergeData.getSampleMergedFieldsMapping(), 
				ReverseGeocodingLoader.getCoordinateProfiles(dataSample.getDsProfile())));

		return mergeData;
	}

	private MergeData initializeSampleMergedData(Map<String, Profile> profileMap, DataSample dataSample) throws MainTypeException {
		Map<String, String> currentSampleMergedFieldsMapping = new HashMap<String, String>();
		List<String> currentSkipKeyList = new ArrayList<String>();
		for(String key : profileMap.keySet()) {
			String fieldNameInSample = String.copyValueOf(key.toCharArray());
			String mergedAccumulatorKey = null;
			Profile profile = profileMap.get(key);
			if(profile.isMergedInto()) {
				//in sProfile mapping, key is schema field name
				//in profile object, original name is name from sample
				mergedAccumulatorKey = key;
				fieldNameInSample = (profileMap.get(key).getOriginalName() != null) ? profileMap.get(key).getOriginalName() : key;
				if(currentSkipKeyList.contains(fieldNameInSample)) {
					currentSkipKeyList.remove(fieldNameInSample);
				}
			} else if(profile.isUsedInSchema()) {
				// if used in schema, use its name
				mergedAccumulatorKey = key;
			} else {
				// otherwise make a note to skip this key for the sample because it's neither merged nor used
				logger.info("Key \"" + key + "\" detected as neither merged nor used in schema - ignoring.");
				if(!currentSampleMergedFieldsMapping.containsKey(key)) {
					currentSkipKeyList.add(key);
				}
				continue;
			}

			fieldMapping.put(mergedAccumulatorKey, null);

			currentSampleMergedFieldsMapping.put(fieldNameInSample, mergedAccumulatorKey);

			AliasNameDetails aliasNameDetails = new AliasNameDetails();
			aliasNameDetails.setAliasName(fieldNameInSample);
			aliasNameDetails.setDsGuid(dataSample.getDsGuid());
			List<AliasNameDetails> aliasList;
			if(aliasMapping.containsKey(mergedAccumulatorKey)) {
				aliasList = aliasMapping.get(mergedAccumulatorKey);
			} else {
				aliasList = new ArrayList<AliasNameDetails>();
			}
			boolean contains = false;
			for (AliasNameDetails alias : aliasList) {
				if (alias.equals(aliasNameDetails)) {
					contains = true;
					break;
				}
			}
			if (!contains) {
				aliasList.add(aliasNameDetails);
			}
			aliasMapping.put(mergedAccumulatorKey, aliasList);

		}
		MergeData mergeData = new MergeData();
		mergeData.setSchema(null);
		mergeData.setSample(dataSample);
		mergeData.setSampleMergedFieldsMapping(currentSampleMergedFieldsMapping);

		regionDataMapping.putAll(mergeCoordinateProfiles(regionDataMapping, mergeData.getSampleMergedFieldsMapping(), 
				ReverseGeocodingLoader.getCoordinateProfiles(dataSample.getDsProfile())));

		return mergeData;
	}

	private void outputCurrentSampleMergedfieldsMapping(String name, Map<String, String> currentMergeMapping) {
		logger.debug(name+" merged fields mapping: ");
		for(String key : currentMergeMapping.keySet()) {
			if(key.equals(currentMergeMapping.get(key))) {
				logger.debug("Accumulating metrics for schema field \"" + currentMergeMapping.get(key) + "\".");
			} else {
				logger.debug("Accumulating metrics for schema field \"" + currentMergeMapping.get(key) 
				+ "\" (original name - \"" + key + "\").");
			}
		}
	}

	private boolean containsInterpretation(Interpretations interpretations, Interpretation candidate) {
		return interpretations.containsInterpretation(candidate);
	}

	public int getRecordsParsed() {
		return recordsLoaded;
	}

	public void setRecordsParsed(int recordsParsed) {
		this.recordsLoaded = recordsParsed;
	}

	public ProfilingProgressUpdateHandler getProgressUpdateListener() {
		return progressUpdateListener;
	}

	public void setProgressUpdateListener(ProfilingProgressUpdateHandler progressUpdateListener) {
		this.progressUpdateListener = progressUpdateListener;
	}

	public static Map<String, RegionData> mergeCoordinateProfiles(Map<String, RegionData> existingRegionData, Map<String, String> mergeMap, List<CoordinateProfile> coordinateProfiles) {
		for(CoordinateProfile coordinateProfile : coordinateProfiles) {
			String mergedLatitudeKey = mergeMap.get(coordinateProfile.getLatitude());
			String mergedLongitudeKey = mergeMap.get(coordinateProfile.getLongitude());

			coordinateProfile.setLatitude(mergedLatitudeKey);
			coordinateProfile.setLongitude(mergedLongitudeKey);

			RegionData regionData = ReverseGeocodingLoader.regionDataFromCoordinateProfile(coordinateProfile);

			if(existingRegionData.containsKey(mergedLatitudeKey)) {
				regionData = RegionData.add(existingRegionData.get(mergedLatitudeKey), regionData);
			}

			existingRegionData.put(mergedLatitudeKey, regionData);
			existingRegionData.put(mergedLongitudeKey, regionData);

		}
		return existingRegionData;
	}

	private static class MergeData {
		private Map<String, String> sampleMergedFieldsMapping;
		private DataSample sample;
		private Schema schema;

		public Map<String, String> getSampleMergedFieldsMapping() {
			return sampleMergedFieldsMapping;
		}

		public void setSampleMergedFieldsMapping(Map<String, String> sampleMergedFieldsMapping) {
			this.sampleMergedFieldsMapping = sampleMergedFieldsMapping;
		}

		public DataSample getSample() {
			return sample;
		}

		public void setSample(DataSample sample) {
			this.sample = sample;
		}

		public Schema getSchema() {
			return schema;
		}

		public void setSchema(Schema schema) {
			this.schema = schema;
		}

	}

	public static Schema generateSchema(List<DataSample> dataSamples, Map<String, List<ProfilerRecord>> sampleToRecordsMapping) {
		SchemaProfiler schemaProfiler = new SchemaProfiler(null, dataSamples);
		for(DataSample sample : dataSamples) {
			List<ProfilerRecord> records = sampleToRecordsMapping.get(sample.getDsGuid());
			schemaProfiler.setCurrentDataSampleGuid(sample.getDsGuid());
			records.forEach(record->schemaProfiler.accumulate(record));
		}
		Schema schema = schemaProfiler.finish();
		schema.setsName(UUID.randomUUID().toString());
		schema.setsGuid(schema.getsName());
		return schema;
	}

	public Map<String, AbstractProfileAccumulator<?>> getFieldMapping() {
		return fieldMapping;
	}

	@Override
	public Schema finish() {
		Schema schema = (existingSchema == null) ? new Schema() : existingSchema;
		Map<String, Profile> sProfile = new HashMap<String, Profile>();
		for(String key: fieldMapping.keySet()) {
			AbstractProfileAccumulator<?> accumulator = fieldMapping.get(key);
			accumulator.finish();
			Profile profile = accumulator.getState();
			profile.setPresence(((float)accumulator.getPresenceCount())/((float)recordsLoaded));
			if(aliasMapping.containsKey(key)) {
				profile.setAliasNames(aliasMapping.get(key));
			}
			if(existingSchema != null && existingSchema.getsProfile().containsKey(key)) {
				Integer existingNumDistinct = MetricsCalculationsFacade.stripNumDistinctValuesChars(
						existingSchema.getsProfile().get(key).getDetail().getNumDistinctValues());
				Integer newNumDistinct = MetricsCalculationsFacade.stripNumDistinctValuesChars(
						profile.getDetail().getNumDistinctValues());
				double m = Math.max(existingNumDistinct, newNumDistinct);
				profile.getDetail().setNumDistinctValues(MetricsCalculationsFacade.stringifyNumDistinctValues((int)m, true));
			}

			for(String mergeDataKey : mergeDataMapping.keySet()) {
				MergeData mergeData = mergeDataMapping.get(mergeDataKey);
				if(mergeData.getSample() != null && mergeData.getSample().getDsProfile().containsKey(key)) {
					DataSample sample = mergeData.getSample();
					Interpretation i = sample.getDsProfile().get(key)
							.getInterpretation();
					if(!containsInterpretation(profile.getInterpretations(), i)) {
						profile.getInterpretations().add(i);
					}
				} else if(schema != null && schema.getsProfile().containsKey(key)) {
					Interpretation i = schema.getsProfile().get(key)
							.getInterpretation();
					if(!containsInterpretation(profile.getInterpretations(), i)) {
						profile.getInterpretations().add(i);
					}
				}
			}

			Interpretation interpretation = profile.getInterpretation();
			if(!Interpretation.isUnknown(interpretation)) {
				logger.info("Interpretting field: " + key + " as " + interpretation.getiName());
			}

			sProfile.put(key, profile);

		}

		for(String key : regionDataMapping.keySet()) {
			if(sProfile.containsKey(key)) {
				sProfile.get(key).getDetail().setRegionDataIfApplicable(regionDataMapping.get(key));
			} else {
				logger.error("Error adding region data for key " + key + ".");
			}
		}

		sProfile.putAll(staticProfiles);
		sProfile.forEach((k,v)->v.setAttributes(Attributes.generateUnknownAttributes()));
		
		// keys no longer replaced in schema mapping
		// sProfile = DisplayNameHelper.replaceKeysWithDisplayNames(DisplayNameHelper.determineDisplayNames(sProfile));
		sProfile = DisplayNameHelper.determineDisplayNames(sProfile);
		
		schema.setsProfile(sProfile);

		schema.setRecordsParsedCount(recordsLoaded);
		schema.setsDataSamples(dataSampleMetaDataList);
		return schema;
	}
}
