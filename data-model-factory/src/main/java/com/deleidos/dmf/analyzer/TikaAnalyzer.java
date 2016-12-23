package com.deleidos.dmf.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.ws.rs.ProcessingException;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.deleidos.config.AbstractConfig;
import com.deleidos.dmf.analyzer.workflows.CLIWorkflow;
import com.deleidos.dmf.exception.AnalyticsCancelledWorkflowException;
import com.deleidos.dmf.exception.AnalyticsInitializationRuntimeException;
import com.deleidos.dmf.exception.AnalyticsInvalidSchemaException;
import com.deleidos.dmf.exception.AnalyticsTikaProfilingException;
import com.deleidos.dmf.exception.AnalyticsUndetectableTypeException;
import com.deleidos.dmf.exception.AnalyticsUnsupportedParserException;
import com.deleidos.dmf.exception.AnalyzerException;
import com.deleidos.dmf.framework.AbstractAnalyticsParser.ProgressUpdatingBehavior;
import com.deleidos.dmf.framework.AnalyticsDefaultDetector;
import com.deleidos.dmf.framework.AnalyticsDefaultParser;
import com.deleidos.dmf.framework.AnalyticsEmbeddedDocumentExtractor;
import com.deleidos.dmf.framework.TikaAnalyzerParameters;
import com.deleidos.dmf.framework.TikaAnalyzerParameters.MostCommonFieldWithWalking;
import com.deleidos.dmf.framework.TikaSampleAnalyzerParameters;
import com.deleidos.dmf.framework.TikaSchemaAnalyzerParameters;
import com.deleidos.dmf.handler.AnalyticsProgressTrackingContentHandler;
import com.deleidos.dmf.progressbar.ProgressBarManager;
import com.deleidos.dmf.progressbar.ProgressState;
import com.deleidos.dmf.progressbar.ProgressState.STAGE;
import com.deleidos.dmf.progressbar.SimpleProgressUpdater;
import com.deleidos.dmf.web.SchemaWizardSessionUtility;
import com.deleidos.dmf.web.TimeEstimateProgressUpdater;
import com.deleidos.dp.beans.Attributes;
import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.beans.Interpretation;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.deserializors.SerializationUtility;
import com.deleidos.dp.enums.Tolerance;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.h2.H2DataAccessObject;
import com.deleidos.dp.interpretation.IEConfig;
import com.deleidos.dp.interpretation.InterpretationEngineFacade;
import com.deleidos.dp.profiler.SampleProfiler;
import com.deleidos.dp.profiler.SampleSecondPassProfiler;
import com.deleidos.dp.profiler.SchemaProfiler;
import com.deleidos.dp.profiler.api.Profiler;
import com.deleidos.dp.profiler.api.ProfilingProgressUpdateHandler;
import com.deleidos.hd.h2.H2Config;
import com.deleidos.hd.h2.H2Database;
import com.deleidos.hd.h2.H2TestDatabase;

/**
 * Use Apache Tika to analyze sample files.
 * @author leegc
 *
 */
public class TikaAnalyzer implements FileAnalyzer {
	private static final Logger logger = Logger.getLogger(TikaAnalyzer.class);
	private static final String EXTRACTED_CONTENT_DIRECTORY_SUFFIX = "-embedded";

	public static TikaSampleAnalyzerParameters generateSampleParameters(String uploadFileDir, String sampleFileName, String domainName, String tolerance,
			String sessionId, int sampleNumber, int totalNumberSamples) throws IOException {
		return generateSampleParameters(uploadFileDir, sampleFileName, domainName, tolerance, sessionId, sampleNumber, totalNumberSamples, null);
	}

