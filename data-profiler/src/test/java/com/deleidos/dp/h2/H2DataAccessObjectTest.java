package com.deleidos.dp.h2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.beans.DataSampleMetaData;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.beans.SchemaMetaData;
import com.deleidos.dp.beans.User;
import com.deleidos.dp.deserializors.SerializationUtility;
import com.deleidos.dp.environ.DPMockUpEnvironmentTest;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.exceptions.H2DataAccessException;

/**
 * Tests the various functionalities of the H2 Data Access Object. These same
 * functions are used by the Service Layer.
 * 
 * @author yoonj1
 *
 */
public class H2DataAccessObjectTest extends DPMockUpEnvironmentTest {
	private static Logger logger = Logger.getLogger(H2DataAccessObjectTest.class);

	@Test
	public void getSchemaByGuidTest() throws DataAccessException {
		Schema schema;
		JSONObject json;

		String schemaGuid = "sad89fuy98a5f-12a3-1231-124sdf31d21f";
		schema = H2DataAccessObject.getInstance().getSchemaByGuid(schemaGuid, false);
		String jsonString = SerializationUtility.serialize(schema);
		json = new JSONObject(jsonString);

		logger.debug(json.toString());
		logger.info("getSchemaByGuid() passed.");
		assertEquals(schemaGuid, json.get("sId"));
		assertTrue(!schema.getsProfile().isEmpty());
	}

	@Test
	public void getSchemaMetaDataByGuidTest() throws DataAccessException {
		SchemaMetaData schemaMetaData;
		JSONObject json;

		String schemaGuid = "sad89fuy98a5f-12a3-1231-124sdf31d21f";
		schemaMetaData = H2DataAccessObject.getInstance().getSchemaMetaDataByGuid(schemaGuid);
		String jsonString = SerializationUtility.serialize(schemaMetaData);
		json = new JSONObject(jsonString);

		logger.debug(json.toString());
		logger.info("getSchemaMetaDataByGuid() passed.");
		assertEquals(schemaGuid, json.get("sId"));
	}

	@Test
	public void getSchemaFieldByGuid() throws DataAccessException {
		Map<String, Profile> map;
		JSONObject json;

		String schemaGuid = "sad89fuy98a5f-12a3-1231-124sdf31d21f";
		map = H2DataAccessObject.getInstance().getSchemaFieldByGuid(schemaGuid, true);
		String jsonString = SerializationUtility.serialize(map);
		json = new JSONObject(jsonString);

		logger.debug(json.toString());
		logger.info("getSchemaFieldByGuid() passed.");
	}

	@Test
	public void getSchemaMetaDataFieldByGuid() throws DataAccessException {
		Map<String, Profile> map;
		JSONObject json;

		String schemaGuid = "sad89fuy98a5f-12a3-1231-124sdf31d21f";
		map = H2DataAccessObject.getInstance().getSchemaFieldByGuid(schemaGuid, false);
		String jsonString = SerializationUtility.serialize(map);
		json = new JSONObject(jsonString);

		logger.debug(json.toString());
		logger.info("getSchemaMetaDataFieldByGuid() passed.");
	}

	@Test
	public void getSampleByGuidTest() throws DataAccessException {
		DataSample sample;
		JSONObject json;

		String sampleGuid = "fdeb76c6-472a-4c6c-8301-e9cfd63e30fa";
		sample = H2DataAccessObject.getInstance().getSampleByGuid(sampleGuid);
		String jsonString = SerializationUtility.serialize(sample);
		json = new JSONObject(jsonString);

		logger.debug(json.toString());
		logger.info("getSampleByGuid() passed.");
		assertEquals(sampleGuid, json.get("dsId"));
	}

