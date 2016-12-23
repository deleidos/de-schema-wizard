package com.deleidos.dp.export;

import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.exceptions.H2DataAccessException;
import com.deleidos.dp.exceptions.SchemaNotFoundException;

public interface Exporter {

	public abstract String generateExport(Schema schema);
	
	public abstract String generateExport(Schema schema, Schema previousVersion);
	
}