	public static TikaSampleAnalyzerParameters generateSampleParameters(String uploadFileDir, String sampleFileName, String domainName, String tolerance,
			String sessionId, int sampleNumber, int totalNumberSamples, ProgressBarManager progressBarManager) throws IOException {
		String guid = Analyzer.generateUUID();

		sampleFileName = new File(sampleFileName).getName();
		File uploadFile = new File(uploadFileDir + File.separator + sampleFileName);
		if(!uploadFile.exists()) {
			throw new IOException("Uploaded file does not exist at " + uploadFile + ".");
		}

		logger.debug("Upload file expected to be " + uploadFile);

		InputStream is = new FileInputStream(uploadFile);

		SampleProfiler sampleProfiler = new SampleProfiler(Tolerance.fromString(tolerance));
		List<String> namePlaceholders = new ArrayList<String>();
		for(int i = 0; i < totalNumberSamples; i++) {
			if(i == sampleNumber) {
				namePlaceholders.add(sampleFileName);
			} else {
				namePlaceholders.add("");
			}
		}

		File extractedContentDirectory = AnalyticsEmbeddedDocumentExtractor.getOrCreateExtractedContentDirectory(
				uploadFileDir, uploadFile.getName(), EXTRACTED_CONTENT_DIRECTORY_SUFFIX);

		progressBarManager = (progressBarManager == null) 
				? ProgressBarManager.sampleProgressBar(namePlaceholders, sampleNumber) 
						: progressBarManager;

		if(!progressBarManager.isDuring(STAGE.DETECT)) {
			progressBarManager.jumpToNthIndexStage(sampleNumber, STAGE.DETECT);
		}
		SimpleProgressUpdater progressUpdater = 
				new SimpleProgressUpdater(sessionId, progressBarManager, uploadFile.length());
		TikaSampleAnalyzerParameters tikaProfilerParams = new TikaSampleAnalyzerParameters(sampleProfiler, 
				progressBarManager, uploadFileDir, guid, is, new AnalyticsProgressTrackingContentHandler(), 
				new Metadata());
		tikaProfilerParams.setUploadFileDir(uploadFileDir);
		tikaProfilerParams.setDomainName(domainName);
		tikaProfilerParams.setNumSamplesUploading(totalNumberSamples);
		tikaProfilerParams.setSampleFilePath(uploadFile.getAbsolutePath());
		tikaProfilerParams.setSampleNumber(sampleNumber);
		tikaProfilerParams.setTolerance(tolerance);
		tikaProfilerParams.setStream(is);
		tikaProfilerParams.setSessionId(sessionId);
		tikaProfilerParams.setStreamLength(uploadFile.length());
		tikaProfilerParams.set(File.class, uploadFile);
		tikaProfilerParams.set(ProfilingProgressUpdateHandler.class, progressUpdater);
		tikaProfilerParams.setExtractedContentDir(extractedContentDirectory.getAbsolutePath());
		return tikaProfilerParams;
	}

	public static TikaSchemaAnalyzerParameters generateSchemaParameters(String uploadFileDir, JSONObject schemaAnalysisData, 
			String domainName, String sessionId) throws DataAccessException, AnalyticsInvalidSchemaException {
		Schema schemaFromFrontEnd = null;
		if(!schemaAnalysisData.isNull("existing-schema")) {
			schemaFromFrontEnd = 
					SerializationUtility.deserialize(schemaAnalysisData.getJSONObject("existing-schema"), Schema.class);
			logger.info("Existing schema guid is " + schemaFromFrontEnd.getsGuid());
		}

		JSONArray edittedSourceAnalysis = schemaAnalysisData.getJSONArray("data-samples");
		List<DataSample> sampleList = new ArrayList<DataSample>();
		List<String> sampleNames = new ArrayList<String>();
		Map<String, MostCommonFieldWithWalking> mostCommonFieldWithWalkingCount = new HashMap<String, MostCommonFieldWithWalking>();
		for(int i = 0; i < edittedSourceAnalysis.length(); i++) {
			try {
				DataSample sample = SerializationUtility.deserialize(edittedSourceAnalysis.get(i).toString(), DataSample.class);
				sampleList.add(sample);
				sampleNames.add(sample.getDsName());
				MostCommonFieldWithWalking commonField = TikaAnalyzerParameters.determineProgressRepresentativeField(sample.getDsProfile());
				mostCommonFieldWithWalkingCount.put(sample.getDsGuid(), commonField);
			} catch (JSONException e) {
				logger.error(e);
				throw new AnalyticsInvalidSchemaException("Error deserializing into DataSample bean.");
			}
		}

		String guid = Analyzer.generateUUID();
		SchemaProfiler schemaProfiler = new SchemaProfiler(schemaFromFrontEnd, sampleList);
		ProgressBarManager schemaProgressBar = ProgressBarManager.schemaProgressBar(sampleNames);
		TikaSchemaAnalyzerParameters schemaParameters = new TikaSchemaAnalyzerParameters(
				schemaProfiler, schemaProgressBar, uploadFileDir, guid, domainName, sampleList);
		schemaParameters.setSessionId(sessionId);
		schemaParameters.setExistingSchema(schemaFromFrontEnd);
		schemaParameters.setMostCommonFieldWithWalkingCount(mostCommonFieldWithWalkingCount);
		return schemaParameters;
	}

