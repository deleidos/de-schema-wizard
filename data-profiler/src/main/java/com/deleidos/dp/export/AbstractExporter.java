package com.deleidos.dp.export;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.Response.Status;

import com.deleidos.dp.exceptions.H2DataAccessException;
import com.deleidos.dp.exceptions.SchemaNotFoundException;
import com.deleidos.dp.export.SQLExporter.SQLExportException;

public abstract class AbstractExporter implements Exporter {
	private static final String EXPORT_TYPE_KEY = "export-type";
	private static final String SQL_TYPE_IDENTIFIER = "sql";
	private static final String SCHEMA_GUID_KEY = "schema-guid";
	private static final String SQL_TYPE_KEY = "sql-type";
	private static final String EXCLUDE_PREVIOUS_VERSION_KEY = "exclude-previous-version";

	private static Set<String> exportTypes = new HashSet<String>(Arrays.asList(
			SQL_TYPE_IDENTIFIER
			));

	public static boolean isSupported(String key) {
		return exportTypes.contains(key);
	}

	public static String export(Map<String, Object> parameters) throws SQLExportException, H2DataAccessException, SchemaNotFoundException {
		if (!parameters.containsKey(SCHEMA_GUID_KEY) || !parameters.containsKey(EXPORT_TYPE_KEY)) {
			throw new SQLExportException("Request did not contain necessary keys.");
		} else {
			String exportType = parameters.get(EXPORT_TYPE_KEY).toString();
			if (exportType.equals(SQL_TYPE_IDENTIFIER)) {
				if (!parameters.containsKey(SQL_TYPE_KEY)) {
					throw new SQLExportException("No SQL type provided.");
				} else {
					boolean withPreviousVersion = parameters.containsKey(EXCLUDE_PREVIOUS_VERSION_KEY)
							? Boolean.valueOf(parameters.get(EXCLUDE_PREVIOUS_VERSION_KEY).toString()) : true;
							String schemaGuid = parameters.get(SCHEMA_GUID_KEY).toString();
							return SQLExporter.export(parameters.get(SQL_TYPE_KEY).toString(), 
									schemaGuid, withPreviousVersion);
				}
			} else {
				throw new SQLExportException("Invalid export key.");
			}
		}
	}
}
