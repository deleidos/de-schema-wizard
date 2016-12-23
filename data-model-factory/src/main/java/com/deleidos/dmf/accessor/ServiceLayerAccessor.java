package com.deleidos.dmf.accessor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.deleidos.dmf.web.SchemaWizardSessionUtility;
import com.deleidos.dmf.web.SchemaWizardSessionUtility.StatusReport;
import com.deleidos.dp.beans.DataSample;
import com.deleidos.dp.beans.DataSampleMetaData;
import com.deleidos.dp.beans.Profile;
import com.deleidos.dp.beans.Schema;
import com.deleidos.dp.beans.SchemaMetaData;
import com.deleidos.dp.beans.User;
import com.deleidos.dp.deserializors.ConversionUtility;
import com.deleidos.dp.deserializors.SerializationUtility;
import com.deleidos.dp.exceptions.DataAccessException;
import com.deleidos.dp.exceptions.H2DataAccessException;
import com.deleidos.dp.exceptions.IEDataAccessException;
import com.deleidos.dp.exceptions.SchemaNotFoundException;
import com.deleidos.dp.export.AbstractExporter;
import com.deleidos.dp.export.SQLExporter.SQLExportException;
import com.deleidos.dp.h2.H2DataAccessObject;
import com.deleidos.dp.interpretation.InterpretationEngine;
import com.deleidos.dp.interpretation.InterpretationEngineFacade;

/**
 * Service layer for Schema Wizard.
 * 
 * @author yoonj1
 *
 */
public class ServiceLayerAccessor implements ServiceLayer {
	public static final Logger logger = Logger.getLogger(ServiceLayerAccessor.class);
	H2DataAccessObject h2Dao;
	InterpretationEngine interpretationEngine;
	SchemaWizardSessionUtility sessionUtility;
	private ResourceBundle bundle = ResourceBundle.getBundle("error-messages");

	private static final String DUPLICATE_USERNAME = "DUPLICATE_USERNAME";

	public ServiceLayerAccessor() {
		try {
			h2Dao = H2DataAccessObject.getInstance();
			interpretationEngine = InterpretationEngineFacade.getInstance();
			sessionUtility = SchemaWizardSessionUtility.getInstance();
		} catch (DataAccessException e) {
			logger.error(e);
		}
	}