	@Test
	public void getSampleMetaDataByGuidTest() throws DataAccessException {
		DataSample sample;
		JSONObject json;

		String sampleGuid = "fdeb76c6-472a-4c6c-8301-e9cfd63e30fa";
		sample = H2DataAccessObject.getInstance().getSampleByGuid(sampleGuid);
		String jsonString = SerializationUtility.serialize(sample);
		json = new JSONObject(jsonString);

		logger.debug(json.toString());
		logger.info("getSampleMetaDataByGuid() passed.");
		assertEquals(sampleGuid, json.get("dsId"));
	}

	@Test
	public void getSampleFieldByGuid() throws DataAccessException {
		Map<String, Profile> map;
		JSONObject json;

		String sampleGuid = "fdeb76c6-472a-4c6c-8301-e9cfd63e30fa";
		map = H2DataAccessObject.getInstance().getSampleFieldByGuid(sampleGuid, true);
		String jsonString = SerializationUtility.serialize(map);
		json = new JSONObject(jsonString);

		logger.debug(json.toString());
		logger.info("getSampleFieldByGuid() passed.");
	}

	@Test
	public void getSampleMetaDataFieldByGuid() throws DataAccessException {
		Map<String, Profile> map;
		JSONObject json;

		String sampleGuid = "fdeb76c6-472a-4c6c-8301-e9cfd63e30fa";
		map = H2DataAccessObject.getInstance().getSampleFieldByGuid(sampleGuid, false);
		String jsonString = SerializationUtility.serialize(map);
		json = new JSONObject(jsonString);

		logger.debug(json.toString());
		logger.info("getSampleFieldByGuid() passed.");
	}

	@Test
	public void getAllSchemaMetaData() throws DataAccessException {
		List<SchemaMetaData> schemaCatalog;

		schemaCatalog = H2DataAccessObject.getInstance().getAllSchemaMetaData();
		schemaCatalog.forEach((k) -> logger.debug(SerializationUtility.serialize(k).toString()));

		logger.info("getAllSchemaMetaData(); passed.");
	}

	@Test
	public void getAllSampleMetaData() throws DataAccessException {
		List<DataSampleMetaData> sampleCatalog;

		sampleCatalog = H2DataAccessObject.getInstance().getAllSampleMetaData();
		sampleCatalog.forEach((k) -> logger.debug(SerializationUtility.serialize(k).toString()));

		logger.info("getAllSchemaMetaData(); passed.");
	}

	@Ignore
	@Test
	public void deleteSchemaByGuid() throws DataAccessException {
		List<SchemaMetaData> schemaList = new ArrayList<SchemaMetaData>();
		List<String> schemaGuids = new ArrayList<String>();
		String schemaGuid = "abfdklgmdklsfngmkldsfjngkdsfngklsdfe";

		// Gets the current list of Schemas
		schemaList = H2DataAccessObject.getInstance().getAllSchemaMetaData();
		logger.debug("Current list of Schema GUIDs:");
		schemaList.forEach((k) -> logger.debug(k.getsGuid()));
		schemaList.forEach((k) -> schemaGuids.add(k.getsGuid()));

		// Assert that the list contains the GUID to be deleted
		assertTrue(schemaGuids.contains(schemaGuid));

		logger.debug("Deleting: " + schemaGuid);
		H2DataAccessObject.getInstance().deleteSchemaByGuid(schemaGuid);

		// Clear the buffer
		schemaList.clear();
		schemaGuids.clear();

		// Retrieves the updated list of Schemas
		schemaList = H2DataAccessObject.getInstance().getAllSchemaMetaData();
		logger.debug("New list of Schema GUIDs:");
		schemaList.forEach((k) -> logger.debug(k.getsGuid()));
		schemaList.forEach((k) -> schemaGuids.add(k.getsGuid()));

		// Assert that the GUID was deleted
		assertFalse(schemaGuids.contains(schemaGuid));

		logger.info("deleteSchemaByGuid(); passed.");
	}

