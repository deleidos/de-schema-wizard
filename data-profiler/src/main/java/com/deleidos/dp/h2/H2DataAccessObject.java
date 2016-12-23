package com.deleidos.dp.h2;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.beans.DataSampleMetaData;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.beans.SchemaMetaData;
import com.deleidos.dp.beans.User;
import com.deleidos.dp.exceptions.H2DataAccessException;
import com.deleidos.dp.h2.H2FunctionRunner.FunctionWithConnection;
import com.deleidos.hd.h2.H2Config;
import com.deleidos.hd.h2.H2Database;

/**
 * Data access object to persist and retrieve schemas, samples, and metrics from
 * the H2 server.
 * 
 * @author leegc
 * @author yoonj1
 *
 */
public class H2DataAccessObject {
	public static final Logger logger = Logger.getLogger(H2DataAccessObject.class);
	private H2FunctionRunner functionRunner = null;
	private H2Config h2Config = null;
	private boolean isLive;
	protected static H2DataAccessObject h2Dao = null;
	private H2MetricsDataAccessObject h2Metrics;
	private H2SampleDataAccessObject h2Samples;
	private H2SchemaDataAccessObject h2Schema;
	private H2ShiroDataAccessObject h2Shiro;
	public static final boolean debug = false;

	private H2DataAccessObject(H2Database h2Database) {
		h2Config = h2Database.getConfig();
		functionRunner = new H2FunctionRunner(h2Database);
		h2Metrics = new H2MetricsDataAccessObject(this);
		h2Samples = new H2SampleDataAccessObject(this);
		h2Schema = new H2SchemaDataAccessObject(this);
		h2Shiro = new H2ShiroDataAccessObject();
	}

	/**
	 * Get or instantiate the static instance of the H2 Data Access Object.
	 * 
	 * @return The static H2DataAccessObject
	 * @throws H2DataAccessException
	 * @throws IOException
	 */
	public static H2DataAccessObject getInstance() throws H2DataAccessException {
		if (h2Dao == null) {
			try {
				h2Dao = new H2DataAccessObject(new H2Database());
			} catch (IOException e) {
				logger.error("Could not find configuration file.");
				logger.error(e);
			}
		}
		return h2Dao;
	}

	public static H2DataAccessObject setInstance(H2Database database) throws H2DataAccessException {
		h2Dao = new H2DataAccessObject(database);
		return h2Dao;
	}