	/**
	 * Gets the catalog from the H2 Database.
	 * 
	 * @return
	 */
	public Response getCatalog() {
		JSONObject json = new JSONObject();

		try {
			List<SchemaMetaData> schemaList = h2Dao.getAllSchemaMetaData();
			List<DataSampleMetaData> sampleList = h2Dao.getAllSampleMetaData();
			JSONArray domainJson = interpretationEngine.getAvailableDomains();

			json.put("schemaCatalog", new JSONArray(SerializationUtility.serialize(schemaList)));
			json.put("dataSamplesCatalog", new JSONArray(SerializationUtility.serialize(sampleList)));
			json.put("domainsCatalog", new JSONArray(domainJson.toString()));
			return Response.ok(json.toString(), MediaType.APPLICATION_JSON).build();
		} catch (JSONException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		} catch (ProcessingException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.GATEWAY_TIMEOUT);
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets a list of interpretations from a domain guid.
	 * 
	 * @param domainGuid
	 * @return
	 */
	public Response getDomainInterpretations(String domainGuid) {
		try {
			JSONObject jObject = interpretationEngine.getInterpretationListByDomainGuid(domainGuid);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (ProcessingException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.GATEWAY_TIMEOUT);
		} catch (IEDataAccessException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("ie.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Creates a domain in the Interpretation Engine MongoDB
	 * 
	 * @param domainJson
	 * @return
	 */
	public Response createDomain(JSONObject domainJson) {
		try {
			JSONObject jObject = interpretationEngine.createDomain(domainJson);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (ProcessingException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.GATEWAY_TIMEOUT);
		} catch (DataAccessException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("ie.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Creates an interpretation in the Interpretation Engine MongoDB
	 * 
	 * @param interpretationJson
	 * @return
	 */
	public Response createInterpretation(JSONObject interpretationJson) {
		try {
			JSONObject jObject = interpretationEngine.createInterpretation(interpretationJson);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (ProcessingException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.GATEWAY_TIMEOUT);
		} catch (DataAccessException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("ie.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates a domain in the Interpretation Engine MongoDB
	 * 
	 * @param domainJson
	 * @return
	 */
	public Response updateDomain(JSONObject domainJson) {
		try {
			JSONObject jObject = interpretationEngine.updateDomain(domainJson);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (ProcessingException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.GATEWAY_TIMEOUT);
		} catch (DataAccessException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("ie.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates an interpretation in the Interpretation Engine MongoDB
	 * 
	 * @param interpretationJson
	 * @return
	 */
	public Response updateInterpretation(JSONObject interpretationJson) {
		try {
			JSONObject jObject = interpretationEngine.updateInterpretation(interpretationJson);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (ProcessingException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.GATEWAY_TIMEOUT);
		} catch (DataAccessException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("ie.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Deletes a domain in the Interpretation Engine MongoDB
	 * 
	 * @param domainJson
	 * @return
	 */
	public Response deleteDomain(JSONObject domainJson) {
		try {
			JSONObject jObject = interpretationEngine.deleteDomain(domainJson);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (ProcessingException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.GATEWAY_TIMEOUT);
		} catch (DataAccessException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("ie.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Deletes an interpretation in the Interpretation Engine MongoDB
	 * 
	 * @param interpretationJson
	 * @return Number of records modified
	 */
	public Response deleteInterpretation(JSONObject interpretationJson) {
		try {
			JSONObject jObject = interpretationEngine.deleteInterpretation(interpretationJson);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (ProcessingException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.GATEWAY_TIMEOUT);
		} catch (DataAccessException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("ie.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Validates a Python script that is encoded in base64.
	 * 
	 * @param Python
	 *            script encoded in base64
	 * @return
	 */
	public Response validatePythonScript(String iId) {
		try {
			JSONObject iIdJson = new JSONObject();
			JSONObject jObject = interpretationEngine.validatePythonScript(iIdJson.put("iId", iId));
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (ProcessingException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.GATEWAY_TIMEOUT);
		} catch (DataAccessException e) {
			logger.error(e.toString());
			JSONObject json = new JSONObject();
			json.put("type", "error");
			json.put("row", "0");
			json.put("text", bundle.getString("ie.unexpected.error"));
			return generateResponse(Status.SERVICE_UNAVAILABLE, json.toString());
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("ie.unexpected.error"));
			JSONObject json = new JSONObject();
			json.put("type", "error");
			json.put("row", "0");
			json.put("text", bundle.getString("ie.unexpected.error"));
			return generateResponse(Status.INTERNAL_SERVER_ERROR, json.toString());
		}
	}

	/**
	 * Tests a Python script with example data
	 * 
	 * @param iId
	 * @return
	 */
	public Response testPythonScript(String iId) {
		try {
			JSONObject iIdJson = new JSONObject();
			JSONObject jObject = interpretationEngine.testPythonScript(iIdJson.put("iId", iId));
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (ProcessingException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.GATEWAY_TIMEOUT);
		} catch (DataAccessException e) {
			logger.error(e.toString());
			return generateEmptyResponse(Status.SERVICE_UNAVAILABLE);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("ie.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 
	 */
	public Response addSchema(JSONObject schemaJson) {
		Schema schema = SerializationUtility.deserialize(schemaJson, Schema.class);

		try {
			JSONObject jObject = new JSONObject();
			jObject.put("schemaGuid", h2Dao.addSchema(schema));
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets Schema bean from the H2 Database by GUID.
	 * 
	 * @param guid
	 * @return Schema bean as a JSON Object
	 */
	public Response getSchemaByGuid(String guid) {
		try {
			// Show histogram
			Schema schema = h2Dao.getSchemaByGuid(guid, true);
			schema.setsProfile(ConversionUtility.addObjectProfiles(schema.getsProfile()));
			String jsonString = SerializationUtility.serialize(schema);
			JSONObject jObject = new JSONObject(jsonString);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets Schema bean with no histogram from the H2 Database by GUID.
	 * 
	 * @param guid
	 * @return Schema bean as a JSON Object
	 */
	public Response getSchemaByGuidNoHistogram(String guid) {
		try {
			// Do not show histogram
			Schema schema = h2Dao.getSchemaByGuid(guid, false);
			String jsonString = SerializationUtility.serialize(schema);
			JSONObject jObject = new JSONObject(jsonString);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets SchemaMetaData bean from the H2 Database by GUID.
	 * 
	 * @param guid
	 * @return SchemaMetaData bean as a JSON Object
	 */
	public Response getSchemaMetaDataByGuid(String guid) {
		try {
			SchemaMetaData schemaMetaData = h2Dao.getSchemaMetaDataByGuid(guid);
			String jsonString = SerializationUtility.serialize(schemaMetaData);
			JSONObject jObject = new JSONObject(jsonString);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets Data Sample bean from the H2 Database by GUID.
	 * 
	 * @param guid
	 * @return Data Sample bean as a JSON object
	 */
	public Response getSampleByGuid(String guid) {
		try {
			DataSample sample = h2Dao.getSampleByGuid(guid);
			sample.setDsProfile(ConversionUtility.addObjectProfiles(sample.getDsProfile()));
			String jsonString = SerializationUtility.serialize(sample);
			JSONObject jObject = new JSONObject(jsonString);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets Data Sample Meta Data bean from the H2 Database by GUID.
	 * 
	 * @param guid
	 * @return Data Sample Meta Data bean as a JSON object
	 */
	public Response getSampleMetaDataByGuid(String guid) {
		try {
			DataSampleMetaData sampleMetaData = h2Dao.getSampleMetaDataByGuid(guid);
			String jsonString = SerializationUtility.serialize(sampleMetaData);
			JSONObject jObject = new JSONObject(jsonString);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets the field of a given Schema.
	 * 
	 * @param guid
	 * @return Field descriptor
	 */
	public Response getSchemaFieldByGuid(String guid) {
		try {
			Map<String, Profile> map = h2Dao.getSchemaFieldByGuid(guid, true);
			String jsonString = SerializationUtility.serialize(map);
			JSONObject jObject = new JSONObject(jsonString);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets the field meta data of a given Schema.
	 * 
	 * @param guid
	 * @return Field descriptor
	 */
	public Response getSchemaMetaDataFieldByGuid(String guid) {
		try {
			Map<String, Profile> map = h2Dao.getSchemaFieldByGuid(guid, false);
			String jsonString = SerializationUtility.serialize(map);
			JSONObject jObject = new JSONObject(jsonString);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets the field of a given Sample.
	 * 
	 * @param guid
	 * @return Field descriptor
	 */
	public Response getSampleFieldByGuid(String guid) {
		try {
			Map<String, Profile> map = h2Dao.getSampleFieldByGuid(guid, true);
			String jsonString = SerializationUtility.serialize(map);
			JSONObject jObject = new JSONObject(jsonString);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets the field meta data of a given Sample.
	 * 
	 * @param guid
	 * @return Field descriptor
	 */
	public Response getSampleMetaDataFieldByGuid(String guid) {
		try {
			Map<String, Profile> map = h2Dao.getSampleFieldByGuid(guid, false);
			String jsonString = SerializationUtility.serialize(map);
			JSONObject jObject = new JSONObject(jsonString);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Deletes a Schema by its GUID.
	 * 
	 * @param guid
	 *            The GUID of a Schema
	 * @return
	 */
	public Response deleteSchemaByGuid(String guid) {
		try {
			boolean deleted = h2Dao.deleteSchemaByGuid(guid);
			JSONObject jObject = new JSONObject();
			jObject.put("guid", guid);
			jObject.put("deleted", deleted);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Deletes a Data Sample by its GUID.
	 * 
	 * @param guid
	 *            The GUID of a Data Sample
	 * @return
	 */
	public Response deleteSampleByGuid(String guid) {
		try {
			boolean deleted = h2Dao.deleteSampleByGuid(guid);
			JSONObject jObject = new JSONObject();
			jObject.put("guid", guid);
			jObject.put("deleted", deleted);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Takes an ambiguous GUID and determinatively deletes it from the database.
	 * 
	 * @param guid
	 *            A GUID from either a Schema or Data Sample
	 * @return
	 */
	public Response deleteByGuid(String guid) {
		try {
			h2Dao.deleteByGuid(guid);
			JSONObject jObject = new JSONObject();
			jObject.put("deleted", guid);
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Response createUser(User user) {
		try {
			JSONObject jObject = new JSONObject();
			jObject.put("username", h2Dao.createUser(user));
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			if (e.getMessage().equals("DUPLICATE_USERNAME")) {
				return generateResponse(Status.CONFLICT, "Cannot insert a duplicate username.");
			} else {
				logger.error("Error accessing the H2 database.");
				return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
			}
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Response getAllUsers() {
		try {
			JSONObject jObject = new JSONObject();
			List<User> userList = h2Dao.getAllUsers();
			JSONArray userPackage = new JSONArray();

			for (User user : userList) {
				JSONObject tmpUser = new JSONObject();
				tmpUser.put("userName", user.getUserName());
				tmpUser.put("firstName", user.getFirstName());
				tmpUser.put("lastName", user.getLastName());
				tmpUser.put("userRole", user.getUserRole());

				userPackage.put(tmpUser);
			}

			jObject.put("userPackage", userPackage);

			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Response getUser(String username) {
		try {
			JSONObject jObject = new JSONObject();
			User user = h2Dao.getUser(username);
			boolean createdSecurityQuestions = false;

			User tmpUser = h2Dao.getSecurityQuestionsForUser(username);
			if (tmpUser.getSecurityQuestion1() != null || tmpUser.getSecurityQuestion2() != null
					|| tmpUser.getSecurityQuestion3() != null)
				createdSecurityQuestions = true;

			jObject.put("userName", user.getUserName());
			jObject.put("firstName", user.getFirstName());
			jObject.put("lastName", user.getLastName());
			jObject.put("userRole", user.getUserRole());
			jObject.put("createdSecurityQuestions", createdSecurityQuestions);

			return generateResponse(Status.OK, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public JSONObject getUserJson(String username) {
		JSONObject jObject = new JSONObject();

		try {
			User user = h2Dao.getUser(username);

			jObject.put("userName", user.getUserName());
			jObject.put("firstName", user.getFirstName());
			jObject.put("lastName", user.getLastName());
			jObject.put("userRole", user.getUserRole());

			if (user.getUserRole().isEmpty()) {
				logger.info("Requesting user info returned a null object");
				jObject.put("userRole", "NOT_FOUND");
			}

			return jObject;
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			jObject.put("userRole", "NOT_FOUND");
			return null;
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			jObject.put("userRole", "NOT_FOUND");
			return null;
		}
	}

	public Response updateUser(User user) {
		try {
			String username = user.getUserName();
			JSONObject jObject = new JSONObject();
			jObject.put("username", username);
			jObject.put("updated", h2Dao.updateUser(user));
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Response deleteUser(String username) {
		try {
			JSONObject jObject = new JSONObject();
			jObject.put("username", username);
			jObject.put("deleted", h2Dao.deleteUser(username));
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Response getUsernameFromFirstName(String firstName) {
		try {
			User user = h2Dao.getUsernameFromFirstName(firstName);
			JSONObject jObject = new JSONObject();
			jObject.put("username", user.getUserName());
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			if (e.getMessage().equals("DUPLICATE_FIRST_NAME")) {
				// We need to come up with a better way to handle duplicate first names
				// but as it stands, the solution is out of scope for RC1 TODO after RC1
				return generateResponse(Status.CONFLICT, "Duplicate names in DB. Cannot proceed.");
			} else {
				logger.error("Error accessing the H2 database.");
				return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
			}
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Response addSecurityQuestionsForUser(User user) {
		try {
			JSONObject jObject = new JSONObject();
			jObject.put("created", h2Dao.addSecurityQuestionsForUser(user));
			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Response getAllSecurityQuestions() {
		try {
			List<String> questions = h2Dao.getAllSecurityQuestions();
			int totalQuestionsThird = questions.size() / 3;

			JSONObject jObject = new JSONObject();
			JSONArray secQuestions1 = new JSONArray();
			JSONArray secQuestions2 = new JSONArray();
			JSONArray secQuestions3 = new JSONArray();

			for (String question : questions) {
				if (secQuestions1.length() < totalQuestionsThird)
					secQuestions1.put(question);
				else if (secQuestions2.length() < totalQuestionsThird)
					secQuestions2.put(question);
				else if (secQuestions3.length() < totalQuestionsThird)
					secQuestions3.put(question);
			}

			jObject.put("securityQuestion1", secQuestions1);
			jObject.put("securityQuestion2", secQuestions2);
			jObject.put("securityQuestion3", secQuestions3);

			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Response getSecurityQuestionsForUser(String userName) {
		try {
			User user = h2Dao.getSecurityQuestionsForUser(userName);

			JSONObject securityQuestions = new JSONObject();

			if (user.getSecurityQuestion1() != null || user.getSecurityQuestion2() != null
					|| user.getSecurityQuestion3() != null) {
				JSONObject questions = new JSONObject();
				securityQuestions.put("userName", user.getUserName());

				questions.put("securityQuestion1", user.getSecurityQuestion1());
				questions.put("securityQuestion2", user.getSecurityQuestion2());
				questions.put("securityQuestion3", user.getSecurityQuestion3());

				securityQuestions.put("questions", questions);
			}

			return generateResponse(Status.ACCEPTED, securityQuestions.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Response verifySecurityQuestionsForUser(User user, String tmpPass) {
		try {
			JSONObject jObject = new JSONObject();
			jObject.put("userName", user.getUserName());
			jObject.put("verification", h2Dao.verifySecurityQuestionsForUser(user));

			if (h2Dao.verifySecurityQuestionsForUser(user)) {
				User sysGenPasswordUser = new User();
				sysGenPasswordUser.setUserName(user.getUserName());
				sysGenPasswordUser.setPassword(user.getPassword());
				sysGenPasswordUser.setSalt(user.getSalt());
				updateUser(sysGenPasswordUser);
				jObject.put("password", tmpPass);
			}

			return generateResponse(Status.ACCEPTED, jObject.toString());
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return generateResponse(Status.SERVICE_UNAVAILABLE, "Unable to reach the H2 database.");
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return generateEmptyResponse(Status.INTERNAL_SERVER_ERROR);
		}
	}

	public boolean initializeDefaultUser(User user) {
		try {
			return h2Dao.initializeDefaultUser(user);
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return false;
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return false;
		}
	}

	public boolean initializeDefaultSecurityQuestions() {
		try {
			return h2Dao.initializeDefaultSecurityQuestions();
		} catch (H2DataAccessException e) {
			logger.error("Error accessing the H2 database.");
			return false;
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error(bundle.getString("h2.unexpected.error"));
			return false;
		}
	}

	// Test methods
	public boolean testH2Connection() throws H2DataAccessException {
		return h2Dao.testDefaultConnection();
	}

	public boolean testIEConnection() throws IEDataAccessException {
		try {
			interpretationEngine.testMongoConnection();
			return true;
		} catch (IEDataAccessException e) {
			throw e;
		} catch (Exception e) {
			throw new IEDataAccessException("An unexpected error occured.");
		}
	}

	public String healthCheck() {
		return healthCheck(false);
	}

	public String healthCheck(boolean includeJobStatus) {
		Map<String, Object> topLevel = new HashMap<String, Object>();
		Map<String, Object> serverStatus = new HashMap<String, Object>();

		long startTime = System.currentTimeMillis();

		try {
			testH2Connection();
			serverStatus.put("H2", "Up");
		} catch (H2DataAccessException e) {
			serverStatus.put("H2", "Down");
		} catch (ProcessingException e) {
			logger.error("Server timed out trying to reach H2.");
		}

		try {
			testIEConnection();
			serverStatus.put("Interpretation Engine", "Up");
			serverStatus.put("MongoDB", "Up");
		} catch (IEDataAccessException e) {
			if (e instanceof IEDataAccessException.DeadMongo) {
				serverStatus.put("Interpretation Engine", "Up");
				serverStatus.put("MongoDB", "Down");
			} else {
				serverStatus.put("Interpretation Engine", "Down");
				serverStatus.put("MongoDB", "Unknown");
			}
		} catch (ProcessingException e) {
			logger.error("Server timed out trying to reach the Interpretation Engine.");
		}

		long endTime = System.currentTimeMillis();

		topLevel.put("Server Statuses", serverStatus);
		topLevel.put("Fetch time", endTime - startTime + "ms");

		if (includeJobStatus) {
			StatusReport statusReport = SchemaWizardSessionUtility.getInstance().getStatusReport();
			topLevel.put("Job Statuses", statusReport);
		}

		return SerializationUtility.serialize(topLevel);
	}

	// H2 Realm Methods
	/**
	 * Retrieves the password of a given user.
	 * 
	 * Does not return a response unlike the other methods in the Service Layer
	 * Accessor because this method is only used by the H2Realm.
	 * 
	 * @param username
	 * @return
	 * @throws H2DataAccessException
	 */
	public User getPasswordForUser(String username) throws H2DataAccessException {
		try {
			return h2Dao.getPasswordForUser(username);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error("Couldn't retrieve role names for user " + username + ".");
			throw new H2DataAccessException("Couldn't retrieve role names.");
		}
	}

	/**
	 * Retrieves roles of a given user.
	 * 
	 * Does not return a response unlike the other methods in the Service Layer
	 * Accessor because this method is only used by the H2Realm.
	 * 
	 * @param username
	 * @return
	 * @throws H2DataAccessException
	 */
	public Set<String> getRoleNamesForUser(String username) throws H2DataAccessException {
		try {
			return h2Dao.getRoleNamesForUser(username);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error("Couldn't retrieve role names for user " + username + ".");
			throw new H2DataAccessException("Couldn't retrieve role names.");
		}
	}

	/**
	 * Retrieves permissions of a given user.
	 * 
	 * Does not return a response unlike the other methods in the Service Layer
	 * Accessor because this method is only used by the H2Realm.
	 * 
	 * @param username
	 * @param roleNames
	 * @return
	 * @throws H2DataAccessException
	 */
	public Set<String> getPermissions(String username, Collection<String> roleNames) throws H2DataAccessException {
		try {
			return h2Dao.getPermissions(username, roleNames);
		} catch (Exception e) {
			logger.error(e.toString());
			logger.error("Couldn't retrieve role names for user " + username + ".");
			throw new H2DataAccessException("Couldn't retrieve permissions.");
		}
	}

	@Override
	public Response exportSchema(Map<String, Object> parameters) {
		final String EXPORT_RESULT_KEY = "export-text";
		final String EXPORT_ERROR_KEY = "error";

		Status status = Status.ACCEPTED;
		JSONObject responseBody = new JSONObject();
		try {
			responseBody.put(EXPORT_RESULT_KEY, AbstractExporter.export(parameters));
		} catch (SchemaNotFoundException | SQLExportException e) {
			logger.error(e);
			responseBody.put(EXPORT_ERROR_KEY, e.getMessage());
			status = Status.BAD_REQUEST;
		} catch (Exception e) {
			logger.error(e);
			responseBody.put(EXPORT_ERROR_KEY, e.getMessage());
			status = Status.INTERNAL_SERVER_ERROR;
		}
		return generateResponse(status, responseBody.toString());
	}

	// Private methods
	private Response generateResponse(Response.Status status, String message) {
		return Response.status(status).entity(message).build();
	}

	private Response generateEmptyResponse(Response.Status status) {
		JSONObject emptyJson = new JSONObject();
		return Response.status(status).entity(emptyJson.toString()).build();
	}

}
