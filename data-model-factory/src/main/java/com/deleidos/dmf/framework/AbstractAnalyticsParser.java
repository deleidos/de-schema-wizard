package com.deleidos.dmf.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.deleidos.dmf.exception.AnalyticsCancelledWorkflowException;
import com.deleidos.dmf.exception.AnalyticsEmbeddedContentException;
import com.deleidos.dmf.exception.AnalyticsInitializationRuntimeException;
import com.deleidos.dmf.exception.AnalyticsParsingRuntimeException;
import com.deleidos.dmf.exception.AnalyticsTikaProfilingException;
import com.deleidos.dmf.exception.AnalyzerException;
import com.deleidos.dmf.framework.AbstractAnalyticsParser.ProgressUpdatingBehavior;
import com.deleidos.dmf.progressbar.SimpleProgressUpdater;
import com.deleidos.dmf.web.SchemaWizardSessionUtility;
import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.profiler.SampleProfiler;
import com.deleidos.dp.profiler.SampleSecondPassProfiler;
import com.deleidos.dp.profiler.SchemaProfiler;
import com.deleidos.dp.profiler.api.Profiler;
import com.deleidos.dp.profiler.api.ProfilerRecord;
import com.deleidos.dp.profiler.api.ProfilingProgressUpdateHandler;

/**
 * The Analytics implementation of Tika Parsers.  This class is a protective abstract class that handles all loading of 
 * metrics data into the appropriate profiler.  The idea of a given subclasses
 * is to convert records of a stream into {@link ProfilerRecord}.  This delegates record splitting functionality 
 * to subclasses and allows the framework to process streams or large files given a functional parser.  
 * <b> You must add the fully qualified class name to this 
 * project's src/main/resources/META-INF/services/org.apache.tika.parser.Parser file. </b>
 * @author leegc
 *
 */
public abstract class AbstractAnalyticsParser implements TikaProfilableParser {
	/**
	 * 
	 */
	private static final long serialVersionUID = -463118690334548751L;
	private static long NOTICEABLE_WAIT_CUTOFF = 15 * 1000;
	private static final Logger logger = Logger.getLogger(AbstractAnalyticsParser.class);
	public static final int RECORD_LIMIT = 1000000;
	private boolean interrupt = false;
	protected String fileName = null;

	public enum ProgressUpdatingBehavior {
		BY_CHARACTERS_READ, BY_RECORD_COUNT, BY_COMMON_FIELD_OCCURANCES, IN_PARSER
	}
	
	private void profileAllRecords(TikaAnalyzerParameters<?> profilableParameters) throws AnalyticsTikaProfilingException, SAXException, IOException {
		InputStream inputStream = profilableParameters.getStream();
		ContentHandler handler = profilableParameters.getHandler();
		Metadata metadata = profilableParameters.getMetadata();

		long parsingStartTime = System.currentTimeMillis();
		profilableParameters.setParsingStartTime(parsingStartTime);
		try {
			
			// verify that the parameters have everything required for Schema Wizard
			verifyRequiredParseContext(inputStream, handler, metadata, profilableParameters);

			// run the pre parse initialization method
			preParse(inputStream, handler, metadata, profilableParameters);

			// run the parsing method, which calls getNextProfilerRecord()
			parse(inputStream, handler, metadata, profilableParameters);
			
			long t2 = System.currentTimeMillis();
			logger.debug("Parsing for " + getSupportedTypes(profilableParameters) + " took " + (t2 - parsingStartTime) + " millis.");	
		
			if(SchemaWizardSessionUtility.getInstance().isCancelled(profilableParameters.getSessionId())) {
				throw new AnalyticsCancelledWorkflowException("Workflow cancelled during parsing.");
			}

		} catch (TikaException | SAXException e) {
			if(SchemaWizardSessionUtility.getInstance().isCancelled(profilableParameters.getSessionId())) {
				throw new AnalyticsCancelledWorkflowException("Workflow cancelled during parsing.");
			} 
			long t2 = System.currentTimeMillis();
			logger.error("Parsing for " + getSupportedTypes(profilableParameters) + " failed after " + (t2 - parsingStartTime) + " millis.");
			throw new AnalyticsTikaProfilingException(e);
		} finally {
			// run the post parse method to clean up any resources
			postParse(handler, metadata, profilableParameters);
		}
	}

