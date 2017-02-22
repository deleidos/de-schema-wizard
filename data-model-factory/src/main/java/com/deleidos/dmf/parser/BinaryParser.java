package com.deleidos.dmf.parser;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.image.ImageMetadataExtractor;
import org.xml.sax.ContentHandler;

import com.deleidos.dmf.exception.AnalyticsParsingRuntimeException;
import com.deleidos.dmf.exception.AnalyticsTikaProfilingException;
import com.deleidos.dmf.framework.AbstractAnalyticsParser;
import com.deleidos.dmf.framework.TikaAnalyzerParameters;
import com.deleidos.dp.profiler.BinaryProfilerRecord;
import com.deleidos.dp.profiler.DefaultProfilerRecord;
import com.deleidos.dp.profiler.api.ProfilerRecord;
import com.deleidos.hd.enums.DetailType;

/**
 * Profilable parser that handles all binary profiling.  Note, this class simply pushes the bytes of the binary to the profiler.
 * @author leegc
 *
 */
public class BinaryParser extends AbstractAnalyticsParser {
	private static final Logger logger = Logger.getLogger(BinaryParser.class);
	public static final int IMAGE_BUFFER_SIZE = 1000000;
	private final static String ENABLE_BINARY_PARSING = "ENABLE_BINARY_PARSING";
	private String mediaType;
	private String name;
	private boolean binaryParsingEnabled = false;

	private final static Map<String, DetailType> types = new HashMap<String, DetailType>();
	private final static Set<MediaType> mediaTypes = new HashSet<MediaType>();
	static {
		// need to store a mapping because there seems to be some issues with media type equivalence
		types.put(MediaType.image("jpeg").toString(), DetailType.IMAGE);
		types.put(MediaType.image("png").toString(), DetailType.IMAGE);
		mediaTypes.addAll(
				types.keySet().parallelStream()
				.map(MediaType::parse)
				.collect(Collectors.toList())
		);
	}

	@Override
	public void preParse(InputStream inputStream, ContentHandler handler, Metadata metadata,
			TikaAnalyzerParameters context) throws AnalyticsTikaProfilingException {
		String enableBinary;
		if((enableBinary = System.getenv(ENABLE_BINARY_PARSING)) != null) {
			binaryParsingEnabled = ("true".equals(enableBinary) || "1".equals(enableBinary)) ? true : false; 
		}
		mediaType = (metadata.get(Metadata.CONTENT_TYPE) != null) ? metadata.get(Metadata.CONTENT_TYPE) : mediaType;
		if(metadata.get(Metadata.RESOURCE_NAME_KEY) != null) {
			name = metadata.get(Metadata.RESOURCE_NAME_KEY);
		} else if(context.get(File.class) != null) {
			name = context.get(File.class).getName();
		} else if(metadata.get(Metadata.CONTENT_TYPE) != null){
			name = metadata.get(Metadata.CONTENT_TYPE);
		} else {
			name = "binary";
		}
	}

	@Override
	public ProfilerRecord getNextProfilerRecord(InputStream stream, ContentHandler handler, Metadata metadata,
			TikaAnalyzerParameters context) throws AnalyticsTikaProfilingException {
		if(!binaryParsingEnabled) {
			return null;
		}

		try {
			byte[] bytes = new byte[2048];
			ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
			int numBytesRead = stream.read(bytes);
			context.setCharsRead(numBytesRead);
			if(numBytesRead > 0) {
				if(!types.containsKey(mediaType.toString())) {
					throw new AnalyticsParsingRuntimeException("Required media type was not set for binary parser.", this);
				}
				BinaryProfilerRecord binaryRecord = new BinaryProfilerRecord(name, types.get(mediaType), byteBuffer);
				return binaryRecord;
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new AnalyticsTikaProfilingException("Error profiling binary object.", e);
		}
	}

	@Override
	public void postParse(ContentHandler handler, Metadata metadata, TikaAnalyzerParameters<?> context) throws AnalyticsTikaProfilingException {
		super.postParse(handler, metadata, context);
		if (binaryParsingEnabled) {
			if (MediaType.image("jpeg").toString().equals(this.mediaType)) try {
				String objectName = name.contains(DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER)
						? name.substring(0, name.lastIndexOf(DefaultProfilerRecord.STRUCTURED_OBJECT_APPENDER))
								: name;
						Metadata imageMetadata = new Metadata();
						ImageMetadataExtractor metadataExtractor = new ImageMetadataExtractor(imageMetadata);
						metadataExtractor.parseJpeg(context.get(File.class));
						DefaultProfilerRecord rootRecord = new DefaultProfilerRecord();
						rootRecord.put(objectName, new DefaultProfilerRecord(
								Arrays.asList(imageMetadata.names()).stream()
								.collect(Collectors.toMap(Function.identity(), imageMetadata::get)
										)));
						context.getProfiler().accumulate(rootRecord);
			} catch (Exception e) {
				logger.error("Could not extract image metadata.");
				throw new AnalyticsTikaProfilingException(e);
			}
		}
	}

	@Override
	public Set<MediaType> getSupportedTypes(ParseContext context) {
		return mediaTypes;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