	public TikaSampleAnalyzerParameters runSampleAnalysis(TikaSampleAnalyzerParameters params)
			throws IOException, AnalyzerException, DataAccessException {
	
		SchemaWizardSessionUtility.getInstance().requestAnalysis(params.getSessionId(), params.get(File.class));

		AnalyticsDefaultDetector detector = new AnalyticsDefaultDetector();
		detector.enableProgressUpdates(params.getSessionId(), params.getProgressBar());
		AnalyticsDefaultParser parser = new AnalyticsDefaultParser(detector, params);

		Profiler<?> profiler = params.getProfiler();
		SampleProfiler sampleProfiler = null;
		if (profiler instanceof SampleProfiler) {
			sampleProfiler = (SampleProfiler) profiler;
		} else {
			throw new AnalyticsInitializationRuntimeException(
					"Expected a sample profiler, got a " + params.getProfiler().getClass() + ".");
		}

		params.getMetadata().set(Metadata.CONTENT_LENGTH, String.valueOf(params.getStreamLength()));
		SchemaWizardSessionUtility.getInstance().updateProgress(params.getProgressBar(), params.getSessionId());

		parser.runSampleAnalysis(params);

		params.getProgressBar().goToNextStateIfCurrentIs(ProgressState.STAGE.FIRST_PASS);
		SchemaWizardSessionUtility.getInstance().updateProgress(params.getProgressBar(), params.getSessionId());
		DataSample dataSampleBean = sampleProfiler.finish();
		if(dataSampleBean.getDsProfile().isEmpty()) {
			throw new AnalyticsUnsupportedParserException("Parsing finished without an exception, but no keys were extracted.");
		}

		int numFields = dataSampleBean.getDsProfile().size();
		TimeEstimateProgressUpdater timeEstimate = 
				new TimeEstimateProgressUpdater(params.getSessionId(), params.getProgressBar(), numFields * 50);
		// show progress estimate based on number of fields (roughly 50 millis per field based on testing)
		// need to estimate using number of interpretations though
		// IE is O(n*m)
		// change the data sample to the appropriate interpretations inline -- side effects
		new Thread(timeEstimate).start(); 
		InterpretationEngineFacade.interpretInline(dataSampleBean, params.getDomainName(), null);
		timeEstimate.setDone();

		params.setRecordsInSample(sampleProfiler.getRecordsParsed());
		logger.debug("Setting content as extracted.");

		params.getProgressBar().goToNextStateIfCurrentIs(ProgressState.STAGE.INTERPRET);
		SchemaWizardSessionUtility.getInstance().updateProgress(params.getProgressBar(), params.getSessionId());


		dataSampleBean.setDsFileType(params.getMetadata().get(Metadata.CONTENT_TYPE));
		int records = sampleProfiler.getRecordsParsed();
		params.setRecordsInSample(records);

		try {
			InputStream secondPassStream = new FileInputStream(params.getSampleFilePath());
			//params.setReverseGeocodingCallsEstimate(sampleProfiler.getNumGeoSpatialQueries());

			SampleSecondPassProfiler sampleGeocoder = new SampleSecondPassProfiler(dataSampleBean);
			SimpleProgressUpdater secondPassProgressUpdater = null;
			ProgressUpdatingBehavior progressUpdatingBehavior = ProgressUpdatingBehavior.BY_CHARACTERS_READ;
			MostCommonFieldWithWalking commonField = TikaAnalyzerParameters.determineProgressRepresentativeField(dataSampleBean.getDsProfile());
			if(commonField != null && sampleGeocoder.getAccumulatorMapping().containsKey(commonField.getFieldName())) {
				progressUpdatingBehavior = ProgressUpdatingBehavior.BY_COMMON_FIELD_OCCURANCES;
				// progress callbacks must come from accumulator
				secondPassProgressUpdater =  new SimpleProgressUpdater(params.getSessionId(), 
						params.getProgressBar(), commonField.getWalkingCount());
				sampleGeocoder.getAccumulatorMapping().get(commonField.getFieldName()).setCallback(secondPassProgressUpdater, true);
			} else {
				secondPassProgressUpdater = new SimpleProgressUpdater(params.getSessionId(), 
						params.getProgressBar(), dataSampleBean.getRecordsParsedCount());
				progressUpdatingBehavior = ProgressUpdatingBehavior.BY_RECORD_COUNT;
			}
			params.setProfiler(sampleGeocoder);
			params.setProgressUpdatingBehavior(progressUpdatingBehavior);
			params.set(ProfilingProgressUpdateHandler.class, secondPassProgressUpdater);
			params.setStream(secondPassStream);
			params.setHandler(new AnalyticsProgressTrackingContentHandler());
			parser.getExtractor().setAreContentsExtracted(false);
			dataSampleBean = parser.runSampleAnalysis(params).getProfilerBean();

		} catch (IOException e) {
			logger.error(e);
			logger.error(
					"There was an error retrieving the file for a second pass.  Returning results from first pass.");
			throw new IOException("Error during second pass", e);
		}

		logger.debug("Sample " + params.getSampleFilePath() + " complete.");
		params.getProgressBar().goToNextStateIfCurrentIs(ProgressState.STAGE.SECOND_PASS);
		SchemaWizardSessionUtility.getInstance().updateProgress(params.getProgressBar(), params.getSessionId());

		for (String key : dataSampleBean.getDsProfile().keySet()) {
			Interpretation interpretation = dataSampleBean.getDsProfile().get(key).getInterpretation();
			if (interpretation != null && !(Interpretation.isUnknown(interpretation))) {
				logger.info("Field \"" + key + "\" interpreted to be " + interpretation.getInterpretation() + ".");
			}

			Attributes attribute = dataSampleBean.getDsProfile().get(key).getAttributes();
			if (attribute != null && !attribute.isUnknown(attribute)) {
				dataSampleBean.getDsProfile().get(key).setAttributes(attribute);
			} 
			//			TODO this is for testing. The above code block largely should remain
			//			unchanged - the object sent back from the Interpretation Engine should be
			//			used for the setting of the attributes
			//			else {
			//				Attributes at = new Attributes();
			//				at.setCategorical("test");
			//				at.setIdentifier("test");
			//				at.setOrdinal("test");
			//				at.setQuantitative("test");
			//				at.setRelational("test");
			//				dataSampleBean.getDsProfile().get(key).setAttributes(at);
			//			}
		}

		if (params.isPersistInH2()) {
			String guid = H2DataAccessObject.getInstance().addSample(params.getProfilerBean());
			if (guid == null) {
				logger.error(params.getSampleFilePath() + " profile not successfully persisted in backend database.");
			}
		} else {
			logger.info("Profile set by parameters to not be persisted.");
		}

		params.getProgressBar().goToNextStateIfCurrentIs(STAGE.SECOND_PASS);
		SchemaWizardSessionUtility.getInstance().updateProgress(params.getProgressBar(), params.getSessionId());

		return params;
	}

