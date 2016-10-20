package com.deleidos.dmf.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.pdfbox.io.IOUtils;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.deleidos.dmf.exception.AnalyticsEmbeddedContentException;
import com.deleidos.dmf.exception.AnalyticsInitializationRuntimeException;
import com.deleidos.dmf.exception.AnalyticsRuntimeException;
import com.deleidos.dp.profiler.api.ProfilingProgressUpdateHandler;

/**
 * Extractor that can be injected into the Tika framework.  Pulls out embedded data from a given stream, and recursively
 * attempts to pull out more data.  Writes this data a separate file on disk, so it can be retrieved later.
 * @author leegc
 *
 */
public class AnalyticsEmbeddedDocumentExtractor implements EmbeddedDocumentExtractor {
	public static final String EXTRACTED_CONTENT_KEY = "extracted-resource-dir-path";
	public static final String RESOURCE_PATH_KEY = "resource-abs-path";
	private static final Logger logger = Logger.getLogger(AnalyticsEmbeddedDocumentExtractor.class);
	private final File extractedContentDirectory;
	private final TikaAnalyzerParameters<?> parentParameters;
	private boolean isAllContentExtracted;
	private boolean shouldParse;
	private static long EMBEDDED_FILE_LIMIT = 8 * 1024 * 1024 * 100;
	private List<ExtractedContent> extractedContents;

	public AnalyticsEmbeddedDocumentExtractor(TikaAnalyzerParameters<?> params) {
		this(params, null);
	}
	
	public AnalyticsEmbeddedDocumentExtractor(TikaAnalyzerParameters<?> params, File alternateExtractContentDirectory) {
		this.parentParameters = params;
		this.extractedContentDirectory = alternateExtractContentDirectory == null ?
				new File(parentParameters.getExtractedContentDir()) : alternateExtractContentDirectory;
		if(params instanceof TikaSchemaAnalyzerParameters) {
			this.isAllContentExtracted = true;
		} else {
			this.isAllContentExtracted = false;
		}
		shouldParse = true;
		this.extractedContents = new ArrayList<ExtractedContent>();
	}

	@Override
	public boolean shouldParseEmbedded(Metadata metadata) {
		return !isAllContentExtracted;
	}

	@Override
	public void parseEmbedded(InputStream stream, ContentHandler handler, Metadata metadata, boolean outputHtml)
			throws SAXException, IOException {	
		try {
			Detector detector = parentParameters.get(Detector.class, new AnalyticsDefaultDetector());
			
			File extractingFile = createSubdirectoriesAndFile(extractedContentDirectory, metadata);
			logger.info("Extracting embedded content: " + extractingFile.getName() + ".");
			TemporaryResources tmp = new TemporaryResources();
			TikaInputStream tis = TikaInputStream.get(stream, tmp);
			
			try {
				MediaType type = detector.detect(tis, metadata);
				metadata.set(Metadata.CONTENT_TYPE, type.toString());

				// we are going to write embedded content to disk, so create the embedded content directory
				if (!extractedContentDirectory.exists() && !extractedContentDirectory.mkdir()) {
					throw new IOException("Could not create embedded document directory.");
				}
				extractingFile = writeToFile(extractingFile, tis, handler, metadata, EMBEDDED_FILE_LIMIT);

				if(extractingFile.length() == 0) {
					throw new AnalyticsInitializationRuntimeException("Embedded document " + extractingFile.getName() + " is empty.");
				}
				metadata.set(Metadata.RESOURCE_NAME_KEY, extractingFile.getName());
				ExtractedContent content = new ExtractedContent(extractingFile, metadata);
				extractedContents.add(content);

				Parser nestedParser = parentParameters.get(Parser.class);
				if(!(nestedParser instanceof AnalyticsDefaultParser)) {
					throw new AnalyticsInitializationRuntimeException("Parser is not anlytics default parser.");
				} else {

					Parser nestedContentExtractionParser = ((AnalyticsDefaultParser)nestedParser).getParser(metadata, parentParameters);
					if(nestedContentExtractionParser instanceof TikaProfilableParser) {
						return;
					} else {
						logger.info("Further extraction with " + nestedContentExtractionParser.getClass().getSimpleName() +".");
						FileInputStream fis = new FileInputStream(extractingFile);
						TemporaryResources nestedTmp = new TemporaryResources();
						TikaInputStream nestedTis = TikaInputStream.get(fis, nestedTmp);

						File nestedExtractedContentDir = getOrCreateExtractedContentDirectory(
								extractedContentDirectory.getAbsolutePath(), extractingFile.getName(), "-embedded");
						AnalyticsEmbeddedDocumentExtractor nestedAnalyticsExtractor = 
								new AnalyticsEmbeddedDocumentExtractor(parentParameters, nestedExtractedContentDir);
						TikaAnalyzerParameters<?> params = initializeTikaProfilingParameters(nestedTis, handler, metadata);
						params.setExtractedContentDir(nestedExtractedContentDir.getAbsolutePath());
						params.set(EmbeddedDocumentExtractor.class, nestedAnalyticsExtractor);
						nestedContentExtractionParser.parse(nestedTis, handler, metadata, params);
						this.extractedContents.addAll(nestedAnalyticsExtractor.getExtractedContents());
					}
				}
			} catch (AnalyticsEmbeddedContentException e) {
				throw new SAXException(e);
			} catch (Exception e) {
				logger.error(e);
				throw new AnalyticsRuntimeException("Error extracting embedded document.", e);
			} finally {
				tmp.dispose();
			}

		} catch (TikaException e) {
			logger.error("Error parsing embedded document.", e);
		} 
	} 

