package com.deleidos.dmf.workflows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.deleidos.dmf.analyzer.Analyzer;
import com.deleidos.dmf.analyzer.TikaAnalyzer;
import com.deleidos.dmf.analyzer.workflows.AbstractHeadlessWorkflow;
import com.deleidos.dmf.analyzer.workflows.HeadlessResource;
import com.deleidos.dmf.exception.AnalyticsTikaProfilingException;
import com.deleidos.dmf.exception.AnalyticsUndetectableTypeException;
import com.deleidos.dmf.exception.AnalyticsUnsupportedParserException;
import com.deleidos.dmf.exception.AnalyzerException;
import com.deleidos.dmf.framework.AnalyticsDefaultDetector;
import com.deleidos.dmf.framework.AnalyticsEmbeddedDocumentExtractor;
import com.deleidos.dmf.framework.TikaSampleAnalyzerParameters;
import com.deleidos.dmf.web.SchemaWizardSessionUtility;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.deserializors.SerializationUtility;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.h2.H2DataAccessObject;
import com.deleidos.dp.profiler.api.ProfilingProgressUpdateHandler;

/**
 * Abstract class defining most functionality of a "workflow."  A workflow is a mocked up series of front end interactions built for 
 * integration tests.  Subclasses of AbstractAnalyzerTestWorkflow must implement three methods:<br> 
 * <i>addNecessaryFiles()<br></i>
 * <i>performMockVerificationStep() <br></i>
 * <i>performMockMergeSamplesStep() <br></i>
 * <i>performMockSchemaInlineEdittingStep() <br></i>
 * See methods for more details.<br>
 * <br>
 * When subclasses of AbstractAnalyzerTestWorkflow are instantiated, the new instance should be passed into the AbstractAnalyzerTestWorkflow.addOrGetWorkflow()
 * method.  This method ensures that every workflow only runs one time, even if the <i>runAnalysis()</i> method is called.  If the new 
 * instance is not passed to this method, identical workflows could run multiple times.  The intention of this structure is to run the entire
 * workflow only once, but run multiple tests based on the results of the workflow. 
 * 
 * @author leegc
 *
 */
public abstract class AbstractAnalyzerTestWorkflow extends AbstractHeadlessWorkflow {

	public static Map<Class<? extends AbstractAnalyzerTestWorkflow>, AbstractAnalyzerTestWorkflow> workflows 
	= new HashMap<>();
	public static AbstractAnalyzerTestWorkflow addOrGetWorkflow(AbstractAnalyzerTestWorkflow workflow) {
		if(workflows.containsKey(workflow.getClass())) {
			return workflows.get(workflow.getClass());
		} else {
			workflows.put(workflow.getClass(), workflow);
			return workflow;
		}
	}

	public static final Logger logger = Logger.getLogger(AbstractAnalyzerTestWorkflow.class);
	public static final String TARGET_UPLOAD_DIR =  new File("./target" +File.separator+ "test-uploads-dir" + File.separator + testSessionId).getAbsolutePath();
	private Schema existingSchema = null;
	private List<String> resourceNames;
	private List<String> generatedSampleGuids;
	private String generatedSchemaGuid;
	private List<JSONObject> singleSourceAnalysis;
	private JSONArray retrieveSourceAnalysisResult;
	private JSONObject schemaAnalysis;
	private boolean testComplete;
	private boolean output = false;
	private List<AnalyzerWorkflowMetadata> workflowMetadataList;

	/**
	 * Static method to add a workflow to the testing suite.  Workflows should be passed into this method when instantiated.  This will
	 * ensure that they are only run once.
	 * @param workflow The instance of the workflow.
	 * @return Either the same instance, or the instance of the same class that has already been instantiated.
	 */
	public static AbstractAnalyzerTestWorkflow addOrGetStaticWorkflow(AbstractAnalyzerTestWorkflow workflow) {
		return addOrGetWorkflow(workflow);
	}