	@Override
	public TikaSchemaAnalyzerParameters runSchemaAnalysis(TikaSchemaAnalyzerParameters params)
			throws IOException, AnalyzerException, DataAccessException {

		List<DataSample> userModifiedSampleList = params.getUserModifiedSampleList();
		String uploadFileDir = params.getUploadFileDir();

		Profiler<?> profiler = params.getProfiler();
		SchemaProfiler schemaProfiler = null;
		if (profiler instanceof SchemaProfiler) {
			schemaProfiler = (SchemaProfiler) profiler;
		} else {
			throw new AnalyticsInitializationRuntimeException(
					"Expected a schema profiler, got a " + params.getProfiler().getClass() + ".");
		}

		for (DataSample sample : userModifiedSampleList) {
			Metadata metadata = new Metadata();
			metadata.set(Metadata.CONTENT_TYPE, sample.getDsFileType());

			File file = new File(uploadFileDir, sample.getDsFileName());
			File extractedContentDirectory = AnalyticsEmbeddedDocumentExtractor.getOrCreateExtractedContentDirectory(
					uploadFileDir, file.getName(), EXTRACTED_CONTENT_DIRECTORY_SUFFIX);

			params.setExtractedContentDir(extractedContentDirectory.getAbsolutePath());

			FileInputStream fis = new FileInputStream(file);

			schemaProfiler.setCurrentDataSampleGuid(sample.getDsGuid());
			schemaProfiler.getFieldMapping().forEach((k,v)->v.removeCallback());

			SimpleProgressUpdater progressUpdater = null;
			ProgressUpdatingBehavior progressUpdatingBehavior = ProgressUpdatingBehavior.BY_CHARACTERS_READ;

			MostCommonFieldWithWalking mostCommon = params.getMostCommonFieldWithWalkingCount().get(sample.getDsGuid());
			if(mostCommon != null && schemaProfiler.getFieldMapping().containsKey(mostCommon.getFieldName())) {
				progressUpdatingBehavior = ProgressUpdatingBehavior.BY_COMMON_FIELD_OCCURANCES;
				// progress callbacks must come from accumulator
				progressUpdater =  new SimpleProgressUpdater(params.getSessionId(), 
						params.getProgressBar(), mostCommon.getWalkingCount());
				schemaProfiler.getFieldMapping().get(mostCommon.getFieldName()).setCallback(progressUpdater, true);
			} else {
				progressUpdater = new SimpleProgressUpdater(params.getSessionId(), 
						params.getProgressBar(), sample.getRecordsParsedCount());
				progressUpdatingBehavior = ProgressUpdatingBehavior.BY_RECORD_COUNT;
			}
			params.setProgressUpdatingBehavior(progressUpdatingBehavior);
			params.set(ProfilingProgressUpdateHandler.class, progressUpdater);
			params.set(File.class, file);
			params.setHandler(new AnalyticsProgressTrackingContentHandler());
			params.setMetadata(metadata);
			params.setStream(fis);
			if(sample.getDsExtractedContentDir() != null) {
				params.setExtractedContentDir(sample.getDsExtractedContentDir());
			}

			SchemaWizardSessionUtility.getInstance().updateProgress(params.getProgressBar(), params.getSessionId());

			//params.setProgress(new ProgressBar(sample.getDsFileName(), i, userModifiedSampleList.size(), ProgressState.schemaProgress));
			//params.getProgress().setCurrentState(ProgressState.schemaProgress);

			params.set(Profiler.class, schemaProfiler);

			SchemaWizardSessionUtility.getInstance().requestAnalysis(params.getSessionId(), params.get(File.class));		

			try {
				AnalyticsDefaultDetector detector = new AnalyticsDefaultDetector();
				AnalyticsDefaultParser parser = new AnalyticsDefaultParser(detector, params);

				parser.runSchemaAnalysis(params);

				params.getProgressBar().goToNextStateIfCurrentIs(STAGE.SCHEMA_PASS);
				logger.info("Schema analysis successfully completed for " + sample.getDsFileName() + ".");
			} finally {
				params.getStream().close();
			}
		}

		params.getProgressBar().goToNextStateIfCurrentIs(ProgressState.STAGE.SCHEMA_PASS);
		SchemaWizardSessionUtility.getInstance().updateProgress(params.getProgressBar(), params.getSessionId());
		return params;
	}