	/**
	 * Optional method that will run once before the parse method is called.  Useful for
	 * headers.  This method will not be called if parseAllRecords returns true.  
	 * This method has an empty body in AnalyticsTikaParser.java.
	 * 
	 * @param inputStream The stream that will be parsed.
	 * @param handler the handler context passed to the AnalyticsTikaParser
	 * @param metadata The metadata of the given stream (at minimum contains Metadata.CONTENT_TYPE and SampleProfiler.SOURCE_NAME)
	 * @param context the parsing context passed to the AnalyticsTikaParser
	 * @param splitter The splitter that will split the stream.
	 * @throws AnalyticsTikaProfilingException a checked exception that should stop the parsing and be reported
	 */
	public void preParse(InputStream inputStream, ContentHandler handler, Metadata metadata, TikaAnalyzerParameters<?> context) throws AnalyticsTikaProfilingException {
		return;
	}
	
	/**
	 *  Parse method implemented for all Analytics parsers.  A subclass' getNextProfilerRecord() method will be called until
	 *  it returns null, interruptParse() is called, or an Exception is thrown.  When adding to the Analytics framework, 
	 *  one should not need to override this method.
	 */
	@Override
	public void parse(InputStream stream, ContentHandler handler,
			Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
		// appropriate context variables have been verified before this method is called
		TikaAnalyzerParameters<?> params = (TikaAnalyzerParameters<?>) context;
		long parsingStartTime = params.getParsingStartTime();
		Profiler<?> profiler = params.getProfiler();
		ProfilingProgressUpdateHandler progressUpdater = params.get(ProfilingProgressUpdateHandler.class);

		try {
			ProfilerRecord record = null;
			// only send generic progress updates when we are parsing by characters read
			boolean sendProgressDescriptionUpdates = params.getProgressUpdatingBehavior().equals(
							ProgressUpdatingBehavior.BY_CHARACTERS_READ);

			for (int i = 0; i < RECORD_LIMIT; i++) {
				record = getNextProfilerRecord(stream, handler, metadata, params);
				if (record == null || interrupt) {
					if (interrupt) {
						logger.warn("Parsing interrupted after " + i + " records.");
					}
					break;
				} 
				profiler.accumulate(record);

				if (SchemaWizardSessionUtility.getInstance().isCancelled(params.getSessionId())) {
					return;
					// cancel has occurred, but can't throw appropriate checked exception here
					// will be checked and thrown again in the profileAllRecords method
				} else if (sendProgressDescriptionUpdates &&
						parsingStartTime - System.currentTimeMillis() > NOTICEABLE_WAIT_CUTOFF) {
					initializeDetailedProgressUpdates(progressUpdater);
					sendProgressDescriptionUpdates = false;
					// should only be called once, the progress updater will then handle the description
				}

				switch (params.getProgressUpdatingBehavior()) {
				case BY_CHARACTERS_READ: progressUpdater.handleProgressUpdate(params.getCharsRead()); break;
				case BY_RECORD_COUNT: progressUpdater.handleProgressUpdate(i); break;
				case BY_COMMON_FIELD_OCCURANCES: break; // progress is handled in accumulators
				case IN_PARSER: break; // progress is handled in parser
				}
				
			}
		} catch (AnalyticsTikaProfilingException e) {
			throw new AnalyticsParsingRuntimeException("Profiling exception from parser "+this.getClass().getName()+".", e, this);
		} 
	}

	/**
	 * Optional method that will run once after the parse method is called.  Note that subclasses should
	 * <b>not</b> close the source input stream, which is why it is left out of this method signature.
	 * @param handler
	 * @param metadata
	 * @param context
	 * @throws AnalyticsTikaProfilingException a checked exception that should stop the parsing and be reported
	 */
	public void postParse(ContentHandler handler, Metadata metadata, TikaAnalyzerParameters<?> context) throws AnalyticsTikaProfilingException {
		return;
	}
	