	public void init() {
		testComplete = false;
		if(output) {
			logger.info("Mock upload directory: " + TARGET_UPLOAD_DIR);
		}
		resourceNames = new ArrayList<String>();
		generatedSampleGuids = new ArrayList<String>();
		singleSourceAnalysis = new ArrayList<JSONObject>();
		workflowMetadataList = new ArrayList<AnalyzerWorkflowMetadata>();
		addNecessaryTestFiles();
	}

	protected AbstractAnalyzerTestWorkflow() {
		super(TARGET_UPLOAD_DIR, "Transportation", "strict");
	}

	protected void addResourceTestFile(String resourceName) {
		resourceNames.add(resourceName);
	}

	@Override
	protected List<HeadlessResource> giveSources() throws IOException {
		List<HeadlessResource> resources = new ArrayList<HeadlessResource>();
		for(int i = 0; i < resourceNames.size(); i++) {
			String file;
			InputStream stream;
			file = resourceNames.get(i);
			stream = getClass().getResourceAsStream(file);

			File resourceCopy = new File(TARGET_UPLOAD_DIR, file);
			FileUtils.copyInputStreamToFile(stream, resourceCopy);

			resources.add(new HeadlessResource(resourceCopy.getPath(), null, null, new FileInputStream(resourceCopy), true, true));
		}
		if(output) {
			logger.info("Added " + resources.size() + " test files.");
		}
		return resources;
	}

	public String runAnalysis() throws AnalyticsUndetectableTypeException, AnalyticsUnsupportedParserException, IOException, AnalyzerException, DataAccessException {
		String schemaGuid = null;
		if(testComplete) {
			return schemaGuid;
		} else {
			init();
		}
		giveSources();
		generatedSampleGuids = processResources();
		generatedSampleGuids.forEach(x->logger.debug(x));

		singleSourceAnalysis = retrieveSingleSourceAnalysis(generatedSampleGuids);
		if(output) {
			logger.info("Retrieved each source analysis individually.");
		}

		singleSourceAnalysis.forEach(x->logger.debug(x));
		retrieveSourceAnalysisResult = retrieveMultipleSourceAnalysis(analyzer, generatedSampleGuids, existingSchema);
		if(retrieveSourceAnalysisResult != null) {
			if(output) {
				logger.info("Retrieved source group analysis.");
			}
			for(int i = 0; i < getRetrieveSourceAnalysisResult().length(); i++) {
				logger.debug(getRetrieveSourceAnalysisResult().get(i));
			}
		} else {
			testComplete = true;
		}

		if(testComplete) {
			if(output) {
				logger.warn("Stopping " + getClass().getName() + " at retrieveMultipleSourceAnalysis() (null parameter passed during test).");
			}
			return schemaGuid;
		} else {
			schemaAnalysis = retrieveSchemaAnalysis(analyzer, existingSchema, retrieveSourceAnalysisResult);
			testComplete = schemaAnalysis == null;
			if(output) {
				logger.info("Retrieved schema analysis.");
			}
			logger.debug(schemaAnalysis);
		}

		if(testComplete) {
			if(output) {
				logger.warn("Stopping " + getClass().getName() + " at retrieveSchemaAnalysis() (null parameter passed during test).");
			}
			return schemaGuid;
		} else {
			schemaGuid = giveSchema(schemaAnalysis);
			generatedSchemaGuid = schemaGuid;
			if (schemaGuid == null) {
				logger.warn("Stopping " + getClass().getName() + " at giveSchema() (null parameter passed during test).");
				testComplete = true;
			} else {
				if (output) {
					logger.info("Saved schema with guid " + schemaGuid + ".");
				}
			}
		}

		if(testComplete) {
			if(output) {
				logger.warn("Stopping " + getClass().getName() + " at giveSchema() (null parameter passed during test).");
			}
			return schemaGuid;
		} 
		
		logger.info("File to schema processing finished.");
		logger.info(this.getClass().getName() + " workflow finished.");
		testComplete = true;
		return schemaGuid;
	}

	public List<String> getGeneratedSampleGuids() {
		return generatedSampleGuids;
	}