	@Override
	public String analyzeSample(String uploadFileDir, String sampleFilePath, String domainName, String tolerance,
			String sessionId, int sampleNumber, int totalNumberSamples, ProgressBarManager progressBar) 
					throws AnalyticsCancelledWorkflowException {

		try {
			TikaSampleAnalyzerParameters params = generateSampleParameters(uploadFileDir, sampleFilePath, domainName, 
					tolerance, sessionId, sampleNumber, totalNumberSamples, progressBar);
			try {
				return runSampleAnalysis(params).getProfilerBean().getDsGuid();
			} catch (AnalyticsCancelledWorkflowException e) {
				throw e;
			} catch (DataAccessException e) {
				logger.error("Data Access Exception processing sample " +sampleFilePath +".",e);
				return H2Database.DATA_ERROR_GUID;
			} catch (AnalyticsUndetectableTypeException e) {
				logger.error("Sample "+sampleFilePath+" was determined to be undetectable.",e);
				return H2Database.UNDETECTABLE_SAMPLE_GUID;
			} catch (AnalyticsUnsupportedParserException e) {
				logger.error("Sample " +sampleFilePath+ " was determined to not have a supported parser.", e);
				return H2Database.UNSUPPORTED_PARSER_GUID;
			} catch (ProcessingException e) {
				throw new ProcessingException(e.getMessage());
			} catch (Exception e) {
				if(SchemaWizardSessionUtility.getInstance().isCancelled(sessionId)) {
					logger.info("An "+e.getClass()+" exception was caught after workflow was cancelled.");
					logger.debug("Exception caught from cancelled workflow", e);
				} else {
					logger.error("Unexpected exception while processing " + sampleFilePath +".", e);
				}
				return H2Database.UNDETERMINED_ERROR_GUID;
			} finally {
				params.getStream().close();
			}
		} catch (IOException e) {
			logger.error("IOException processing sample " + sampleFilePath + ".", e);
			return H2Database.IO_ERROR_GUID;
		} finally {
			if (sampleNumber == totalNumberSamples - 1) {
				SchemaWizardSessionUtility.getInstance().registerCompleteAnalysis(sessionId);
			}
		}
	}

