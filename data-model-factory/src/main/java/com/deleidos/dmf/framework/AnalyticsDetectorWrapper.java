package com.deleidos.dmf.framework;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.pkg.PackageParser;

import com.deleidos.dmf.exception.AnalyticsRuntimeException;

/**
 * Wrapper detector built for use with media types that Tika can recognize by default.  "Wrap" an existing Tika detector
 * by passing it into the constructor.  The detector will then work with the framework (more specifically, the ranking
 * system).  As of 1/19/2016, this class does not need to be subclassed.
 * <br>"Note: this class has a natural ordering that is inconsistent with equals."
 * @author leegc
 *
 */
public class AnalyticsDetectorWrapper {
	/**
	 * 
	 */
	public static final Logger logger = Logger.getLogger(AnalyticsDetectorWrapper.class);
	public static final String CONFIDENCE_KEY = "detection-confidence";
	private static final long serialVersionUID = -7004302637534333152L;
	protected Set<MediaType> detectableTypes;
	protected float confidenceInterval = 0.0f;
	private final boolean hasPlainTextBodyContent;
	protected final Detector detector;
	
	static {
		blackListStaticInit();
	}
	
	private static Set<MediaType> bodyContentBlackList;
	
	public static void addToBodyContentBlackList(MediaType blackListedMediaType) {
		bodyContentBlackList.add(blackListedMediaType);
	}
	
	private static void blackListStaticInit() {
		bodyContentBlackList = new HashSet<MediaType>();
		PackageParser packageParser = new PackageParser();
		bodyContentBlackList.addAll(packageParser.getSupportedTypes(null));
		bodyContentBlackList.add(MediaType.TEXT_PLAIN);
		bodyContentBlackList.add(MediaType.OCTET_STREAM);
		packageParser = null;
	}
	
	private static boolean isBodyContentDisabled(MediaType type, Metadata metadata) {
		return bodyContentBlackList.contains(type); 
	}

	private AnalyticsDetectorWrapper(AbstractMarkSupportedAnalyticsDetector analyticsDetector) {
		this.detector = analyticsDetector;
		this.detectableTypes = analyticsDetector.getDetectableTypes();
		this.hasPlainTextBodyContent = detector.getClass().isAnnotationPresent(BodyContent.class);
	}
	
	private AnalyticsDetectorWrapper(Detector detector, Set<MediaType> detectableType) {
		this.detector = detector;
		this.detectableTypes = detectableType;
		this.hasPlainTextBodyContent = true;
	}

	/**
	 * Provides a default "wrapper" around the global detector.  This assigns the necessary <i>confidence</i> and
	 * <i> detectableTypes </i> variables that the Analytics detecting framework needs.  Parsers that do not subclass
	 * the AnalyticsMarkSupportedTikaDetector class will be given a default confidence of .01 because they are
	 * assumed to be a lower priority than Analytics Detectors.
	 */
	public DetectionResult wrappedDetect(InputStream input, Metadata metadata)
			throws IOException {
		MediaType type = detector.detect(input, metadata);
		Boolean couldHaveBodyContent = hasPlainTextBodyContent && !isBodyContentDisabled(type, metadata);
		final Float defaultAnalyticsConfidence = .99f;
		final Float defaultTikaConfidence = .01f;
		final Float defaultOctetStreamConfidence = 0f;
		if (!type.equals(MediaType.OCTET_STREAM)) {
			// only set the confidence is the subclass has not already set it
			if (detector instanceof AnalyticsDetectorWrapper) {
				if (metadata.get(CONFIDENCE_KEY) == null) {
					// expected case, the subclass just returned a type without setting the confidence
					// default to high confidence 99%
					metadata.set(CONFIDENCE_KEY, String.valueOf(defaultAnalyticsConfidence));
				} else {
					if (!NumberUtils.isNumber(metadata.get(CONFIDENCE_KEY))) {
						// subclasses did something weird with the confidence - throw the runtime exception
						throw new AnalyticsRuntimeException(
								"Confidence was set to non number format " + metadata.get(CONFIDENCE_KEY));
					}
					// otherwise use the confidence set by the subclass
				}
			} else {
				// the detector is a Tika detector, give it low confidence so Analytics detectors have priority
				metadata.set(CONFIDENCE_KEY, String.valueOf(defaultTikaConfidence));
			}
		} else {
			// anything that returns octet stream is considered to have 0 confidence
			metadata.set(CONFIDENCE_KEY, String.valueOf(defaultOctetStreamConfidence));
		}
		Float confidence = Float.valueOf(metadata.get(CONFIDENCE_KEY));
		// confidence will not be set for Tika detectors - use .01f
		return new DetectionResult(type, confidence, couldHaveBodyContent);
	}
	
	public static void setConfidence(Metadata metadata, Float confidence) {
		metadata.set(CONFIDENCE_KEY, String.valueOf(confidence));
	}
	
	public Set<MediaType> getDetectableTypes() {
		return detectableTypes;
	}

	public void setDetectableTypes(Set<MediaType> detectableTypes) {
		this.detectableTypes = detectableTypes;
	}
	
	public static AnalyticsDetectorWrapper newInstance(Detector detector) {
		if (detector instanceof AbstractMarkSupportedAnalyticsDetector) {
			return new AnalyticsDetectorWrapper((AbstractMarkSupportedAnalyticsDetector) detector);
		} else {
			return new AnalyticsDetectorWrapper(detector, new HashSet<MediaType>());
		}
	}
	
	public static AnalyticsDetectorWrapper newInstance(Detector detector, Set<MediaType> detectableTypes) {
		return new AnalyticsDetectorWrapper(detector, detectableTypes);
	}
	
	/**
	 * Annotation that identifies detectors whose types may contain some parseable plain text.
	 * Marking a detector implementation with this annotation signifies that the associated parser could
	 * write some plain text to a content handler that may or may not be parseable. In other words,
	 * after the given type is parsed, a recursive detect/parsing call should be made on the file's plain
	 * text content.
	 * 
	 * Marking a detector implementation with this annotation is the same as adding its type to the
	 * <i>bodyContentBlackList</i>.
	 * 
	 * @author leegc
	 *
	 */
	public @interface BodyContent { }
	
	public static DetectionResult UNDETECTABLE = new DetectionResult(MediaType.OCTET_STREAM, 0f, false);
	public static class DetectionResult {
		private final MediaType mediaType;
		private final Float confidence;
		private final Boolean couldHaveBodyContent;
		
		public DetectionResult(MediaType type, Float conf, Boolean bodyContent) {
			mediaType = type;
			confidence = conf;
			couldHaveBodyContent = bodyContent;
		}
		
		public MediaType getMediaType() {
			return mediaType;
		}
		public Float getConfidence() {
			return confidence;
		} 
		public Boolean getCouldHaveBodyContent() {
			return couldHaveBodyContent;
		}
	}
}
