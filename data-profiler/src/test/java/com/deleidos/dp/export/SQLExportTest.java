package com.deleidos.dp.export;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.environ.DPMockUpEnvironmentTest;
import com.deleidos.dp.environ.TestUtils;
import com.deleidos.dp.environ.TestUtils.RecordGeneratorFunction;
import com.deleidos.dp.environ.TestUtils.SampleIngestionUtility;
import com.deleidos.dp.exceptions.H2DataAccessException;
import com.deleidos.dp.export.SQLExporter.SQL_IMPLEMENTATION;
import com.deleidos.dp.h2.H2DataAccessObject;
import com.deleidos.dp.profiler.DefaultProfilerRecord;
import com.deleidos.dp.profiler.api.ProfilerRecord;

public class SQLExportTest extends DPMockUpEnvironmentTest {
	private static final Logger logger = Logger.getLogger(SQLExportTest.class);
	static Schema schema;
	static Schema secondSchema;
	final String expectedMySQLCreateExport = 
			"CREATE TABLE `schema_test`\r\n" + 
			"(\r\n" + 
			"	`str` VARCHAR(6),\r\n" + 
			"	`num` INT,\r\n" + 
			"	`long-str` VARCHAR(38)\r\n" + 
			");";
	final String expectedSQLServerCreateExport = 
			"CREATE TABLE \"schema_test\"\r\n" + 
			"(\r\n" + 
			"	\"str\" VARCHAR(6),\r\n" + 
			"	\"num\" INT,\r\n" + 
			"	\"long-str\" VARCHAR(38)\r\n" + 
			");";
	final String expectedOracle10GCreateExport = expectedSQLServerCreateExport;
	final String expectedMySQLAlterExport =
			"ALTER TABLE `schema_test` DROP COLUMN `str`;\r\n" + 
			"/* Field `num` was not altered. */\r\n" + 
			"ALTER TABLE `schema_test` MODIFY COLUMN `long-str` VARCHAR(74);\r\n" + 
			"ALTER TABLE `schema_test` ADD `added-num` INT;";
	final String expectedSQLServerAlterExport =
			"ALTER TABLE \"schema_test\" DROP COLUMN \"str\";\r\n" + 
			"/* Field \"num\" was not altered. */\r\n" + 
			"ALTER TABLE \"schema_test\" ALTER COLUMN \"long-str\" VARCHAR(74);\r\n" + 
			"ALTER TABLE \"schema_test\" ADD \"added-num\" INT;";
	final String expectedOracle10GAlterExport =
			"ALTER TABLE \"schema_test\" DROP COLUMN \"str\";\r\n" + 
			"/* Field \"num\" was not altered. */\r\n" + 
			"ALTER TABLE \"schema_test\" MODIFY \"long-str\" VARCHAR(74);\r\n" + 
			"ALTER TABLE \"schema_test\" ADD \"added-num\" INT;";
	final String expectedPostGreSQLAlterExport = 
			"ALTER TABLE \"schema_test\" DROP COLUMN \"str\";\r\n" + 
			"/* Field \"num\" was not altered. */\r\n" + 
			"ALTER TABLE \"schema_test\" ALTER COLUMN \"long-str\" TYPE VARCHAR(74);\r\n" + 
			"ALTER TABLE \"schema_test\" ADD \"added-num\" INT;";

	@BeforeClass
	public static void generateSchema() throws H2DataAccessException {
		SampleIngestionUtility ingestUtility = new SampleIngestionUtility();
		ingestUtility.addSampleIngestion(100, new BasicSQLRecordGenerator());
		schema = TestUtils.processWorkflowWithDefaultBehavior("schema_test", ingestUtility, true);
		SampleIngestionUtility ingestUtility2 = new SampleIngestionUtility();
		ingestUtility2.addSampleIngestion(100, new BasicSQLRecordGenerator2());
		secondSchema = TestUtils.processWorkflowWithAdvancedBehavior("schema_test", ingestUtility2, 
				true, schema.getsGuid(), null, x->x.getKey().equals("str"));
	}
	
	//@Test
	public void outputPostGreSQL() {
		System.out.println(SQLExporter.export(SQL_IMPLEMENTATION.POSTGRESQL, secondSchema, schema));
	}

	@Test
	public void testMySQLExport() {
		SQL_IMPLEMENTATION sqlLanguage = SQL_IMPLEMENTATION.MY_SQL;
		String expected = expectedMySQLCreateExport;
		String export = SQLExporter.export(sqlLanguage, schema);
		try {
			assertTrue(export.contains(expected));
			logger.info(export);
		} catch (AssertionError e) {
			logger.error("Export for "+sqlLanguage+" did not match expectation.");
			logger.error("Expected:\n"+expected);
			logger.error("Actual:\n"+export);
			fail();
		}
	}