	public void initAlreadyExtractedContentFromDisk() throws IOException, TikaException {
		// contents are already on disk
		// don't extract, just detect metadata and add to extracted content list
		if (parentParameters.getExtractedContentDir() != null) {
			Detector detector = parentParameters.get(Detector.class, new AnalyticsDefaultDetector());
			File baseDir = new File(parentParameters.getExtractedContentDir());
			extractedContents = recursiveInitExtractedContentFromDisk(detector, baseDir);
		}
		/*if(parentParameters.getExtractedContentDir() == null) {
			logger.info("No extracted content found.");
		} else {
			File extractedDir = new File(parentParameters.getExtractedContentDir());
			for(File extractedFile : extractedDir.listFiles()) {
				TemporaryResources tmp = new TemporaryResources();
				TikaInputStream tis = TikaInputStream.get(new FileInputStream(extractedFile), tmp);
				try {
					Metadata reinitializedMetadata = new Metadata();
					MediaType type = detector.detect(tis, reinitializedMetadata);
					reinitializedMetadata.set(Metadata.CONTENT_TYPE, type == null 
							? MediaType.OCTET_STREAM.toString() : type.toString());
					reinitializedMetadata.set(Metadata.RESOURCE_NAME_KEY, extractedFile.getName());
					ExtractedContent content = new ExtractedContent(extractedFile, reinitializedMetadata);
					extractedContents.add(content);
				} finally {
					tmp.dispose();
				}
			}
		}*/
	}

	private List<ExtractedContent> recursiveInitExtractedContentFromDisk(Detector detector, File directory) throws IOException, TikaException {
		List<ExtractedContent> extractedContents = new ArrayList<ExtractedContent>();
		if(directory == null) {
			throw new IOException("Null directory passed.");
		} else if(!directory.exists()) {
			logger.debug("No embedded content directory found " + directory + " (does not exist).  " + 
					"This is not necessarily an error, but could be if embedded content was expected.");
		} else {
			for(File extractedFile : directory.listFiles()) {
				if (extractedFile.isFile()) {
					TemporaryResources tmp = new TemporaryResources();
					TikaInputStream tis = TikaInputStream.get(new FileInputStream(extractedFile), tmp);
					try {
						Metadata reinitializedMetadata = new Metadata();
						MediaType type = detector.detect(tis, reinitializedMetadata);
						reinitializedMetadata.set(Metadata.CONTENT_TYPE, type.toString());
						reinitializedMetadata.set(Metadata.RESOURCE_NAME_KEY, extractedFile.getName());
						ExtractedContent content = new ExtractedContent(extractedFile, reinitializedMetadata);
						extractedContents.add(content);
					} finally {
						tmp.dispose();
					}
				} else {
					extractedContents.addAll(recursiveInitExtractedContentFromDisk(detector, extractedFile));
				}
			}
		}
		return extractedContents;
	}

	private File createSubdirectoriesAndFile(File baseExtractDirectory, Metadata metadata) {
		final String defaultName = "embedded-document";

		/*String embeddedDocumentName = (metadata.get(Metadata.RESOURCE_NAME_KEY) != null) ? metadata.get(Metadata.RESOURCE_NAME_KEY) : defaultName;

		File embeddedDocumentFile = new File(embeddedDocumentName);
		embeddedDocumentName = embeddedDocumentFile.getName();
		if(embeddedDocumentName.contains(File.separator)) {
			embeddedDocumentName = embeddedDocumentName.substring(0, embeddedDocumentName.lastIndexOf(File.separator));
		}
		Set<String> files = new HashSet<String>();
		for(String file : new File(parentParameters.getExtractedContentDir()).list()) {
			files.add(file);
		}*/

		String embeddedDocumentName = (metadata.get(Metadata.RESOURCE_NAME_KEY) != null) ? 
				metadata.get(Metadata.RESOURCE_NAME_KEY) : defaultName;


				File embeddedDocumentFile = new File(baseExtractDirectory, embeddedDocumentName);
				if (!embeddedDocumentFile.getParentFile().exists()) {
					if (!embeddedDocumentFile.getParentFile().mkdirs()) {
						throw new AnalyticsRuntimeException("IO Exception caused unsuccessful "
								+ "package extraction", 
								new IOException("Could not created zipped subdirectory."));
					}
				}

				return embeddedDocumentFile;
	}

	public TikaAnalyzerParameters<?> getParentParameters() {
		return parentParameters;
	}

