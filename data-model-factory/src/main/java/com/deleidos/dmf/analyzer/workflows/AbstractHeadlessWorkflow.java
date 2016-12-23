package com.deleidos.dmf.analyzer.workflows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.deleidos.dmf.analyzer.TikaAnalyzer;
import com.deleidos.dmf.exception.AnalyzerException;
import com.deleidos.dmf.framework.TikaSampleAnalyzerParameters;
import com.deleidos.dp.beans.BinaryDetail;
import com.deleidos.dp.beans.Detail;
import com.deleidos.dp.beans.NumberDetail;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.beans.StringDetail;
import com.deleidos.dp.deserializors.SerializationUtility;
import com.deleidos.dp.enums.DetailType;
import com.deleidos.dp.enums.MainType;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.exceptions.MainTypeRuntimeException;
import com.deleidos.dp.h2.H2DataAccessObject;

public abstract class AbstractHeadlessWorkflow {
	public static final String testSessionId = "test-session";
	private List<String> fileNames;
	protected File uploadDir = null;
	protected String sessionId;
	protected String domainName;
	protected String tolerance;
	protected TikaAnalyzer analyzer;

	public AbstractHeadlessWorkflow(String uploadDir, String domainName, String tolerance) {
		fileNames = new ArrayList<String>();
		this.domainName = domainName;
		this.tolerance = tolerance;
		this.sessionId = testSessionId;
		this.uploadDir = new File(uploadDir);
		analyzer = new TikaAnalyzer();
	}

	protected void addLocalTestFile(String fileName) {
		fileNames.add(fileName);
	}

	protected List<HeadlessResource> giveSources() throws IOException {
		List<HeadlessResource> resources = new ArrayList<HeadlessResource>();
		for(int i = 0; i < fileNames.size(); i++) {
			String file;
			file = fileNames.get(i);
			File existingFile = new File(file);
			File fileCopy = (uploadDir == null) 
					? File.createTempFile("headless-schwiz", String.valueOf(System.currentTimeMillis())) 
							: new File(uploadDir, existingFile.getName());
					FileUtils.copyFile(existingFile, fileCopy);

					resources.add(new HeadlessResource(fileCopy.getPath(), null, null, 
							new FileInputStream(fileCopy), true, true));
		}
		resources.addAll(giveEmbeddedResources());
		return resources;
	}
	
	public abstract List<HeadlessResource> giveEmbeddedResources();

	public List<String> processResources() 
			throws AnalyzerException, DataAccessException, IOException {
		List<HeadlessResource> resources = giveSources();
		TikaAnalyzer analyzer = new TikaAnalyzer();
		List<String> guids = new ArrayList<String>();
		for(int i = 0; i < resources.size(); i++) {
			HeadlessResource dtr = resources.get(i);
			TikaSampleAnalyzerParameters params = TikaAnalyzer.generateSampleParameters(uploadDir.getAbsolutePath(), 
					dtr.getFilePath(), domainName, tolerance, testSessionId, i, resources.size());
			guids.add(analyzer.runSampleAnalysis(params).getGuid());
		}
		return guids;
	}

	public void simulateMerge(JSONObject sampleObject1, String nonMergedKey, String mergedFieldKey) {
		JSONObject dsProfile = sampleObject1.getJSONObject("dsProfile");
		JSONObject oldProfile = new JSONObject(dsProfile.getJSONObject(nonMergedKey).toString());
		boolean sameKey = nonMergedKey.equals(mergedFieldKey);
		if(sameKey) {
			oldProfile.put("merged-into-schema", true);
		} else {
			oldProfile.put("original-name", nonMergedKey);
			oldProfile.put("merged-into-schema", true);
		}
		dsProfile.put(mergedFieldKey, oldProfile);
		if(!sameKey) {
			Object rKey = dsProfile.remove(nonMergedKey);
		}
		sampleObject1.put("dsProfile", dsProfile);
	}
	
	public void addField(Schema schemaObject, String fieldName, MainType fieldMainType, DetailType fieldDetailType) {
		if (!fieldDetailType.getMainType().equals(fieldMainType)) {
			throw new MainTypeRuntimeException();
		}
		Profile profile = new Profile();
		profile.setPresence(-1f);
		Detail detail = null;
		switch (fieldMainType) {
		case NUMBER: detail = new NumberDetail(); break;
		case STRING: detail = new StringDetail(); break;
		case BINARY: detail = new BinaryDetail(); break;
		default: break;
		}
		if (detail != null) {
			detail.setDetailType(fieldDetailType.toString());
		}
		profile.setDetail(detail);
		profile.setMainType(fieldMainType.toString());
		schemaObject.getsProfile().put(fieldName, profile);
	}

