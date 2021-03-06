package com.deleidos.hd.h2;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.RunScript;
import org.h2.tools.Server;

import com.deleidos.hd.enums.DetailType;
import com.deleidos.hd.enums.MainType;

/**
 * Data access object to persist and retrieve schemas, samples, and metrics from
 * the H2 server.
 * 
 * @author leegc
 * @author yoonj1
 *
 */
public class H2Database {
	public static Logger logger = Logger.getLogger(H2Database.class);
	public static final String DB_DRIVER = "org.h2.Driver";
	private final JdbcConnectionPool connectionPool;
	public int emptyHistogramId = 1;
	public int unknownInterpretationId = 1;
	private static Map<String, String> failedAnalysisMapping;
	public static final String UNDETERMINED_ERROR_GUID = "failed-analysis-general-001";
	public static final String UNDETERMINED_ERROR_MESSAGE = "There was an error while processing the sample.";
	public static final String UNDETECTABLE_SAMPLE_GUID = "failed-analysis-undetectable-002";
	public static final String UNDETECTABLE_SAMPLE_MESSAGE = "The sample file type could not be determined.";
	public static final String UNSUPPORTED_PARSER_GUID = "failed-analysis-unsupported-003";
	public static final String UNSUPPORTED_PARSER_MESSAGE = "The sample file type is not supported.";
	public static final String DATA_ERROR_GUID = "failed-analysis-no-data-004";
	public static final String DATA_ERROR_MESSAGE = "The sample analysis could not be completed due to a database error.";
	public static final String IO_ERROR_GUID = "failed-analysis-io-err-005";
	public static final String IO_ERROR_MESSAGE = "The sample analysis could not be completed due to a file error.";
	private static volatile boolean shutdownFlag = false;
	public static boolean debug = false;
	private final H2Config config;

	public H2Database() throws IOException {
		this(new H2Config().load(), false);
	}

	static {
		failedAnalysisMapping = initFailedAnalysisMapping();
	}

	public static Map<String, String> getFailedAnalysisMapping() {
		return failedAnalysisMapping;
	}

	protected H2Database(H2Config config, boolean purge) {
		if(purge) {
			DeleteDbFiles.execute(config.getDir(), config.getName(), false);
		}
		this.connectionPool = JdbcConnectionPool.create(config.getConnectionString(), config.getUser(), config.getPasswd());
		if(config.equals(H2Config.TEST_CONFIG)) {
			this.connectionPool.setMaxConnections(1);
			logger.info("Initialized connection pooling with 1 test connection.");
		} else {
			this.connectionPool.setMaxConnections(20);
			logger.info("Initialized connection pooling with 20 connections.");
		}
		this.config = config;
	}

	private static Map<String, String> initFailedAnalysisMapping() {
		Map<String, String> failedAnalysisMap = new HashMap<String, String>();
		failedAnalysisMap.put(UNDETECTABLE_SAMPLE_GUID, UNDETECTABLE_SAMPLE_MESSAGE);
		failedAnalysisMap.put(UNDETERMINED_ERROR_GUID, UNDETERMINED_ERROR_MESSAGE);
		failedAnalysisMap.put(DATA_ERROR_GUID, DATA_ERROR_MESSAGE);
		failedAnalysisMap.put(UNSUPPORTED_PARSER_GUID, UNSUPPORTED_PARSER_MESSAGE);
		failedAnalysisMap.put(IO_ERROR_GUID, IO_ERROR_MESSAGE);
		return failedAnalysisMap;
	}

