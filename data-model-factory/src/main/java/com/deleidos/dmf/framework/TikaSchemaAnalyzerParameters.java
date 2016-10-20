package com.deleidos.dmf.framework;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.deleidos.dmf.exception.AnalyticsRuntimeException;
import com.deleidos.dmf.progressbar.ProgressBarManager;
import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.profiler.SchemaProfiler;
import com.deleidos.dp.profiler.api.Profiler;

/**
 * Class that is specifically used to hold components of a schema analysis process.
 * @author leegc
 *
 */
public class TikaSchemaAnalyzerParameters extends TikaAnalyzerParameters<Schema> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2710971238389516736L;

	private Schema existingSchema;
	private JSONArray modifiedSampleList;
	private JSONObject proposedSchemaAnalysis;		
	private List<DataSample> userModifiedSampleList;
	private Map<String, MostCommonFieldWithWalking> mostCommonFieldWithWalkingCount;

	public TikaSchemaAnalyzerParameters(SchemaProfiler profiler, ProgressBarManager progressBar, String uploadDir, 
			String guid, String domainName, List<DataSample> dataSampleList) {
		super(profiler, progressBar, uploadDir, guid);
		userModifiedSampleList = dataSampleList;
		this.setDomainName(domainName);
	}

	public JSONArray getModifiedSampleList() {
		return modifiedSampleList;
	}

	public void setModifiedSampleList(JSONArray modifiedSampleList) {
		this.modifiedSampleList = modifiedSampleList;
	}

	public JSONObject getProposedSchemaAnalysis() {
		return proposedSchemaAnalysis;
	}

	public void setProposedSchemaAnalysis(JSONObject proposedSchemaAnalysis) {
		this.proposedSchemaAnalysis = proposedSchemaAnalysis;
	}

	public List<DataSample> getUserModifiedSampleList() {
		return userModifiedSampleList;
	}

	public void setUserModifiedSampleList(List<DataSample> userModifiedSampleList) {
		this.userModifiedSampleList = userModifiedSampleList;
	}

	@Override
	public Schema getProfilerBean() {
		SchemaProfiler schemaProfiler = (SchemaProfiler) super.profiler;
		Schema bean = schemaProfiler.finish();
		Schema schemaBean = (Schema) bean;
		schemaBean.setsGuid(getGuid());
		schemaBean.setRecordsParsedCount(schemaProfiler.getRecordsParsed());
		schemaBean.setsDataSamples(schemaProfiler.getDataSampleMetaDataList());
		schemaBean.setsDomainName(this.getDomainName());
		return schemaBean;
	}

	public Schema getExistingSchema() {
		return existingSchema;
	}

	public void setExistingSchema(Schema existingSchema) {
		this.existingSchema = existingSchema;
	}

	public Map<String, MostCommonFieldWithWalking> getMostCommonFieldWithWalkingCount() {
		return mostCommonFieldWithWalkingCount;
	}

	public void setMostCommonFieldWithWalkingCount(Map<String, MostCommonFieldWithWalking> mostCommonFieldWithWalkingCount) {
		this.mostCommonFieldWithWalkingCount = mostCommonFieldWithWalkingCount;
	}

	@Override
	public SchemaProfiler getProfiler() {
		return (SchemaProfiler) profiler;
	}
}