	@Ignore
	@Test
	public void deleteSampleByGuid() throws DataAccessException {
		List<DataSampleMetaData> sampleList = new ArrayList<DataSampleMetaData>();
		List<String> sampleGuids = new ArrayList<String>();
		String sampleGuid = "ghsdkgfdsg-sdfgh-sfdgers-dfghw3e4gfs";

		// Gets the current list of Samples
		sampleList = H2DataAccessObject.getInstance().getAllSampleMetaData();
		logger.debug("Current list of Sample GUIDs:");
		sampleList.forEach((k) -> logger.debug(k.getDsGuid()));
		sampleList.forEach((k) -> sampleGuids.add(k.getDsGuid()));

		// Assert that the list contains the GUID to be deleted
		assertTrue(sampleGuids.contains(sampleGuid));

		logger.debug("Deleting: " + sampleGuid);
		H2DataAccessObject.getInstance().deleteSampleByGuid(sampleGuid);

		// Clear the buffer
		sampleList.clear();
		sampleGuids.clear();

		// Gets the current list of Samples
		sampleList = H2DataAccessObject.getInstance().getAllSampleMetaData();
		logger.debug("New list of Sample GUIDs:");
		sampleList.forEach((k) -> logger.debug(k.getDsGuid()));
		sampleList.forEach((k) -> sampleGuids.add(k.getDsGuid()));

		// Assert that the list contains the GUID to be deleted
		assertFalse(sampleGuids.contains(sampleGuid));

		logger.info("deleteSampleByGuid(); passed.");
	}

	@Ignore
	@Test
	public void deleteByGuid() throws DataAccessException {
		// Testing a Data Sample GUID
		List<DataSampleMetaData> sampleList = new ArrayList<DataSampleMetaData>();
		List<String> sampleGuids = new ArrayList<String>();
		String sampleGuid = "ghsdkgfdsg-sdfgh-sfdgers-dfghw3e4gfs";

		// Gets the current list of Samples
		sampleList = H2DataAccessObject.getInstance().getAllSampleMetaData();
		logger.debug("Current list of Sample GUIDs:");
		sampleList.forEach((k) -> logger.debug(k.getDsGuid()));
		sampleList.forEach((k) -> sampleGuids.add(k.getDsGuid()));

		// Assert that the list contains the GUID to be deleted
		assertTrue(sampleGuids.contains(sampleGuid));

		logger.debug("Deleting: " + sampleGuid);
		H2DataAccessObject.getInstance().deleteByGuid(sampleGuid);

		// Clear the buffer
		sampleList.clear();
		sampleGuids.clear();

		// Gets the current list of Samples
		sampleList = H2DataAccessObject.getInstance().getAllSampleMetaData();
		logger.debug("New list of Sample GUIDs:");
		sampleList.forEach((k) -> logger.debug(k.getDsGuid()));
		sampleList.forEach((k) -> sampleGuids.add(k.getDsGuid()));

		// Assert that the list contains the GUID to be deleted
		assertFalse(sampleGuids.contains(sampleGuid));

		logger.info("deleteSampleByGuid(); passed.");

		// Testing a Schema GUID
		List<SchemaMetaData> schemaList = new ArrayList<SchemaMetaData>();
		List<String> schemaGuids = new ArrayList<String>();
		String schemaGuid = "abfdklgmdklsfngmkldsfjngkdsfngklsdfe";

		// Gets the current list of Schemas
		schemaList = H2DataAccessObject.getInstance().getAllSchemaMetaData();
		logger.debug("Current list of Schema GUIDs:");
		schemaList.forEach((k) -> logger.debug(k.getsGuid()));
		schemaList.forEach((k) -> schemaGuids.add(k.getsGuid()));

		// Assert that the list contains the GUID to be deleted
		assertTrue(schemaGuids.contains(schemaGuid));

		logger.debug("Deleting: " + schemaGuid);
		H2DataAccessObject.getInstance().deleteByGuid(schemaGuid);

		// Clear the buffer
		schemaList.clear();
		schemaGuids.clear();

		// Retrieves the updated list of Schemas
		schemaList = H2DataAccessObject.getInstance().getAllSchemaMetaData();
		logger.debug("New list of Schema GUIDs:");
		schemaList.forEach((k) -> logger.debug(k.getsGuid()));
		schemaList.forEach((k) -> schemaGuids.add(k.getsGuid()));

		// Assert that the GUID was deleted
		assertFalse(schemaGuids.contains(schemaGuid));

		logger.info("deleteSchemaByGuid(); passed.");
	}
	