	@Test
	public void testOracle10GExport() {
		SQL_IMPLEMENTATION sqlLanguage = SQL_IMPLEMENTATION.ORACLE_10G;
		String expected = expectedOracle10GCreateExport;
		String export = SQLExporter.export(sqlLanguage, schema);
		try {
			assertTrue(export.contains(expected));
		} catch (AssertionError e) {
			logger.error("Export for "+sqlLanguage+" did not match expectation.");
			logger.error("Expected:\n"+expected);
			logger.error("Actual:\n"+export);
			fail();
		}
	}

	@Test
	public void testSQLServerExport() {
		SQL_IMPLEMENTATION sqlLanguage = SQL_IMPLEMENTATION.SQL_SERVER;
		String expected = expectedSQLServerCreateExport;
		String export = SQLExporter.export(sqlLanguage, schema);
		try {
			assertTrue(export.contains(expected));
		} catch (AssertionError e) {
			logger.error("Export for "+sqlLanguage+" did not match expectation.");
			logger.error("Expected:\n"+expected);
			logger.error("Actual:\n"+export);
			fail();
		}
	}
	
	@Test
	public void testOracle10GAlterExport() {
		SQL_IMPLEMENTATION sqlLanguage = SQL_IMPLEMENTATION.ORACLE_10G;
		String expected = expectedOracle10GAlterExport;
		String export = SQLExporter.export(sqlLanguage, secondSchema, schema);
		try {
			assertTrue(export.contains(expected));
		} catch (AssertionError e) {
			logger.error("Export for "+sqlLanguage+" did not match expectation.");
			logger.error("Expected:\n"+expected);
			logger.error("Actual:\n"+export);
			fail();
		}
	}
	
	@Test
	public void testMySQLAlterExport() {
		SQL_IMPLEMENTATION sqlLanguage = SQL_IMPLEMENTATION.MY_SQL;
		String expected = expectedMySQLAlterExport;
		String export = SQLExporter.export(sqlLanguage, secondSchema, schema);
		try {
			assertTrue(export.contains(expected));
		} catch (AssertionError e) {
			logger.error("Export for "+sqlLanguage+" did not match expectation.");
			logger.error("Expected:\n"+expected);
			logger.error("Actual:\n"+export);
			fail();
		}
	}
	
	@Test
	public void testSQLServerAlterExport() {
		SQL_IMPLEMENTATION sqlLanguage = SQL_IMPLEMENTATION.SQL_SERVER;
		String expected = expectedSQLServerAlterExport;
		String export = SQLExporter.export(sqlLanguage, secondSchema, schema);
		try {
			assertTrue(export.contains(expected));
		} catch (AssertionError e) {
			logger.error("Export for "+sqlLanguage+" did not match expectation.");
			logger.error("Expected:\n"+expected);
			logger.error("Actual:\n"+export);
			fail();
		}
	}
	
	@Test
	public void testPostGreSQLAlterExport() {
		SQL_IMPLEMENTATION sqlLanguage = SQL_IMPLEMENTATION.POSTGRESQL;
		String expected = expectedPostGreSQLAlterExport;
		String export = SQLExporter.export(sqlLanguage, secondSchema, schema);
		try {
			System.out.println(export);
			assertTrue(export.contains(expected));
		} catch (AssertionError e) {
			logger.error("Export for "+sqlLanguage+" did not match expectation.");
			logger.error("Expected:\n"+expected);
			logger.error("Actual:\n"+export);
			fail();
		}
	}

	static class BasicSQLRecordGenerator implements RecordGeneratorFunction {

		@Override
		public ProfilerRecord apply(int value) {
			DefaultProfilerRecord profilerRecord = new DefaultProfilerRecord();
			profilerRecord.put("num", value);
			profilerRecord.put("str", "str-"+String.valueOf(value));
			profilerRecord.put("long-str", 
					"placeholder-placeholder-placeholder-"+String.valueOf(value));
			return profilerRecord;
		}

	}
	
	static class BasicSQLRecordGenerator2 implements RecordGeneratorFunction {

		@Override
		public ProfilerRecord apply(int value) {
			DefaultProfilerRecord profilerRecord = new DefaultProfilerRecord();
			profilerRecord.put("num", value);
			// profilerRecord.put("str", "str-"+String.valueOf(value));
			// removed "str"
			profilerRecord.put("long-str", 
					"placeholder-placeholder-placeholder-placeholder-placeholder-"+String.valueOf(value));
			profilerRecord.put("added-num", value);
			return profilerRecord;
		}

	}
}
