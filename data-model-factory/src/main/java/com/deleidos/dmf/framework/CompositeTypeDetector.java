package com.deleidos.dmf.framework;

import java.io.File;
import java.util.List;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

import com.deleidos.dmf.framework.AnalyticsEmbeddedDocumentExtractor.ExtractedContent;

public interface CompositeTypeDetector {
	/**
	 * Detect a special type based on the extracted contents of a compressed file.  When this detector returns a type, the
	 * framework will <i>only</i> call the associated parser for the determined type, rather than iterating through each
	 * file.
	 * @param metadata the metadata that should be populated with necessary information
	 * @param extractedContent a list of the extracted content pulled out of a zip file
	 * @return Metadata that should be used to indicate the type, or null if it is not a special case.
	 *   This metadata should, at a minimum, have the Metadata.CONTENT_TYPE set.  If metadata is null or 
	 *   Metadata.CONTENT_TYPE is octet-stream, it is assumed that no composite type was detected.
	 * 
	 */
	public MediaType detectSpecial(String extractedContentDirectory, Metadata metadata, List<ExtractedContent> extractedContent); 
}