	@Test
	public void testCreateShiroUser() throws H2DataAccessException, SQLException {
		User user = createRandomUser();
		
		logger.info("Retrieving user with the username: " + user.getUserName());
		User retrievedUser = H2DataAccessObject.getInstance().getUser(user.getUserName());
		assertTrue(retrievedUser.getFirstName() == null);
		logger.info("User not found (expected behavior).");
		logger.info("");
		logger.info("Creating a user with the profile: ");
		logger.info("\tUsername: " + user.getUserName());
		logger.info("\tPassword: " + user.getPassword());
		logger.info("\tFirst Name: " + user.getFirstName());
		logger.info("\tLast Name: " + user.getLastName());
		logger.info("\tSalt: " + user.getSalt());
		logger.info("\tRole: " + user.getUserRole());
		H2DataAccessObject.getInstance().createUser(user);
		
		logger.info("Retrieiving user with the username: " + user.getUserName());
		retrievedUser = H2DataAccessObject.getInstance().getUser(user.getUserName());
		assertTrue(retrievedUser.getFirstName() != null);
		logger.info("User found. Test passed.");
	}
	
	@Test
	public void testGetAllShiroUsers() throws H2DataAccessException, SQLException {
		List<User> users = H2DataAccessObject.getInstance().getAllUsers();
		int intialUserPoolSize = users.size();
		int numUsersToAdd = 5;
		
		logger.info("Initial user pool size is: " + intialUserPoolSize);
		logger.info("Adding " + numUsersToAdd + " users to the database.");
		
		for (int i = 1; i <= numUsersToAdd; i++) {
			logger.info("Adding user " + i);
			User tmpUser = createRandomUser();
			logger.info("\tUsername: " + tmpUser.getUserName());
			H2DataAccessObject.getInstance().createUser(tmpUser);
		}
		
		users = H2DataAccessObject.getInstance().getAllUsers();
		int newUserPoolSize = users.size();
		logger.info("New user pool size is: " + newUserPoolSize);
		assertTrue(newUserPoolSize == intialUserPoolSize + numUsersToAdd);
		logger.info("Test passed.");
	}
	
	@Test
	public void testUpdateShiroUser() throws H2DataAccessException, SQLException {
		User user = createRandomUser();
		logger.info("Creating user in DB.");
		H2DataAccessObject.getInstance().createUser(user);
		
		logger.info("Ensuring write consistency of inputted user values.");
		User retrievedUser = H2DataAccessObject.getInstance().getUser(user.getUserName());
		String originalUser = SerializationUtility.serialize(user);
		
		assertTrue(user.getUserName() == retrievedUser.getUserName());
		assertTrue(user.getFirstName() == retrievedUser.getFirstName());
		assertTrue(user.getLastName() == retrievedUser.getLastName());
		
		logger.info("Modifying the user object.");
		User modifiedUser = user;
		modifiedUser.setFirstName("Jane");
		modifiedUser.setLastName("Bow");
		modifiedUser.setUserRole("admin");
		H2DataAccessObject.getInstance().updateUser(modifiedUser);
		
		logger.info("Ensuring write consistency of inputted user values.");
		retrievedUser = H2DataAccessObject.getInstance().getUser(user.getUserName());
		
		logger.info("Old user object:");
		logger.info(originalUser);
		logger.info("New user object:");
		logger.info(SerializationUtility.serialize(modifiedUser));
		
		logger.info("Comparing what's stored in the database to what's expected (new user object).");
		assertTrue(modifiedUser.getUserName() == retrievedUser.getUserName());
		assertTrue(modifiedUser.getFirstName() == retrievedUser.getFirstName());
		assertTrue(modifiedUser.getLastName() == retrievedUser.getLastName());
		
		logger.info("Test passed.");
	}
	
