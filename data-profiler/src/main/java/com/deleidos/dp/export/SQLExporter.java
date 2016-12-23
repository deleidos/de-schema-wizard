package com.deleidos.dp.export;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.enums.DetailType;
import com.deleidos.dp.exceptions.H2DataAccessException;
import com.deleidos.dp.exceptions.MainTypeRuntimeException;
import com.deleidos.dp.exceptions.SchemaNotFoundException;
import com.deleidos.dp.h2.H2DataAccessObject;

/**
 * References that were used to create this class:<br>
 * <li><a href="http://www.w3schools.com/sql/sql_alter.asp">W3 Schools Reference</a></li>
 * <li><a href="https://www.postgresql.org/docs/9.1/static/sql-altertable.html">PostgreSQL</a></li>
 * <li><a href="http://www.h2database.com/html/grammar.html#alter_table_alter_column">H2</a></li>
 * @author leegc
 *
 */
public class SQLExporter extends AbstractExporter {
	public static final String MY_SQL_KEY = "mysql";
	public static final String SQL_SERVER_KEY = "sqlserver";
	public static final String ORACLE_10G_KEY = "oracle10g";
	public static final String H2_KEY = "h2";
	public static final String POSTGRES_KEY = "postgres";
	public static final String WINDOWS_NEWLINE = "\r\n";
	public static final String UNIX_NEWLINE = "\n";
	private final SQL_IMPLEMENTATION sqlLanguage;
	private final String alterColumnStatement;
	private final String newLineCharacter;
	private final String fieldWrapperIdentifier;

	public enum SQL_IMPLEMENTATION {
		SQL_SERVER, MY_SQL, ORACLE_10G, POSTGRESQL
	}

	private enum ACTION { 
		ADD, DROP, MODIFY, NONE
	}

	private SQLExporter(SQL_IMPLEMENTATION sqlLanguage, boolean useUnixNewline) {
		this.sqlLanguage = sqlLanguage;
		this.fieldWrapperIdentifier = !sqlLanguage.equals(SQL_IMPLEMENTATION.MY_SQL) 
				? "\""
				: "`";
		switch (sqlLanguage) {
		case MY_SQL: alterColumnStatement = "MODIFY COLUMN"; break;
		case ORACLE_10G: alterColumnStatement = "MODIFY"; break;
		case SQL_SERVER: alterColumnStatement = "ALTER COLUMN"; break;
		case POSTGRESQL: alterColumnStatement = "ALTER COLUMN"; break;
		default: throw new RuntimeException("Unexpected SQL enumeration " + sqlLanguage.toString() + ".");
		}
		this.newLineCharacter = useUnixNewline ? UNIX_NEWLINE : WINDOWS_NEWLINE;
	}

	private SQLExporter(SQL_IMPLEMENTATION sqlLanguage) {
		this(sqlLanguage, false);
	}

	private static final Map<String, SQL_IMPLEMENTATION> sqlImplementationNameMapping;
	static {
		sqlImplementationNameMapping = new HashMap<String, SQL_IMPLEMENTATION>();
		sqlImplementationNameMapping.put(SQL_SERVER_KEY, SQL_IMPLEMENTATION.SQL_SERVER);
		sqlImplementationNameMapping.put(MY_SQL_KEY, SQL_IMPLEMENTATION.MY_SQL);
		sqlImplementationNameMapping.put(ORACLE_10G_KEY, SQL_IMPLEMENTATION.ORACLE_10G);
		sqlImplementationNameMapping.put(H2_KEY, SQL_IMPLEMENTATION.SQL_SERVER);
		sqlImplementationNameMapping.put(POSTGRES_KEY, SQL_IMPLEMENTATION.POSTGRESQL);
	}

	public static String export(String sqlLanguage, String schemaGuid, boolean withPreviousVersion) 
			throws SQLExportException, SchemaNotFoundException, H2DataAccessException {
		if (!sqlImplementationNameMapping.containsKey(sqlLanguage)) {
			throw new SQLExportException("SQL Language " + sqlLanguage + " not supported.");
		} else {
			SQL_IMPLEMENTATION sqlImpl = sqlImplementationNameMapping.get(sqlLanguage);
			if (withPreviousVersion) {
				List<Schema> schemas = H2DataAccessObject.getInstance().getSchemaAndPreviousByGuid(schemaGuid);
				return schemas.get(1) == null ? export(sqlImpl, schemas.get(0)) : 
					export(sqlImpl, schemas.get(0), schemas.get(1));
			} else {
				Schema schema = H2DataAccessObject.getInstance().getSchemaByGuid(schemaGuid, true);
				return export(sqlImpl, schema);
			}
		}
	}

	public static String export(SQL_IMPLEMENTATION sqlLanguage, Schema schema, Schema previousVersion) {
		return new SQLExporter(sqlLanguage).generateExport(schema, previousVersion);
	}