	@Override
	public JSONObject analyzeSchema(String uploadFileDir, JSONObject schemaAnalysisData, String domainName, String sessionId)
			throws AnalyticsCancelledWorkflowException {
		try {
			TikaSchemaAnalyzerParameters params = generateSchemaParameters(uploadFileDir, schemaAnalysisData, domainName, sessionId);

			try {
				Schema schemaBean = runSchemaAnalysis(params).getProfilerBean();
				String currentVersion = schemaBean.getsVersion();
				if (currentVersion != null) {
					double newVersion = Double.parseDouble(currentVersion);
					newVersion += 0.01;
					String newStringVersion = new DecimalFormat("#0.00").format(newVersion);
					schemaBean.setsVersion(newStringVersion);
				} else {
					schemaBean.setsVersion("1.00");
				}
				JSONObject result = new JSONObject(SerializationUtility.serialize(schemaBean));
				SchemaWizardSessionUtility.getInstance().registerCompleteAnalysis(sessionId);
				return result;
			} catch (AnalyticsCancelledWorkflowException e) {
				logger.error("Schema workflow cancelled - session " + sessionId +".");
				throw e;
			} 
		} catch(IOException e) {
			logger.error("IOException during schema pass.", e);
			return new JSONObject();
		} catch(AnalyticsCancelledWorkflowException e) {
			logger.error("Workflow cancelled during schema analysis.", e);
			throw e;
		} catch (Exception e) {
			if(SchemaWizardSessionUtility.getInstance().isCancelled(sessionId)) {
				logger.info("An "+e.getClass()+" exception was caught after workflow was cancelled.");
				logger.debug("Exception caught from cancelled workflow.", e);
			} else {
				logger.error("Unexpected exception while processing schema.", e);
			}
			return new JSONObject();
		}
	}

	public static void main(String[] args)
			throws IOException, AnalyzerException, DataAccessException, SQLException, InterruptedException {
		if (System.getenv(H2Config.SW_CONFIG_ENV_VAR) == null) {
			H2TestDatabase h2Test = new H2TestDatabase();
			h2Test.startTestServer(h2Test.getConfig());
			H2DataAccessObject.setInstance(h2Test);
			InterpretationEngineFacade.setInstance(IEConfig.BUILTIN_CONFIG);
		}
		Scanner scanner = new Scanner(System.in);
		List<String> argList = new ArrayList<String>(args.length);
		List<String> files = new ArrayList<String>();
		for (String a : args) {
			argList.add(a);
		}
		String tolerance = "strict";
		String domainName = "Transportation";
		String interpretationEngine = "";
		try {
			int index = -1;
			if ((index = argList.indexOf("-i")) > -1) {
				interpretationEngine = argList.get(index + 1);
				argList.remove(index + 1);
				argList.remove(index);
			}
			if ((index = argList.indexOf("-d")) > -1) {
				domainName = argList.get(index + 1);
				argList.remove(index + 1);
				argList.remove(index);
			}
			if ((index = argList.indexOf("-t")) > -1) {
				tolerance = argList.get(index + 1);
				argList.remove(index + 1);
				argList.remove(index);
			}
			if (args.length == 0) {
				logger.error("Provide a path(s) to the file(s) that you want to analyze.");
				scanner.close();
				return;
			}
		} catch (Exception e) {
			System.err.println(e);
			return;
		}

		argList.forEach(x->System.out.println(x));
		files.addAll(argList);
		if (files.size() < 1) {
			System.out.println("No files provided.");
			System.exit(1);
		}
		
		do {
			String uploadDir = Files.createTempDirectory("temp").toString();
			CLIWorkflow mainWorkflow = new CLIWorkflow(uploadDir, domainName, tolerance, files, scanner);
			String schemaGuid = mainWorkflow.runAnalysis();
			System.out.println("Added schema with guid " + schemaGuid + ".");
			System.out.println(SerializationUtility.serialize(
					H2DataAccessObject.getInstance().getSchemaByGuid(schemaGuid, true)));
			System.out.println("Would you like to run another analysis? (y/n)");
			if (scanner.nextLine().equals("y")) {
				List<String> f = new ArrayList<String>();
				do {
					System.out.println("Enter file name: ");
					f.add(scanner.nextLine());
					System.out.println("Another? (y/n) ");
				} while (scanner.nextLine().equals("y"));
				for (int i = 0; i < f.size(); i++) {
					files = f;
				}
			} else {
				break;
			}
		} while (files.size() > 0);
		scanner.close();
	}

}