	@Test
	public void testDeleteShiroUser() throws H2DataAccessException, SQLException {
		User user = createRandomUser();
		
		logger.info("Created user.");
		H2DataAccessObject.getInstance().createUser(user);
		
		logger.info("Ensuring user was successfully written to database.");
		User retrievedUser = H2DataAccessObject.getInstance().getUser(user.getUserName());
		assertTrue(retrievedUser.getUserName() != null);
		
		logger.info("Deleting user.");
		H2DataAccessObject.getInstance().deleteUser(user.getUserName());
		
		logger.info("Ensuring user was successfully removed from the database.");
		retrievedUser = H2DataAccessObject.getInstance().getUser(user.getUserName());
		assertTrue(retrievedUser.getUserName() == null);
		
		logger.info("Test passed.");
	}
	
	@Test
	public void testAddSecurityQuestionsForUser() throws H2DataAccessException, SQLException {
		User user = createRandomUser();
		
		logger.info("Ensuring there is no existing user for the impending insertion.");
		User retrievedUser = H2DataAccessObject.getInstance().getSecurityQuestionsForUser(user.getUserName());
		// ensure the user's security questions doesn't exist before proceeeding
		assertTrue(retrievedUser.getSecurityQuestion1() == null);
		assertTrue(retrievedUser.getSecurityQuestion2() == null);
		assertTrue(retrievedUser.getSecurityQuestion3() == null);
		
		logger.info("Creating the user and adding security questions in DB.");
		H2DataAccessObject.getInstance().createUser(user);
		H2DataAccessObject.getInstance().addSecurityQuestionsForUser(user);
		
		retrievedUser = H2DataAccessObject.getInstance().getSecurityQuestionsForUser(user.getUserName());
		assertTrue(retrievedUser.getSecurityQuestion1().equals(user.getSecurityQuestion1()));
		assertTrue(retrievedUser.getSecurityQuestion2().equals(user.getSecurityQuestion2()));
		assertTrue(retrievedUser.getSecurityQuestion3().equals(user.getSecurityQuestion3()));
		
		assertTrue(retrievedUser.getSecurityQuestion1Answer().equals(user.getSecurityQuestion1Answer()));
		assertTrue(retrievedUser.getSecurityQuestion2Answer().equals(user.getSecurityQuestion2Answer()));
		assertTrue(retrievedUser.getSecurityQuestion3Answer().equals(user.getSecurityQuestion3Answer()));
		logger.info("Confirmed successful insertion of security questions.");
	}
	
	@Test
	public void testVerifySecurityQuestionsForUser() throws H2DataAccessException, SQLException {
		User user = createRandomUser();
		
		logger.info("Ensuring there is no existing user for the impending insertion.");
		User retrievedUser = H2DataAccessObject.getInstance().getSecurityQuestionsForUser(user.getUserName());
		// ensure the user doesn't exist before proceeding
		assertTrue(retrievedUser.getUserName() == null);
		logger.info("Creating the user in DB.");
		H2DataAccessObject.getInstance().createUser(user);
		H2DataAccessObject.getInstance().addSecurityQuestionsForUser(user);
		
		logger.info("Retrieving the username from the name: " + user.getFirstName());
		retrievedUser = H2DataAccessObject.getInstance().getUsernameFromFirstName(user.getFirstName());
		logger.info("Got username: " + retrievedUser.getUserName() + ". Expected: " + user.getUserName());
		assertTrue(retrievedUser.getUserName().equals(user.getUserName()));
		
		logger.info("Submitting: " + SerializationUtility.serialize(user));
		
		boolean result = H2DataAccessObject.getInstance().verifySecurityQuestionsForUser(user);
		assertTrue(result);
		
		logger.info("Test passed.");
	} 
	