	public static String export(SQL_IMPLEMENTATION sqlLanguage, Schema schema) {
		return new SQLExporter(sqlLanguage).generateExport(schema);
	}

	@Override
	public String generateExport(Schema schema) {
		return generateSqlCreateStatement(sqlLanguage, 
				schema.getsName(), schema.getsProfile());
	}

	@Override
	public String generateExport(Schema schema, Schema previousVersion) {
		return generateSqlCreateStatement(
				sqlLanguage, previousVersion.getsName(), previousVersion.getsProfile()) 
				+ "\r\n\r\n" +
				generateSqlAlterStatement(
						sqlLanguage, schema.getsName(), schema.getsProfile(),
						previousVersion.getsName(), previousVersion.getsProfile()
						); 
	}

	private String generateSqlCreateStatement(
			SQL_IMPLEMENTATION sqlLanguage, String schemaName, Map<String, Profile> profiles) {
		return "/* Create statements */" + newLineCharacter
				+	"CREATE TABLE "+wrap(schemaName) + newLineCharacter +"("+newLineCharacter+"\t" + 
				String.join(","+newLineCharacter+"\t", profiles.entrySet()
						.stream()
						.map(this::generateCreateTableLine)
						.toArray(size->new String[size])) + 
				newLineCharacter + ");";
	}

	private String generateSqlAlterStatement(
			SQL_IMPLEMENTATION sqlLanguage, 
			String schemaName, Map<String, Profile> profiles, 
			String previousSchemaName, Map<String, Profile> previousProfiles) {
		Set<String> allKeys = new HashSet<String>(profiles.keySet());
		allKeys.addAll(previousProfiles.keySet());
		return "/* Alter table statements */" + newLineCharacter +
				String.join(newLineCharacter, allKeys
						.stream()
						.map(key->generateAlterTableLine(schemaName, key, profiles.get(key), previousProfiles))
						.toArray(size->new String[size]));
	}

	private final String determineTypeString(Profile profile, Profile previousProfile) {
		switch (profile.getMainTypeClass()) {
		case NUMBER: {
			if (profile.getDetail().getDetailTypeClass().equals(DetailType.INTEGER)) {
				return "INT";
			} else {
				return "FLOAT";
			}
		}
		case STRING: {
			Integer maxNum = 1024;
			if (profile.getPresence() > 0) {
				Integer previousProfileMax = 0;
				if (previousProfile != null) {
					previousProfileMax = 
							Double.valueOf(Profile.getStringDetail(profile).getMaxLength()
									+ Profile.getStringDetail(profile).getStdDevLength()).intValue();
				}
				maxNum = Math.max(Double.valueOf(Profile.getStringDetail(profile).getMaxLength()
						+ Profile.getStringDetail(profile).getStdDevLength()).intValue(), previousProfileMax);
			} 
			return "VARCHAR("+maxNum+")";
		}
		case BINARY: return "BLOB";
		default: throw new MainTypeRuntimeException();
		}
	}

	private String generateCreateTableLine(Entry<String, Profile> entry) {
		return generateCreateTableLine(entry.getKey(), entry.getValue());
	}

	private String generateCreateTableLine(String columnName, Profile profile) {
		return ""+wrap(columnName)+" " + determineTypeString(profile, null);
	}

	private final String generateAlterTableLine(String tableName, String columnName, Profile profile,
			Map<String, Profile> oldProfiles) {
		if (profile == null) {
			return "ALTER TABLE "+wrap(tableName)+" DROP COLUMN "+wrap(columnName)+";";
		} else if (oldProfiles.containsKey(columnName)) {
			String previousTypeString = determineTypeString(oldProfiles.get(columnName), null);
			String typeString = determineTypeString(profile, oldProfiles.get(columnName));
			if (typeString.equals(previousTypeString)) {
				return "/* Field "+wrap(columnName)+" was not altered. */";
			} else {
				if (sqlLanguage.equals(SQL_IMPLEMENTATION.POSTGRESQL)) {
					return "ALTER TABLE " + wrap(tableName) + " " + alterColumnStatement + " "
							+ wrap(columnName) + " TYPE " + typeString + ";";
				} else {
					return "ALTER TABLE " + wrap(tableName) + " " + alterColumnStatement + " "
							+ wrap(columnName) + " " + typeString + ";";
				}
			}
		} else {
			return "ALTER TABLE " + wrap(tableName)+" ADD " + wrap(columnName) + " " 
					+ determineTypeString(profile, null) + ";";
		}
	}
	
	private String wrap(String unwrapped) {
		return fieldWrapperIdentifier + unwrapped + fieldWrapperIdentifier;
	}

	public static class SQLExportException extends Exception {
		public SQLExportException(String message) {
			super(message);
		}
	}
}