	/**
	 * Start the server with defaults.
	 * 
	 * @param args
	 *            Command line arguments for H2.
	 * @throws IOException
	 * @throws InterruptedException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) {
		try {
			H2Config config = new H2Config().load();
			H2Database h2 = new H2Database(config, false);
			try {
				final Server server = h2.startServer(config);
				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						logger.info("Server shutting down.");
						h2.connectionPool.dispose();
						server.stop();
					}
				});
				try {
					Connection dbConnection = h2.getNewConnection();
					if(!dbConnection.isValid(5)) {
						dbConnection.close();
						throw new SQLException("Connection could not be made with H2.");
					} else {
						logger.info("H2 connection established.");
					}
					h2.runSchemaWizardStartupScript(dbConnection);
					dbConnection.close();
					h2.join(server);
				} catch(SQLException e) {
					logger.error(e);
					System.err.println("Could not populate database with necessary tables.");
				}
			} catch (InterruptedException e) {
				logger.error(e);
				System.err.println("Unexpected threading error while starting server.");
			} catch (SQLException e) {
				logger.error(e);
				System.err.println("Connection could not be made to server.");
			}
		} catch (IOException e) {
			logger.error(e);
			System.err.println("Could not find configuration file.");
		}
	}

	/**
	 * Override method to run the server in its own thread. Able to implement
	 * maintenance here.
	 */
	public void join(Server server) {
		long t1 = System.currentTimeMillis();
		logger.info("Server running at " + server.getURL());
		while (true) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - t1) > 360000 && t1 != currentTime) {
				logger.info("Server running at " + server.getURL());
				t1 = currentTime;
			}
			if (shutdownFlag) {
				logger.info("Server thread signaled to stop.");
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.error(e);
				e.printStackTrace();
			}
		}
		logger.info("Server loop ending.");
	}

	/**
	 * Start up the server with command line arguments. Unless the init(String
	 * initFile) method is called, the server will be started with properties
	 * from src/main/resources/h2-init.properties
	 * 
	 * @param args
	 *            Arguments to start the server. These should match the
	 *            properties file. TODO if empty, use properties file
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * @throws InterruptedException 
	 */
	public Server startServer(H2Config config) throws SQLException, InterruptedException {
		logger.info("Starting up H2 server.");
		String[] args = new String[3];
		args[0] = "-tcpAllowOthers";
		args[1] = "-tcpPort";
		args[2] = config.getPortNum().toString();
		Server server = Server.createTcpServer(args);
		logger.info("Server started at " + server.getURL());
		server.setOut(System.out);
		server.start();
		return server;
	}

	/**
	 * Generate the schema wizard's schema in the H2 database.
	 * 
	 * @throws SQLException
	 *             If there is an exception executing the startup script.
	 */
	public void runSchemaWizardStartupScript(Connection dbConnection) throws SQLException {
		try {
			InputStreamReader isr = new InputStreamReader(
					getClass().getResourceAsStream("/scripts/init_field_characterization.sql"));
			RunScript.execute(dbConnection, isr);
			logger.info("Initialization script executed.");
			isr.close();

			String countMainType = "SELECT * FROM main_type";
			PreparedStatement ppstCheckMain = dbConnection.prepareStatement(countMainType);
			ResultSet rsMainType = ppstCheckMain.executeQuery();
			if (!rsMainType.next()) {
				for (MainType type : MainType.values()) {
					int id = type.getIndex();
					String name = type.name();
					String insertIntoMainType = "INSERT INTO main_type VALUES (? , ?)";
					PreparedStatement ppst = dbConnection.prepareStatement(insertIntoMainType);
					ppst.setInt(1, id);
					ppst.setString(2, name);
					ppst.execute();

				} // 3 num,4 string,3 bin
				logger.info("Data types inserted.");
			} else {
				logger.info("Data types exist.");
			}

			String countDetailType = "SELECT * FROM detail_type";
			PreparedStatement ppstCheckDetail = dbConnection.prepareStatement(countDetailType);
			ResultSet rsDetailType = ppstCheckDetail.executeQuery();
			if (!rsDetailType.next()) {
				for (DetailType type : DetailType.values()) {
					int id = type.getIndex();
					String name = type.name();
					String insertIntoDetailType = "INSERT INTO detail_type VALUES (?, ?)";
					PreparedStatement ppst = dbConnection.prepareStatement(insertIntoDetailType);
					ppst.setInt(1, id);
					ppst.setString(2, name);
					ppst.execute();

					String insertIntoTypeMapping = "INSERT INTO type_mapping VALUES (NULL, ?, ?)";
					PreparedStatement ppst2 = dbConnection.prepareStatement(insertIntoTypeMapping);
					ppst2.setLong(1, type.getMainType().getIndex());
					ppst2.setInt(2, id);
					ppst2.execute();
				}
				logger.info("Detail types inserted.");
			} else {
				logger.info("Detail types exist.");
			}

			String countHistogram = "SELECT * FROM histogram";
			PreparedStatement ppstCheckHistogram = dbConnection.prepareStatement(countHistogram);
			ResultSet rsHistogram = ppstCheckHistogram.executeQuery();
			if (!rsHistogram.next()) {
				String insertIntoHistogram = "INSERT INTO histogram VALUES (NULL, NULL, NULL, NULL)";
				PreparedStatement ppstHistogram = dbConnection.prepareStatement(insertIntoHistogram);
				ppstHistogram.execute();
				setEmptyHistogramId(getGeneratedKey(ppstHistogram));
				logger.info("Empty histogram inserted.");
			} else {
				logger.info("Histograms exists.");
			}

			String countInterpretation = "SELECT * FROM interpretation";
			PreparedStatement ppstCheckInterpretation = dbConnection.prepareStatement(countInterpretation);
			ResultSet rsInterpretation = ppstCheckInterpretation.executeQuery();
			if (!rsInterpretation.next()) {
				String insertIntoInterpretation = "INSERT INTO interpretation (i_name) VALUES (?) ";
				PreparedStatement ppstInterpretation = dbConnection.prepareStatement(insertIntoInterpretation,
						PreparedStatement.RETURN_GENERATED_KEYS);
				ppstInterpretation.setString(1, "unknown");
				ppstInterpretation.execute();
				setUnknownInterpretationId(getGeneratedKey(ppstInterpretation));
				logger.info("Empty interpretation inserted.");
			} else {
				logger.info("Interpretations exists.");
			}

			logger.info("Database initialized.");
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}

	/**
	 * Remove all files in the database directory with the database name. The
	 * database must be closed before calling this method.
	 */
	public void purge() {
		if(connectionPool.getActiveConnections() > 0) {
			logger.warn(connectionPool.getActiveConnections() + " open connections when attempting to shut down database.");
		}
		this.connectionPool.dispose();
		logger.info("Deleting database files at " + config.getDir() + ".");
		DeleteDbFiles.execute(config.getDir(), config.getName(), false);
	}

	/**
	 * Initialize the database connection with the given connection string.
	 * 
	 * @param connectionString
	 *            The string to connect with.
	 * @return The connection.
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 
	protected Connection initSingleConnection(H2Config config) throws ClassNotFoundException, SQLException {
		String connString = config.getConnectionString();
		logger.info("Connecting with " + connString);
		try {
			Class.forName(config.getDriver());
		} catch (Exception e) {
			logger.error(e);
			Class.forName(DB_DRIVER);
		}
		Connection dbConnection = DriverManager.getConnection(connString, config.getUser(), config.getPasswd());
		return dbConnection;
	}*/

	public boolean isShutdownFlag() {
		return shutdownFlag;
	}

	public void setShutdownFlag() {
		H2Database.shutdownFlag = true;
	}

	// Getters and Setters
	public int getEmptyHistogramId() {
		return emptyHistogramId;
	}

	public void setEmptyHistogramId(int emptyHistogramId) {
		this.emptyHistogramId = emptyHistogramId;
	}

	public int getUnknownInterpretationId() {
		return unknownInterpretationId;
	}

	public void setUnknownInterpretationId(int unknownInterpretationId) {
		this.unknownInterpretationId = unknownInterpretationId;
	}



	/**
	 * Return the generated key from a statement (H2 only allows a maximum of
	 * one to be returned per query). Calling this method will not execute the
	 * statement.
	 * 
	 * @param stmt
	 *            The executed statement
	 * @return The key generated by executing this statement
	 * @throws SQLException
	 *             Thrown if there is an error in the query.
	 */
	protected int getGeneratedKey(Statement stmt) throws SQLException {
		ResultSet gKeys = stmt.getGeneratedKeys();
		int fieldId = -1;
		if (gKeys.next()) {
			fieldId = gKeys.getInt(1);
			stmt.close();
		} else {
			stmt.close();
			return -1;
		}
		return fieldId;
	}

	public Connection getNewConnection() throws SQLException {
		Connection connection = connectionPool.getConnection();
		logger.debug(connectionPool.getActiveConnections() + " connections open in H2Database.");
		return connection;
	}

	public H2Config getConfig() {
		return config;
	}


}