	@Test
	public void testDuplicateUsernameInsertion() throws H2DataAccessException, SQLException {
		User user = createRandomUser();
		
		logger.info("Retrieving user with the username: " + user.getUserName());
		User retrievedUser = H2DataAccessObject.getInstance().getUser(user.getUserName());
		assertTrue(retrievedUser.getFirstName() == null);
		logger.info("User not found (expected behavior).");
		logger.info("");
		logger.info("Creating a user with the profile: ");
		logger.info("\tUsername: " + user.getUserName());
		logger.info("\tPassword: " + user.getPassword());
		logger.info("\tFirst Name: " + user.getFirstName());
		logger.info("\tLast Name: " + user.getLastName());
		logger.info("\tSalt: " + user.getSalt());
		logger.info("\tRole: " + user.getUserRole());
		H2DataAccessObject.getInstance().createUser(user);
		
		logger.info("Retrieiving user with the username: " + user.getUserName());
		retrievedUser = H2DataAccessObject.getInstance().getUser(user.getUserName());
		assertTrue(retrievedUser.getFirstName() != null);
		
		logger.info("Attempting to insert duplicate user.");
		try { 
			String retrievedUsername = H2DataAccessObject.getInstance().createUser(user);
		} catch (H2DataAccessException e) {
			assertTrue(e.getMessage().equals("DUPLICATE_USERNAME"));
		}
		
		logger.info("Test passed.");
	}
	
	@Test
	public void testGetSecurityQuestionsFromBank() throws H2DataAccessException, SQLException {
		logger.info("Populating the DB with the initial security questions.");
		H2DataAccessObject.getInstance().initializeDefaultSecurityQuestions();
		
		logger.info("Retrieving security questions from the DB.");
		List<String> questions = H2DataAccessObject.getInstance().getAllSecurityQuestions();
		assertTrue(questions.size() > 0);
		
		logger.info("Security questions:");
		
		for (String question : questions) { 
			logger.info("\t" + question);
		}
		
		logger.info("Test passed.");
	}
	
	@Test
	public void testGetUsernameFromFirstName() throws H2DataAccessException, SQLException {
		User user = createRandomUser();
		
		logger.info("Ensuring there is no existing user for the impending insertion.");
		User retrievedUser = H2DataAccessObject.getInstance().getSecurityQuestionsForUser(user.getUserName());
		// ensure the user doesn't exist before proceeding
		assertTrue(retrievedUser.getUserName() == null);
		logger.info("Creating the user in DB.");
		H2DataAccessObject.getInstance().createUser(user);
		
		logger.info("Retrieving the username from the name: " + user.getFirstName());
		retrievedUser = H2DataAccessObject.getInstance().getUsernameFromFirstName(user.getFirstName());
		logger.info("Got username: " + retrievedUser.getUserName() + ". Expected: " + user.getUserName());
		assertTrue(retrievedUser.getUserName().equals(user.getUserName()));
		logger.info("Test passed.");
	}
	
	// Private methods
	private User createRandomUser() {
		User user = new User();
		user.setUserName(UUID.randomUUID().toString().substring(0, 15));
		user.setPassword(UUID.randomUUID().toString().substring(0, 15));
		user.setSalt(UUID.randomUUID().toString().substring(0, 15));
		user.setFirstName(UUID.randomUUID().toString().substring(0, 15));
		user.setLastName(UUID.randomUUID().toString().substring(0, 15));
		user.setUserRole("user");
		
		user.setSecurityQuestion1("What is 1 + 1?");
		user.setSecurityQuestion1Answer("2");
		user.setSecurityQuestion2("What is 2 + 2?");
		user.setSecurityQuestion2Answer("4");
		user.setSecurityQuestion3("What is 3 * 3?");
		user.setSecurityQuestion3Answer("9");
		
		return user;
	}
}