	protected List<JSONObject> retrieveSingleSourceAnalysis(List<String> sampleGuids) throws JSONException, DataAccessException {
		List<JSONObject> singleSourceAnalyses = new ArrayList<JSONObject>();
		for(int i = 0; i < sampleGuids.size(); i++) {
			JSONObject singleAnalysis = new JSONObject(SerializationUtility.serialize(H2DataAccessObject.getInstance().getSampleByGuid(sampleGuids.get(i))));
			singleSourceAnalyses.add(singleAnalysis);
		}
		return singleSourceAnalyses;
	}

	protected JSONArray retrieveMultipleSourceAnalysis(TikaAnalyzer analyzer, List<String> sampleGuidsList, Schema existingSchema) throws DataAccessException, AnalyzerException {
		String[] sampleGuids = performMockVerificationStep(sampleGuidsList.toArray(new String[sampleGuidsList.size()]));
		if(sampleGuids == null) {
			return null;
		} else {
			String existingSchemaGuid = (existingSchema != null) ? existingSchema.getsGuid() : null;
			JSONArray result = analyzer.matchAnalyzedFields(sessionId, existingSchemaGuid, sampleGuids);
			return result;
		}
	}	

	protected JSONObject retrieveSchemaAnalysis(TikaAnalyzer analyzer, Schema existingSchema, JSONArray sourceAnalysisResult) throws DataAccessException, AnalyzerException, IOException {
		JSONArray mockedUpFrontendAdjustedSourceAnalysis = performMockMergeSamplesStep(existingSchema, new JSONArray(sourceAnalysisResult.toString()));
		JSONObject mockedUpFrontendSchema = (existingSchema != null) ? new JSONObject(SerializationUtility.serialize(existingSchema)) : null; 
		JSONObject analyzeSchemaData = new JSONObject();
		analyzeSchemaData.put("existing-schema", mockedUpFrontendSchema);
		analyzeSchemaData.put("data-samples", mockedUpFrontendAdjustedSourceAnalysis);
		if(mockedUpFrontendAdjustedSourceAnalysis == null) {
			return null;
		}
		return analyzer.analyzeSchema(uploadDir.getPath(), analyzeSchemaData, domainName, sessionId);
	}

	protected String giveSchema(JSONObject schemaJson) throws DataAccessException {
		JSONObject mockedUpSchemaJSON = performMockSchemaInlineEdittingStep(new JSONObject(schemaJson.toString()));
		if(mockedUpSchemaJSON == null) {
			return null;
		} else {
			return H2DataAccessObject.getInstance()
					.addSchema(SerializationUtility.deserialize(mockedUpSchemaJSON, Schema.class));
		}
	}

	public String runAnalysis() throws AnalyzerException, DataAccessException, IOException {
		addNecessaryTestFiles();
		giveSources();
		List<String> generatedSampleGuids = processResources();
		JSONArray retrieveSourceAnalysisResult = retrieveMultipleSourceAnalysis(analyzer, generatedSampleGuids, null);
		JSONObject schemaAnalysis = retrieveSchemaAnalysis(analyzer, null, retrieveSourceAnalysisResult);
		String schemaGuid = giveSchema(schemaAnalysis);
		return schemaGuid;
	}

	/**
	 * Add desired files to the workflow.  This step is identical to selecting files for upload on the frontend.  Use <i>addResourcesTestFile</i>
	 * to add resources included in the classpath or <i>addLocalTestFile</i> to add resources from the local file system.
	 */
	public abstract void addNecessaryTestFiles();

	/**
	 * This step represents the sample verification step of the schema wizard workflow.  The string array returned will be the sample guids
	 * analyzed.
	 * @param generatedSampleGuids The list of all sample guids from the preceding "upload"
	 * @return the editted String array, or null if this step is unimportant for the workflow
	 */
	public abstract String[] performMockVerificationStep(String[] generatedSampleGuids);

	/**
	 * This step represents the user interaction that defines merges, seeds, and dropped values.  JSON objects must be manually manipulated
	 * and returned as a JSONArray.
	 * @param existingSchema TODO
	 * @param retrieveSourceAnalysisResult The array of Sample JSONObjects that is returned from TikaAnalyzer.retrieveSourceAnalysis()
	 * @return the editted DataSample JSONArray, or null if this step is unimportant for the workflow
	 */
	public abstract JSONArray performMockMergeSamplesStep(Schema existingSchema, JSONArray retrieveSourceAnalysisResult);

	/**
	 * This step represents the schema editted step of the schema wizard workflow.  The Schema object must be manually editted and returned 
	 * in subclasses that implement this method.
	 * @param schemaAnalysis The schema analysis that is returned from TikaAnalyzer.retrieveSchemaAnalysis() 
	 * @return the editted Schema JSONObject, or null if this step is unimportant for the workflow
	 */
	public abstract JSONObject performMockSchemaInlineEdittingStep(JSONObject schemaAnalysis);

}