	public void verifyRequiredParseContext(InputStream stream, ContentHandler handler, 
			Metadata metadata, ParseContext context) {
		TikaAnalyzerParameters<?> params = (TikaAnalyzerParameters<?>) context;
		if(!(context instanceof TikaAnalyzerParameters)) {
			throw new AnalyticsInitializationRuntimeException("Context is not an instance of TikaProfilerParameters.");
		}
		
		Profiler<?> profiler = params.getProfiler(); 
		if(profiler == null) {
			throw new AnalyticsInitializationRuntimeException("Profiler not defined in context.");
		}

		File file = params.get(File.class);
		if (file != null) {
			fileName = file.getName();
		} else {
			throw new AnalyticsInitializationRuntimeException("File not set in parsing context.");
		}

		ProfilingProgressUpdateHandler progressUpdater =
				context.get(ProfilingProgressUpdateHandler.class);
		if(progressUpdater == null) {
			throw new AnalyticsInitializationRuntimeException("Progress updater not defined in context.");
		} else {
			if(profiler instanceof SampleProfiler) {
				((SampleProfiler)profiler).setProgressUpdateListener(progressUpdater);
			} else if(profiler instanceof SampleSecondPassProfiler) {
				((SampleSecondPassProfiler)profiler).setProgressUpdateListener(progressUpdater);
			} else if(profiler instanceof SchemaProfiler) {
				((SchemaProfiler)profiler).setProgressUpdateListener(progressUpdater);
			}
		}

	}
	
	private void initializeDetailedProgressUpdates(ProfilingProgressUpdateHandler progressUpdater) {
		if (progressUpdater instanceof SimpleProgressUpdater &&
			!((SimpleProgressUpdater) progressUpdater).getDescriptionCallback().isPresent()) {
				// if the description callback is not set, set it to be a general update for the
				// number of records that have been parsed
				((SimpleProgressUpdater) progressUpdater).setDescriptionCallback((updater, progress) -> 
					generateNumberOfRecordsDescription(fileName, progress));
		}
	}

	/**
	 * The method that should be implemented when adding parsers to the framework.  Read the stream and parse a single
	 * record out of it.  The ProfilerRecord result is a map of field keys to a list of its values.  This method allows
	 * the framework to gather metrics in an efficient, "unlimited" manner.  Keys in the object are used as headers, or names,
	 * for each field, and the values are used to accumulate metrics.  Subclasses can simply use the 
	 * DefaultProfilerRecord in the com.deleidos.dp.profiler package 
	 * to accumulate records, though they may implement a different strategy if desired.  Return null when parsing has
	 * been completed.
	 *  
	 * @return a profiler record, or null if parsing is completed.
	 */
	@Override
	public abstract ProfilerRecord getNextProfilerRecord(InputStream inputStream, ContentHandler handler, Metadata metadata, TikaAnalyzerParameters<?> context) throws AnalyticsTikaProfilingException;

	public void interruptParse() {
		interrupt = true;
	}

	public boolean isInterrupt() {
		return interrupt;
	}

	public void setInterrupt(boolean interrupt) {
		this.interrupt = interrupt;
	}

	/**
	 * Implementation of sampleAnalysis so Analyzers other then TikaAnalyzer may use the parsers if desired.  Need to call
	 * setInputStream, setMetadata, and setContentHandler to use outside of the TikaAnalyzer framework. 
	 */
	@Override
	public TikaSampleAnalyzerParameters runSampleAnalysis(TikaSampleAnalyzerParameters sampleProfilableParams)
			throws AnalyzerException {
		try {
			profileAllRecords(sampleProfilableParams);
		} catch (SAXException | IOException e) {
			throw new AnalyticsTikaProfilingException(e);
		} 
		return sampleProfilableParams;
	}

	/**
	 * Implementation of schemaAnalysis so Analyzers other then TikaAnalyzer may use the parsers if desired.  Need to call
	 * setInputStream, setMetadata, and setContentHandler to use outside of the TikaAnalyzer framework. 
	 */
	@Override
	public TikaSchemaAnalyzerParameters runSchemaAnalysis(TikaSchemaAnalyzerParameters schemaProfilableParams)
			throws AnalyzerException {
		try {
			profileAllRecords(schemaProfilableParams);
		} catch (SAXException | IOException e) {
			throw new AnalyticsTikaProfilingException(e);
		} 
		return schemaProfilableParams;
	}

	protected static String generateNumberOfRecordsDescription(String sampleName, long recordsParsed) {
		return sampleName + ": Parsing field (" + recordsParsed + " records parsed).";
	}
	
	protected static String generateNumberOfRecordsDescription(String sampleName, long recordsParsed, long totalExpected) {
		return sampleName + ": Parsing fields ("
				+String.valueOf(recordsParsed)+"/"+totalExpected+
				" records).";
	}

}