	public void setGeneratedSampleGuids(List<String> generatedSampleGuids) {
		this.generatedSampleGuids = generatedSampleGuids;
	}

	public String getGeneratedSchemaGuid() {
		return generatedSchemaGuid;
	}

	public void setGeneratedSchemaGuid(String generatedSchemaGuid) {
		this.generatedSchemaGuid = generatedSchemaGuid;
	}

	public List<JSONObject> getSingleSourceAnalysis() {
		return singleSourceAnalysis;
	}

	public void setSingleSourceAnalysis(List<JSONObject> singleSourceAnalysis) {
		this.singleSourceAnalysis = singleSourceAnalysis;
	}

	public JSONArray getRetrieveSourceAnalysisResult() {
		return retrieveSourceAnalysisResult;
	}

	public void setRetrieveSourceAnalysisResult(JSONArray retrieveSourceAnalysisResult) {
		this.retrieveSourceAnalysisResult = retrieveSourceAnalysisResult;
	}

	public JSONObject getSchemaAnalysis() {
		return schemaAnalysis;
	}

	public void setSchemaAnalysis(JSONObject schemaAnalysis) {
		this.schemaAnalysis = schemaAnalysis;
	}

	public boolean isOutput() {
		return output;
	}

	public void setOutput(boolean output) {
		this.output = output;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public List<AnalyzerWorkflowMetadata> getWorkflowMetadataList() {
		return workflowMetadataList;
	}

	public void setWorkflowMetadataList(List<AnalyzerWorkflowMetadata> workflowMetadataList) {
		this.workflowMetadataList = workflowMetadataList;
	}

	public Schema getExistingSchema() {
		return existingSchema;
	}

	public void setExistingSchema(Schema existingSchema) {
		this.existingSchema = existingSchema;
	}

	public class AnalyzerWorkflowMetadata {
		private long totalExecutionTime;
		private long inputFileSize;
		private int numberOfRecords;
		private int numberOfReverseGeocodingCalls;
		private int numberOfEmbeddedDocuments;
		private int numberOfFields;
		private boolean extractedAndProfiledBodyContent;
		private String fileType;

		public String getFileType() {
			return fileType;
		}
		public void setFileType(String fileType) {
			this.fileType = fileType;
		}
		public long getTotalExecutionTime() {
			return totalExecutionTime;
		}
		public void setTotalExecutionTime(long totalExecutionTime) {
			this.totalExecutionTime = totalExecutionTime;
		}
		public long getInputFileSize() {
			return inputFileSize;
		}
		public void setInputFileSize(long inputFileSize) {
			this.inputFileSize = inputFileSize;
		}
		public int getNumberOfRecords() {
			return numberOfRecords;
		}
		public void setNumberOfRecords(int numberOfRecords) {
			this.numberOfRecords = numberOfRecords;
		}
		public int getNumberOfReverseGeocodingCalls() {
			return numberOfReverseGeocodingCalls;
		}
		public void setNumberOfReverseGeocodingCalls(int numberOfReverseGeocodingCalls) {
			this.numberOfReverseGeocodingCalls = numberOfReverseGeocodingCalls;
		}
		public int getNumberOfEmbeddedDocuments() {
			return numberOfEmbeddedDocuments;
		}
		public void setNumberOfEmbeddedDocuments(int numberOfEmbeddedDocuments) {
			this.numberOfEmbeddedDocuments = numberOfEmbeddedDocuments;
		}
		public boolean isExtractedAndProfiledBodyContent() {
			return extractedAndProfiledBodyContent;
		}
		public void setExtractedAndProfiledBodyContent(boolean extractedAndProfiledBodyContent) {
			this.extractedAndProfiledBodyContent = extractedAndProfiledBodyContent;
		}
		public int getNumberOfFields() {
			return numberOfFields;
		}
		public void setNumberOfFields(int numberOfFields) {
			this.numberOfFields = numberOfFields;
		}

	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getTolerance() {
		return tolerance;
	}

	public void setTolerance(String tolerance) {
		this.tolerance = tolerance;
	}
}