	/**
	 * Remove all files in the database directory with the database name.
	 */
	public void purge() {
		functionRunner.purge();
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
	public static int getGeneratedKey(Statement stmt) throws SQLException {
		ResultSet gKeys = stmt.getGeneratedKeys();
		if (gKeys.next()) {
			int fieldId = gKeys.getInt(1);
			stmt.close();
			return fieldId;
		} else {
			throw new SQLException("Unable to get generated key from statement " + stmt.toString());
		}
	}

	/**
	 * Add a sample
	 * 
	 * @param sample
	 *            the DataSample bean to be added
	 * @throws H2DataAccessException
	 */
	public String addSample(DataSample sample) throws H2DataAccessException {
		return runWithConnection(connection -> h2Samples.addSample(connection, sample), "Error adding sample.");
	}

	/**
	 * Add a schema to H2.
	 * 
	 * @param schemaBean
	 *            the schema object as a bean
	 * @return The guid of the schema
	 * @throws H2DataAccessException
	 */
	public String addSchema(Schema schemaBean) throws H2DataAccessException {
		return runWithConnection(connection -> h2Schema.addSchema(connection, schemaBean), "Error adding schema.");
	}

	/**
	 * Get a list of the schema meta data in H2
	 * 
	 * @return a list of SchemaMetaData beans
	 * @throws H2DataAccessException
	 */
	public List<SchemaMetaData> getAllSchemaMetaData() throws H2DataAccessException {
		return runWithConnection(connection -> h2Schema.getAllSchemaMetaData(connection),
				"Error retrieving all schema meta data.");
	}

	/**
	 * Get a list of the sample meta data in H2
	 * 
	 * @return a list of SampleMetaDataBeans
	 * @throws H2DataAccessException
	 */
	public List<DataSampleMetaData> getAllSampleMetaData() throws H2DataAccessException {
		return runWithConnection(connection -> h2Samples.getAllSampleMetaData(connection),
				"Error retrieving all sample meta data.");
	}

	/**
	 * Get a specific schema meta data object by its GUID
	 * 
	 * @param guid
	 *            the desired guid
	 * @return the SchemaMetaData bean that coincides with the GUID
	 * @throws H2DataAccessException
	 */
	public SchemaMetaData getSchemaMetaDataByGuid(String guid) throws H2DataAccessException {
		return runWithConnection(connection -> h2Schema.getSchemaMetaDataByGuid(connection, guid),
				"SQL error deleting schema metadata with guid " + guid + ".");
	}

	/**
	 * Get a schema by its guid
	 * 
	 * @param guid
	 *            the schema's guid
	 * @param showHistogram
	 *            true if histogram data should be displayed, false if it should
	 *            be removed
	 * @return the Schema bean
	 * @throws H2DataAccessException
	 */
	public Schema getSchemaByGuid(String guid, boolean showHistogram) throws H2DataAccessException {
		return runWithConnection(connection -> h2Schema.getSchemaByGuid(connection, guid, showHistogram),
				"SQL error getting schema with guid " + guid + ". ");
	}

	public List<Schema> getSchemaAndPreviousByGuid(String guid) throws H2DataAccessException {
		return runWithConnection(connection -> h2Schema.getSchemaAndPreviousVersion(connection, guid),
				"Error getting schema and previous for guid " + guid + ".");
	}

	/**
	 * Gets the field-descriptor
	 * 
	 * @param guid
	 *            The Schema's Guid
	 * @param showHistogram
	 *            True if histogram data should be displayed; False if it should
	 *            be removed
	 * @return
	 * @throws H2DataAccessException
	 */
	public Map<String, Profile> getSchemaFieldByGuid(String guid, boolean showHistogram) throws H2DataAccessException {
		return runWithConnection(connection -> h2Schema.getSchemaFieldByGuid(connection, guid, showHistogram),
				"SQL error getting schema fields for guid " + guid + ".");
	}

	/**
	 * Call underlying H2SampleDAO class to retrieve a mapping of all the sample
	 * names to their respective media types in the database.
	 * 
	 * @return
	 * @throws H2DataAccessException
	 */
	public Map<String, String> getExistingSampleNames() throws H2DataAccessException {
		return runWithConnection(h2Samples::getExistingSampleNames, "Error getting existing sample names.");
	}

	/**
	 * Get a list of samples by their guids
	 * 
	 * @param guids
	 *            ordered list of guids
	 * @return an ordered list of DataSample beans
	 * @throws H2DataAccessException
	 * @throws SQLException
	 */
	public List<DataSample> slowerGetSamplesByGuids(String[] guids) throws H2DataAccessException {
		return runWithConnection(connection -> {
			List<DataSample> samples = new ArrayList<DataSample>();
			for (String guid : guids) {
				samples.add(h2Samples.getSampleByGuid(connection, guid));
			}
			return samples;
		} , "SQL error getting multiple samples.");
	}

	/**
	 * Get a list of data samples representing these guids. This list may
	 * contain null values.
	 * 
	 * @param guids
	 * @return
	 */
	public List<DataSample> getSamplesByGuids(String[] guids) {
		return Arrays.asList(guids).stream().parallel()
				.map(guid -> runWithOptionalReturn(conn -> h2Samples.getSampleByGuid(conn, guid),
						"SQL error getting sample " + guid + ".").get())
				.collect(Collectors.toList());
	}

	/**
	 * Gets a given Data Sample bean given its Guid
	 * 
	 * @param guid
	 * @return
	 * @throws H2DataAccessException
	 */
	public DataSample getSampleByGuid(String guid) throws H2DataAccessException {
		return runWithConnection(connection -> h2Samples.getSampleByGuid(connection, guid),
				"SQL error getting sample with guid " + guid + ".");
	}

	/**
	 * Gets a Data Sample Meta Data bean given its Guid
	 * 
	 * @param guid
	 * @return
	 * @throws H2DataAccessException
	 */
	public DataSampleMetaData getSampleMetaDataByGuid(String guid) throws H2DataAccessException {
		return runWithConnection(connection -> h2Samples.getDataSampleMetaDataByGuid(connection, guid),
				"SQL error getting sample metadata for guid " + guid + ".");
	}

	/**
	 * Gets the field-descriptor
	 * 
	 * @param guid
	 * @return
	 * @throws H2DataAccessException
	 */
	public Map<String, Profile> getSampleFieldByGuid(String guid, boolean showHistogram) throws H2DataAccessException {
		return runWithConnection(connection -> h2Samples.getSampleFieldByGuid(connection, guid, showHistogram),
				"SQL error populating profile for sample " + guid + ".");
	}

	public boolean deleteSchemaByGuid(String guid) throws H2DataAccessException {
		return runWithConnection(connection -> h2Schema.deleteSchemaByGuid(connection, guid),
				"SQL error deleting schema with guid " + guid + ".");
	}

	public boolean deleteSampleByGuid(String guid) throws H2DataAccessException {
		return runWithConnection(connection -> h2Samples.deleteSampleByGuid(connection, guid),
				"SQL error deleting sample with guid " + guid + ".");
	}

	/**
	 * Has logic to determine if a GUID is a Schema or Data Sample.
	 * 
	 * @param guid
	 *            An ambiguous GUID belonging to either a Schema or Data Sample
	 * @throws H2DataAccessException
	 */
	public void deleteByGuid(String guid) throws H2DataAccessException {
		runWithConnection(connection -> {
			Schema schema = h2Schema.getSchemaByGuid(connection, guid);
			DataSample sample = h2Samples.getSampleByGuid(connection, guid);

			if (schema != null) {
				h2Schema.deleteSchemaByGuid(connection, guid);
			} else if (sample != null) {
				h2Samples.deleteSampleByGuid(connection, guid);
			} else {
				logger.error("No such guid exists in the database.");
				throw new H2DataAccessException("Error finding guid in H2 database");
			}
			return null;
		} , "Error finding guid in H2 database");
	}

	public static void setH2DAO(H2DataAccessObject h2dao) {
		h2Dao = h2dao;
	}

	public H2MetricsDataAccessObject getH2Metrics() {
		return h2Metrics;
	}

	public H2SampleDataAccessObject getH2Samples() {
		return h2Samples;
	}

	public H2SchemaDataAccessObject getH2Schema() {
		return h2Schema;
	}

	protected boolean testConnection(Connection conn) throws SQLException {
		try {
			isLive = conn.isValid(5);
		} catch (SQLException e) {
			isLive = false;
			logger.error(e);
		}
		return isLive;
	}

	// Used for the health check of H2 from the ServiceLayerAccessor
	public boolean testDefaultConnection() throws H2DataAccessException {
		return runWithConnection(this::testConnection, "Test connection failed.");
	}

	public boolean isLive() {
		return isLive;
	}

	public User getPasswordForUser(String username) throws H2DataAccessException {
		return runWithConnection(conn -> h2Shiro.getPasswordForUser(conn, username),
				"Error retrieving password hash for user " + username + ".");
	}

	public Set<String> getRoleNamesForUser(String username) throws H2DataAccessException {
		return runWithConnection(conn -> h2Shiro.getRoleNamesForUser(conn, username),
				"Error getting roles for user " + username + ".");
	}

	public Set<String> getPermissions(String username, Collection<String> roleNames) throws H2DataAccessException {
		return runWithConnection(conn -> h2Shiro.getPermissions(conn, username, roleNames),
				"Error getting permissions for user " + username + ".");
	}

	/**
	 * Debugging methods.
	 */

	/**
	 * Run a query in H2.
	 * 
	 * @param sql
	 *            The string of the SQL query.
	 * @return The result set from executing this query.
	 * @throws SQLException
	 *             Thrown if there is an error in the query.
	 * @throws H2DataAccessException
	 */
	public ResultSet query(String sql) throws SQLException, H2DataAccessException {
		if (debug) {
			return queryWithOutput(sql);
		} else {
			return runWithConnection(dbConnection -> dbConnection
					.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY).executeQuery(sql),
					"Error executing " + sql + ".");
		}
	}

