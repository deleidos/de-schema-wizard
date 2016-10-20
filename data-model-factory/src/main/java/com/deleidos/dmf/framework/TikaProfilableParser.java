package com.deleidos.dmf.framework;

import java.io.InputStream;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;

import com.deleidos.dmf.analyzer.Analyzer;
import com.deleidos.dmf.exception.AnalyticsTikaProfilingException;
import com.deleidos.dp.profiler.api.ProfilerRecord;

/**
 * An interface for parsers that comply with the Schema Wizard parsing framework.
 * @author leegc
 *
 */
public interface TikaProfilableParser extends Parser, Analyzer<TikaSampleAnalyzerParameters, TikaSchemaAnalyzerParameters> {

	/**
	 * Get the next profiler record from a given stream.  This method closely resembles the parsing method of the 
	 * Tika framework.  The main difference is that it returns a single record at a time.  Parsers are encouraged to
	 * parse streams based on state, rather than loading entire files into memory.
	 * @param stream the stream to be parsed
	 * @param handler the content handler of the results
	 * @param metadata the metadata associated with the stream
	 * @param context an instance of TikaProfilerParameters that contains Schema Wizard specific configuration
	 * @return an instance of a {@link ProfilerRecord} that will be passed into the Schema Wizard framework.
	 * @throws AnalyticsTikaProfilingException thrown if there is an error that should 1) be reported and 2) prevent
	 * any future calls to the method
	 */
	public ProfilerRecord getNextProfilerRecord(InputStream stream, ContentHandler handler, Metadata metadata, TikaAnalyzerParameters<?> context) throws AnalyticsTikaProfilingException;

}
