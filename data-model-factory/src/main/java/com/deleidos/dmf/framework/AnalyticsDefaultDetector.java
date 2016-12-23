package com.deleidos.dmf.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.mime.MimeTypes;

import com.deleidos.dmf.framework.AnalyticsDetectorWrapper.DetectionResult;
import com.deleidos.dmf.framework.AnalyticsEmbeddedDocumentExtractor.ExtractedContent;
import com.deleidos.dmf.progressbar.ProgressBarManager;
import com.deleidos.dmf.progressbar.ProgressState;
import com.deleidos.dmf.progressbar.ProgressState.STAGE;
import com.deleidos.dmf.web.SchemaWizardSessionUtility;

/**
 * Detector class that contains an instance of all other detectors.  This class will iterate through the other detectors
 * and attempt to find the match with the highest confidence.  This class is not meant to be subclassed.
 * @author leegc
 *
 */
public class AnalyticsDefaultDetector extends DefaultDetector {
	private static final Logger logger = Logger.getLogger(AnalyticsDefaultDetector.class);
	private MediaTypeRegistry registry; 
	public static final String HAS_BODY_CONTENT = "has-body-content";
	public static final String BODY_CONTENT_TYPE = "body-content-type";
	private static final long FILE_CUTOFF_IN_BYTES = 1024 * 1024 * 1024;
	private ProgressBarManager progressBar = null;
	private String sessionId = null;

	public AnalyticsDefaultDetector() {
		this.registry = MimeTypes.getDefaultMimeTypes().getMediaTypeRegistry();
	}
	
	public void enableProgressUpdates(String sessionId, ProgressBarManager progressBar) {
		this.sessionId = sessionId;
		this.progressBar = progressBar;
	}
	
	public void disableProgressUpdates() {
		this.progressBar = null;
		this.sessionId = null;
	}

	@Override
	public MediaType detect(InputStream input, Metadata metadata) throws IOException { 
		if(metadata.get(HAS_BODY_CONTENT) != null 
				&& metadata.get(HAS_BODY_CONTENT).equals(Boolean.FALSE.toString())
				&& metadata.get(Metadata.CONTENT_TYPE) != null) {
			return MediaType.parse(metadata.get(Metadata.CONTENT_TYPE));
		}
		
		long fileSize = metadata.get(Metadata.CONTENT_LENGTH) != null ? Long.valueOf(metadata.get(Metadata.CONTENT_LENGTH)) : -1;
		
		List<Detector> detectors;
		if(fileSize > FILE_CUTOFF_IN_BYTES) {
			detectors = getStateBasedDetectors();
		} else {
			detectors = getDetectors();
		}
		
		int numDetectors = detectors.size();
		
		if(progressBar != null) {
			progressBar.goToNextStateIfCurrentIs(ProgressState.STAGE.UPLOAD);
			SchemaWizardSessionUtility.getInstance().updateProgress(progressBar, sessionId);
			progressBar.split(numDetectors);
		}
		
		DetectionResult bestResult = AnalyticsDetectorWrapper.UNDETECTABLE;
		for (Detector detector : detectors) {
			AnalyticsDetectorWrapper wrapper = AnalyticsDetectorWrapper.newInstance(detector);
			//need field in H2 that is body-content-type
			DetectionResult detectionResult = wrapper.wrappedDetect(input, metadata);
			
			if(progressBar != null) {
				progressBar.goToNextStateIfCurrentIs(STAGE.SPLIT);
				progressBar.updateNumeratorInRequiredState(progressBar.getCurrentState().getStartValue(), STAGE.SPLIT);
				SchemaWizardSessionUtility.getInstance().updateProgress(progressBar, sessionId);
			}
			
			if(detectionResult.getMediaType().equals(MediaType.OCTET_STREAM)) {
				continue;
			}
			
			if (registry.isSpecializationOf(detectionResult.getMediaType(), bestResult.getMediaType())) {
				if (detectionResult.getConfidence() >= bestResult.getConfidence()) { 
					bestResult = detectionResult;
				}
			}
		}
		
		if(progressBar != null) {
			progressBar.jumpToEndOfSplits();
		}
		
		if(metadata.get(HAS_BODY_CONTENT) == null) {
			// only change if it has not been set (parser will use this to stop recursive parse)
			if (!bestResult.getCouldHaveBodyContent()) {
				logger.debug("Body content parsing disabled for " + bestResult.getMediaType() + ".");
			}
			metadata.set(AnalyticsDefaultDetector.HAS_BODY_CONTENT, 
					bestResult.getCouldHaveBodyContent().toString());
		}
		
		return bestResult.getMediaType();
	}
	
	public List<Detector> getStateBasedDetectors() {
		List<Detector> detectors = getDetectors();
		Iterator<Detector> iDetector = detectors.iterator();
		while(iDetector.hasNext()) {
			Detector detector = iDetector.next();
			if((detector instanceof AbstractMarkSupportedAnalyticsDetector)) {
				iDetector.remove();
			}
		}
		return detectors;
	}
	
	public MediaType runSpecialZipDetection(String extractedContentDirectory, 
			Metadata metadata, List<CompositeTypeDetector> compositeTypeDetectors,
			List<ExtractedContent> extractedContent) {
		for(CompositeTypeDetector specialZipDetector : compositeTypeDetectors) {
			Metadata freshMetadata = new Metadata();
			MediaType mediaType = specialZipDetector
					.detectSpecial(extractedContentDirectory, freshMetadata, extractedContent);
			if(!mediaType.equals(MediaType.OCTET_STREAM) && mediaType != null) {
				String names[] = freshMetadata.names();
				for (int i = 0; i < names.length; i++) {
					metadata.add(names[i], freshMetadata.get(names[i]));
				}
				return mediaType;
			}
		}
		return MediaType.OCTET_STREAM;
	}
	
}