	/**
	 * Run a query in H2 and log the output at the debug level.
	 * 
	 * @param sql
	 *            The string of the SQL query. It is the callers job to close
	 *            the conneciton.
	 * @return The result set from executing this query.
	 * @throws SQLException
	 *             Thrown if there is an error in the query.
	 * @throws H2DataAccessException
	 */
	public ResultSet queryWithOutput(String sql) throws SQLException, H2DataAccessException {
		return runWithConnection(dbConnection -> {
			ResultSet rs = dbConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
					.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();
			int c = rsmd.getColumnCount();
			StringBuilder sb = new StringBuilder();
			logger.info(sql);
			for (int i = 1; i <= c; i++) {
				sb.append(rsmd.getColumnName(i) + "\t");
			}
			logger.info(sb);
			while (rs.next()) {
				StringBuilder s = new StringBuilder();
				for (int i = 1; i <= c; i++) {
					s.append(rs.getString(i) + "\t");
				}
				logger.info(s.toString());
			}
			rs.beforeFirst();
			return rs;
		} , "Error executing " + sql);
	}

	public H2Config getH2Config() {
		return h2Config;
	}

	public void setH2Config(H2Config h2Config) {
		this.h2Config = h2Config;
	}

	public String createUser(User user) throws H2DataAccessException, SQLException {
		return runWithConnection(conn -> h2Shiro.createUser(conn, user), "Error creating user " + user + ".");
	}