	private File writeToFile(File embeddedDocument, InputStream stream, ContentHandler handler, Metadata metadata, 
			long sizeCutoff) throws AnalyticsEmbeddedContentException, IOException {
		
		if (embeddedDocument.length() > sizeCutoff) {
			throw new AnalyticsEmbeddedContentException("Embedded content exceeds file size limit of " + sizeCutoff + ".");
		}
		FileOutputStream fos = new FileOutputStream(embeddedDocument);
		IOUtils.copy(stream, fos);
		fos.close();

		return embeddedDocument;
	}

	public TikaAnalyzerParameters<?> initializeTikaProfilingParameters(InputStream inputStream, ContentHandler handler, Metadata metadata) throws IOException {
		TikaAnalyzerParameters<?> parameters;
		if(parentParameters instanceof TikaSampleAnalyzerParameters) {
			TikaSampleAnalyzerParameters sampleParams = (TikaSampleAnalyzerParameters) parentParameters; 
			parameters = new TikaSampleAnalyzerParameters(sampleParams.getProfiler(), sampleParams.getProgressBar(),
					sampleParams.getUploadFileDir(), sampleParams.getGuid(), inputStream, handler, metadata);
			parameters.set(ProfilingProgressUpdateHandler.class, sampleParams.get(ProfilingProgressUpdateHandler.class));
			((TikaSampleAnalyzerParameters)parameters).setRecordsInSample(sampleParams.getRecordsInSample());
		} else if(parentParameters instanceof TikaSchemaAnalyzerParameters) {
			TikaSchemaAnalyzerParameters schemaParams = (TikaSchemaAnalyzerParameters) parentParameters;
			parameters = new TikaSchemaAnalyzerParameters(schemaParams.getProfiler(), schemaParams.getProgressBar(),
					schemaParams.getUploadFileDir(), schemaParams.getGuid(), schemaParams.getDomainName(), 
					schemaParams.getUserModifiedSampleList());
			parameters.set(ProfilingProgressUpdateHandler.class, schemaParams.get(ProfilingProgressUpdateHandler.class));
			parameters.setStream(inputStream);
			parameters.setHandler(handler);
			parameters.setMetadata(metadata);
		} else {
			throw new AnalyticsRuntimeException("Parameters not defined as sample or schema parameters.");
		}
		parameters.setStreamLength(metadata.get(Metadata.CONTENT_LENGTH) != null ? Integer.valueOf(metadata.get(Metadata.CONTENT_LENGTH)) : 0);
		File parentFile = parentParameters.get(File.class);
		parameters.set(File.class, parentFile);
		parameters.set(Detector.class, parentParameters.get(Detector.class));
		parameters.set(Parser.class, parentParameters.get(Parser.class));
		parameters.set(EmbeddedDocumentExtractor.class, parentParameters.get(EmbeddedDocumentExtractor.class));
		parameters.set(PDFParserConfig.class, parentParameters.get(PDFParserConfig.class));
		parameters.setSessionId(parentParameters.getSessionId());
		parameters.setProgressUpdatingBehavior(parentParameters.getProgressUpdatingBehavior());
		parameters.set(ProfilingProgressUpdateHandler.class, parentParameters.get(ProfilingProgressUpdateHandler.class));
		//EmbeddedDocumentExtractor extractor= new ParsingEmbeddedDocumentExtractor(parameters);
		//parameters.set(EmbeddedDocumentExtractor.class, extractor);
		return parameters;
	}
	
	public static File getOrCreateExtractedContentDirectory(String baseDir, String sampleName, String suffix) throws IOException {
		int dotIndex = sampleName.lastIndexOf('.');
		if (dotIndex > -1) {
			sampleName = sampleName.substring(0, dotIndex);
		}
		String embeddedContentDirName = sampleName + suffix;
		File extractedContentDirectory = new File(baseDir, embeddedContentDirName);
		return extractedContentDirectory;
	}

	public class ExtractedContent {
		private File extractedFile;
		private Metadata metadata;
		public ExtractedContent(File file, Metadata metadata) {
			setExtractedFile(file);
			setMetadata(metadata);
		}
		public File getExtractedFile() {
			return extractedFile;
		}
		public void setExtractedFile(File extractedFile) {
			this.extractedFile = extractedFile;
		}
		public Metadata getMetadata() {
			return metadata;
		}
		public void setMetadata(Metadata metadata) {
			this.metadata = metadata;
			this.metadata.set(Metadata.CONTENT_LENGTH, String.valueOf(extractedFile.length()));
			this.metadata.set(RESOURCE_PATH_KEY, extractedFile.getAbsolutePath());
		}
	}

	public List<ExtractedContent> getExtractedContents() {
		return extractedContents;
	}

	public void setExtractedContents(List<ExtractedContent> extractedContents) {
		this.extractedContents = extractedContents;
	}

	public boolean areContentsExtracted() {
		return isAllContentExtracted;
	}

	public void setAreContentsExtracted(boolean extractedAllContents) {
		this.isAllContentExtracted = extractedAllContents;
		this.shouldParse = false;
	}

}