	public List<User> getAllUsers() throws H2DataAccessException, SQLException {
		return runWithConnection(h2Shiro::getAllUsers, "Error getting all users.");
	}

	public boolean updateUser(User user) throws H2DataAccessException, SQLException {
		return runWithConnection(conn -> h2Shiro.updateUser(conn, user), "Error updating user " + user + ".");
	}

	public boolean deleteUser(String username) throws H2DataAccessException, SQLException {
		return runWithConnection(conn -> h2Shiro.deleteUser(conn, username), "Error deleting user " + username + ".");
	}

	public boolean initializeDefaultUser(User user) throws H2DataAccessException, SQLException {
		return runWithConnection(conn -> h2Shiro.initializeDefaultUser(conn, user),
				"Error intializing default user " + user + ".");
	}

	public boolean initializeDefaultSecurityQuestions() throws H2DataAccessException, SQLException {
		return runWithConnection(h2Shiro::initializeDefaultSecurityQuestions,
				"Error initializing default security questions.");
	}

	public User getUser(String username) throws H2DataAccessException, SQLException {
		return runWithConnection(conn -> h2Shiro.getUser(conn, username), "Error retrieving user " + username + ".");
	}

	public User getUsernameFromFirstName(String firstName) throws H2DataAccessException, SQLException {
		return runWithConnection(conn -> h2Shiro.getUsernameFromFirstName(conn, firstName),
				"Error getting username from first name " + firstName + ".");
	}

	public boolean addSecurityQuestionsForUser(User user) throws H2DataAccessException, SQLException {
		return runWithConnection(conn -> h2Shiro.addSecurityQuestionsForUser(conn, user),
				"Error adding security questions for user " + user + ".");
	}

	public User getSecurityQuestionsForUser(String username) throws H2DataAccessException, SQLException {
		return runWithConnection(conn -> h2Shiro.getSecurityQuestionsForUser(conn, username),
				"Error getting security questions for user " + username + ".");
	}

	public boolean verifySecurityQuestionsForUser(User user) throws H2DataAccessException, SQLException {
		return runWithConnection(conn -> h2Shiro.verifySecurityAnswersForUser(conn, user),
				"Error verifying security questions for user " + user + ".");
	}

	public List<String> getAllSecurityQuestions() throws H2DataAccessException, SQLException {
		return runWithConnection(h2Shiro::getAllSecurityQuestionsFromBank, "Error getting all security questions.");
	}

	/**
	 * Run a FunctionWithConnection with a connection to the database. The
	 * caller should not close the connection. An example implementation using a
	 * lambda expresion:
	 * 
	 * <pre>
	 * runWithConnection(connection -> connection.getNetworkTimeout());
	 * </pre>
	 * 
	 * Each H2DataAccessObject method that requires a connection should open a
	 * connection using this method call. Any subsequent calls to other data
	 * accessors should be passed the same connection instance.
	 * 
	 * 
	 * @param function
	 *            a FunctionWithConnection instance (designed to be used with a
	 *            lambda expression)
	 * @param errorMessage
	 *            The error message that should be logged and thrown as part of
	 *            any H2DataAccessException
	 * @return The result of the FunctionWithConnection
	 * @throws H2DataAccessException
	 *             thrown if there is any Exception in the method, if
	 *             errorMessage is set, it will use it as a message
	 */
	private <T> T runWithConnection(FunctionWithConnection<T> function, String errorMessage)
			throws H2DataAccessException {
		return functionRunner.runConnectionFunction(function, errorMessage);
	}

	/**
	 * Run the FunctionWithConnection that does not throw any checked
	 * exceptions. This can be used to construct efficient streaming flows with
	 * parallelized connections.
	 * 
	 * @param function
	 * @param errorMessage
	 * @return
	 */
	private <T> Optional<T> runWithOptionalReturn(FunctionWithConnection<T> function, String errorMessage) {
		try {
			return Optional.of(runWithConnection(function, errorMessage));
		} catch (Exception e) {
			logger.error(e);
			return Optional.empty();
		}
	}

	private <T> Optional<T> runWithOptionalReturn(FunctionWithConnection<T> function, String errorMessage,
			T errorValue) {
		try {
			return Optional.of(runWithConnection(function, errorMessage));
		} catch (Exception e) {
			logger.error(e);
			return Optional.ofNullable(errorValue);
		}
	}

}
