package com.deleidos.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.SimpleByteSource;
import org.json.JSONArray;
import org.json.JSONObject;

import com.deleidos.dmf.accessor.ServiceLayerAccessor;
import com.deleidos.dmf.analyzer.TikaAnalyzer;
import com.deleidos.dmf.exception.AnalyticsCancelledWorkflowException;
import com.deleidos.dmf.web.SchemaWizardSessionUtility;
import com.deleidos.dp.beans.User;
import com.deleidos.dp.deserializors.SerializationUtility;
import com.deleidos.dp.enums.Roles;
import com.deleidos.sw.realms.H2Realm;
import com.fasterxml.jackson.core.type.TypeReference;

@Path("/")
public class SchemaWizardController implements ISchemaWizardController {

	private static Logger logger = Logger.getLogger(SchemaWizardController.class);

	private final String uploadDirectory;

	private ResourceBundle bundle = ResourceBundle.getBundle("error-messages");

	private final TikaAnalyzer analyzerService;

	private final ServiceLayerAccessor dataService;

	private final SchemaWizardSessionUtility sessionUtil;

	private static final String ADMIN_USER_NAME = "admin";

	private static final String ADMIN_PASSWORD = "admin";

	private static final String ADMIN = Roles.ADMIN;

	private static final String USER = Roles.USER;

	private static final String NOT_FOUND = "NOT_FOUND";

	/**
	 * Constructor
	 *
	 * @param service
	 */
	public SchemaWizardController(TikaAnalyzer analyzerService, ServiceLayerAccessor dataService,
			SchemaWizardSessionUtility sessionUtil, String uploadDirectory) {
		SecurityManager securityManager = new DefaultSecurityManager(new H2Realm());
		SecurityUtils.setSecurityManager(securityManager);
		this.sessionUtil = sessionUtil;
		this.analyzerService = analyzerService;
		this.dataService = dataService;
		this.uploadDirectory = uploadDirectory;

		if (dataService.initializeDefaultUser(this.defaultUser())) {
			logger.debug("No users detected therefore a default user was created.");
		} else {
			logger.debug("User(s) in the application exist. Not initializing any new users.");
		}

		if (dataService.initializeDefaultSecurityQuestions()) {
			logger.debug("No security questions detected. Populating the question bank with defaults.");
		} else {
			logger.debug("Security questions detected. Not initializing any new questions.");
		}
	} // constructor

	/**
	 * Return the servlet's session id for this session
	 *
	 * @return <sessionId>
	 *
	 *         Only available to Schema Wizard
	 */
	@GET
	@Path("/sessionId")
	@RolesAllowed({ ADMIN, USER })
	public Response getSessionId(@Context HttpServletRequest request) {
		String sessionId = request.getSession().getId();
		logger.debug("");
		logger.debug("sessionId: " + sessionId);
		JSONObject jObject = new JSONObject();
		jObject.put("sessionId", sessionId);
		logger.debug("");
		logger.debug("jObject: " + jObject.toString());
		logger.debug("");
		if (sessionId == null || sessionId.equals("")) {
			return Response.status(Response.Status.EXPECTATION_FAILED).build();
		} else {
			return Response.ok(jObject.toString(), MediaType.APPLICATION_JSON).build();
		}
	} // getSessionId

	/**
	 * Query the H2 database for the entire catalog. Return metadata for catalog
	 * schemas, data samples, and domains.
	 *
	 * @return <catalog>
	 *
	 *         Only available to Schema Wizard
	 */
	@GET
	@Path("/catalog")
	@RolesAllowed({ ADMIN, USER })
	public Response getCatalog() {
		Response response;
		logger.debug("");
		logger.debug("getCatalog request received");
		logger.debug("");
		response = dataService.getCatalog();
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // getCatalog

	/**
	 * Save the schema in the H2 database. The schema is sent in the payload of
	 * the request.
	 *
	 * @param schema
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@POST
	@Path("/schema")
	@RolesAllowed({ ADMIN, USER })
	public Response saveSchema(String schema) {
		logger.debug("");
		logger.debug("saveSchema request received");
		logger.debug("");
		JSONObject jObject = new JSONObject(schema);
		logger.debug("");
		logger.debug("schema: " + jObject.toString());
		logger.debug("");
		Response response = dataService.addSchema(jObject);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // saveSchema

	/**
	 * Query the H2 database for the schema with the requested id.
	 *
	 * @param schemaId
	 * @return
	 */
	@GET
	@Path("/schema/{schemaId}")
	@RolesAllowed({ ADMIN, USER })
	public Response getSchema(@PathParam("schemaId") String schemaId) {
		logger.debug("");
		logger.debug("getSchema request received: " + schemaId);
		logger.debug("");
		logger.debug("no histogram");
		Response response = dataService.getSchemaByGuid(schemaId);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // getSchema

	/**
	 * Query the H2 database for the schema with the requested id. Do not
	 * include histogram data in the returned object.
	 *
	 * @param schemaId
	 * @return <schema>
	 */
	@GET
	@Path("/schema/{schemaId}/{nohistogram}")
	@RolesAllowed({ ADMIN, USER })
	public Response getSchema(@PathParam("schemaId") String schemaId, @PathParam("nohistogram") String nohistogram) {
		logger.debug("");
		logger.debug("getSchema request received: " + schemaId + "   " + nohistogram);
		logger.debug("");
		logger.debug("no histogram");
		Response response = dataService.getSchemaByGuidNoHistogram(schemaId);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // getSchema

	/**
	 * Delete the schema with the requested id from the H2 database.
	 *
	 * @param schemaId
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@DELETE
	@Path("/schema/{schemaId}")
	@RolesAllowed({ ADMIN, USER })
	public Response deleteSchema(@PathParam("schemaId") String schemaId) {
		logger.debug("");
		logger.debug("deleteSchema request received: " + schemaId);
		logger.debug("");
		Response response = dataService.deleteSchemaByGuid(schemaId);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // deleteSchema

	/**
	 * Query the H2 database for the schema with the requested id. Return
	 * metadata for the schema
	 *
	 * @param schemaId
	 * @return <schema-meta-data>
	 */
	@GET
	@Path("/schemaMetaData/{schemaId}")
	@RolesAllowed({ ADMIN, USER })
	public Response getSchemaMetaData(@PathParam("schemaId") String schemaId) {
		logger.debug("");
		logger.debug("getSchemaMetaData request received: " + schemaId);
		logger.debug("");
		Response response = dataService.getSchemaMetaDataByGuid(schemaId);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // getSchemaMetaData

	/**
	 * Add a field to the schema. The field meta data and characterization sent
	 * in the payload of the request. Meta dat is <main-type-value>,
	 * <detail-json<>>, <classification-json>.
	 *
	 * @param schemaId
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@POST
	@Path("/schemaField/{schemaId}")
	@RolesAllowed({ ADMIN, USER })
	public Response addSchemaField(@PathParam("schemaId") String schemaId) {
		logger.debug("");
		logger.debug("addSchemaField request received: " + schemaId);
		logger.debug("");
		Response response = Response.status(Response.Status.ACCEPTED).entity("Method not yet implemented").build();
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // addSchemaField

	/**
	 * Query the H2 database for the schema field with the requested ids.
	 *
	 * @param schemaId
	 * @param fieldId
	 * @return <field-descriptor>
	 */
	@GET
	@Path("/schemaField/{schemaId}/{fieldId}")
	@RolesAllowed({ ADMIN, USER })
	public Response getSchemaField(@PathParam("schemaId") String schemaId, @PathParam("fieldId") String fieldId) {
		logger.debug("");
		logger.debug("getSchemaField request received: " + schemaId + "   " + fieldId);
		logger.debug("");
		Response response = Response.status(Response.Status.ACCEPTED).entity("Method not yet implemented").build();
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // getSchemaField

	/**
	 * Delete the schema field with the requested ids from the H2 database.
	 *
	 * @param schemaId
	 * @param fieldId
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@DELETE
	@Path("/schemaField/{schemaId}/{fieldId}")
	@RolesAllowed({ ADMIN, USER })
	public Response deleteSchemaField(@PathParam("schemaId") String schemaId, @PathParam("fieldId") String fieldId) {
		logger.debug("");
		logger.debug("deleteSchemaField request received: " + schemaId + "   " + fieldId);
		logger.debug("");
		Response response = Response.status(Response.Status.ACCEPTED).entity("Method not yet implemented").build();
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // deleteSchemaField

	/**
	 * Query the H2 database for the schema field with the requested ids. Return
	 * metadata for the schema field
	 *
	 * @param schemaId
	 * @param fieldId
	 * @return <tbd>
	 */
	@GET
	@Path("/schemaFieldMetaData/{schemaId}/{fieldId}")
	@RolesAllowed({ ADMIN, USER })
	public Response getSchemaFieldMetaData(@PathParam("schemaId") String schemaId,
			@PathParam("fieldId") String fieldId) {
		logger.debug("");
		logger.debug("getSchemaFieldMetaData request received: " + schemaId + "   " + fieldId);
		logger.debug("");
		Response response = Response.status(Response.Status.ACCEPTED).entity("Method not yet implemented").build();
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // getSchemaFieldMetaData

	/**
	 * Save the sample data in the H2 database for the sample data with the
	 * requested id. The sample data name is sent in the payload of the request.
	 *
	 * @param sampleDataId
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@POST
	@Path("/sampleData/{sampleDataId}")
	@RolesAllowed({ ADMIN, USER })
	public Response saveSampleData(@PathParam("sampleDataId") String sampleDataId) {
		logger.debug("");
		logger.debug("saveSampleData request received: " + sampleDataId);
		logger.debug("");
		Response response = Response.status(Response.Status.ACCEPTED).entity("Method not yet implemented").build();
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // saveSampleData

	/**
	 * Query the H2 database for the sample data with the requested id.
	 *
	 * @param sampleDataId
	 * @return <data-sample>
	 *
	 *         Only available to Schema Wizard
	 */
	@GET
	@Path("/sampleData/{sampleDataId}")
	@RolesAllowed({ ADMIN, USER })
	public Response getSampleData(@PathParam("sampleDataId") String sampleDataId) {
		logger.debug("");
		logger.debug("getSampleData request received: " + sampleDataId);
		logger.debug("");
		Response response = dataService.getSampleByGuid(sampleDataId);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // getSampleData

	/**
	 * Delete the sample data with the requested id from the H2 database.
	 *
	 * @param sampleDataId
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@DELETE
	@Path("/sampleData/{sampleDataId}")
	@RolesAllowed({ ADMIN, USER })
	public Response deleteSampleData(@PathParam("sampleDataId") String sampleDataId) {
		logger.debug("");
		logger.debug("deleteSampleData request received: " + sampleDataId);
		logger.debug("");
		Response response = dataService.deleteSampleByGuid(sampleDataId);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // deleteSampleData

	/**
	 * Query the H2 database for the sample data with the requested id. Return
	 * metadata for the sample data
	 *
	 * @param sampleDataId
	 * @return <tbd>
	 *
	 *         Only available to Schema Wizard
	 */
	@GET
	@Path("/sampleDataMetaData/{sampleDataId}")
	@RolesAllowed({ ADMIN, USER })
	public Response getSampleDataMetaData(@PathParam("sampleDataId") String sampleDataId) {
		logger.debug("");
		logger.debug("getSampleDataMetaData request received: " + sampleDataId);
		logger.debug("");
		Response response = dataService.getSampleMetaDataByGuid(sampleDataId);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // getSampleDataMetaData

	/**
	 * Query the H2 database for the sample data field with the requested ids.
	 *
	 * @param sampleDataId
	 * @param fieldId
	 * @return <field-descriptor>
	 *
	 *         Only available to Schema Wizard
	 */
	@GET
	@Path("/sampleDataField/{sampleDataId}/{fieldId}")
	@RolesAllowed({ ADMIN, USER })
	public Response getSampleDataField(@PathParam("sampleDataId") String sampleDataId,
			@PathParam("fieldId") String fieldId) {
		logger.debug("");
		logger.debug("getSampleDataField request received: " + sampleDataId + "   " + fieldId);
		logger.debug("");
		Response response = Response.status(Response.Status.ACCEPTED).entity("Method not yet implemented").build();
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // getSampleDataField

	/**
	 * Delete the sample data field with the requested ids from the H2 database.
	 *
	 * @param sampleDataId
	 * @param fieldId
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@DELETE
	@Path("/sampleDataField/{sampleDataId}/{fieldId}")
	@RolesAllowed({ ADMIN, USER })
	public Response deleteSampleDataField(@PathParam("sampleDataId") String sampleDataId,
			@PathParam("fieldId") String fieldId) {
		logger.debug("");
		logger.debug("deleteSampleDataField request received: " + sampleDataId + "   " + fieldId);
		logger.debug("");
		Response response = Response.status(Response.Status.ACCEPTED).entity("Method not yet implemented").build();
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // deleteSampleDataField

	/**
	 * Query the H2 database for the sample data field with the requested ids.
	 * Return metadata for the sample data field
	 *
	 * @param sampleDataId
	 * @param fieldId
	 * @return <tbd>
	 *
	 *         Only available to Schema Wizard
	 */
	@GET
	@Path("/sampleDataFieldMetaData/{sampleDataId}/{fieldId}")
	@RolesAllowed({ ADMIN, USER })
	public Response getSampleDataMetaData(@PathParam("sampleDataId") String sampleDataId,
			@PathParam("fieldId") String fieldId) {
		logger.debug("");
		logger.debug("getSampleDataMetaData request received: " + sampleDataId + "   " + fieldId);
		logger.debug("");
		Response response = Response.status(Response.Status.ACCEPTED).entity("Method not yet implemented").build();
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // getSampleDataMetaData

	/**
	 * Upload one or more user modified data samples. Return a JSON Array of the
	 * proposed schema.
	 * 
	 * @param request
	 */
	@POST
	@Path("/uploadModifiedSamples")
	@RolesAllowed({ ADMIN, USER })
	public void uploadModifiedSamples(@Suspended final AsyncResponse asyncResponse,
			@Context final HttpServletRequest request, final String schemaAnalysisData) {
		new Thread(new Runnable() {
			public void run() {
				String sessionId = request.getSession().getId();
				String schemaGuid = request.getParameter("schemaGuid");
				String domain = request.getParameter("domain");
				logger.debug("");
				logger.debug("uploadModifiedSamples");
				logger.debug("schemaGuid: " + schemaGuid);
				logger.debug("domain:    " + domain);
				logger.debug("sessionId: " + sessionId);
				logger.debug("schemaAnalysisData");
				logger.debug("");
				logger.debug(schemaAnalysisData.toString());
				logger.debug("");
				JSONObject schemaAnalysisDataJson = new JSONObject(schemaAnalysisData);
				JSONObject jObject = new JSONObject();
				try {
					String sessionUploadDirectory = uploadDirectory + File.separator + sessionId;

					jObject = analyzerService.analyzeSchema(sessionUploadDirectory, schemaAnalysisDataJson, domain,
							sessionId);
					logger.debug("");
					asyncResponse.resume(Response.status(Response.Status.ACCEPTED).entity(jObject.toString()).build());
				} catch (AnalyticsCancelledWorkflowException e) {
					logger.info("Caught cancellation.  Not sending any response.");
					asyncResponse.cancel();
					return;
				}
				asyncResponse.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE)
						.entity(bundle.getString("analyzer.error")).build());
			}
		}).start();
	} // uploadModifiedSamples

	/**
	 * Upload one or more files for analysis. Return a JSON Array of the Data
	 * Sample Descriptors (JSON Object) for each file.
	 *
	 * @param request
	 */
	@POST
	@Path("/upload")
	@RolesAllowed({ ADMIN, USER })
	public void uploadSamples(@Suspended final AsyncResponse asyncResponse, @Context final HttpServletRequest request) {
		new Thread(new Runnable() {
			public void run() {
				logger.debug("Received upload request");
				String domain = request.getParameter("domain");
				String tolerance = request.getParameter("tolerance");
				String schemaGuid = request.getParameter("schemaGuid");
				String sessionId = request.getSession().getId();
				Integer numberOfFiles = Integer.valueOf(request.getParameter("numberOfFiles"));
				Long totalFilesSize = Long.valueOf(request.getParameter("filesTotalSize"));
				logger.debug("");
				logger.debug("sessionId: " + sessionId);
				logger.debug("domain:    " + domain);
				logger.debug("tolerance: " + tolerance);
				logger.debug("schemaGuid: " + schemaGuid);
				logger.debug("numberOfFiles: " + numberOfFiles);
				logger.debug("filesTotalSize: " + totalFilesSize);
				logger.debug("");
				JSONArray jArray = new JSONArray();

				if (ServletFileUpload.isMultipartContent(request)) {
					try {
						/*
						 * Create a directory for the session specific files
						 * that will be uploaded. Initialize the TikaAnalyzer to
						 * analyze the files in this directory.
						 */
						File sessionDir = new File(uploadDirectory + File.separator + sessionId);
						if (!sessionDir.exists() && !sessionDir.mkdirs()) {
							throw new IOException("Session directory could not be created.");
						}
						jArray = analyzerService.analyzeSamples(schemaGuid, sessionDir.getAbsolutePath(), domain,
								tolerance, sessionId, request, numberOfFiles, totalFilesSize);
						logger.debug("");
						logger.debug("jArray.length(): " + jArray.length());
						logger.debug("");
						logger.debug("jArray: " + jArray.toString());
						logger.debug("");
						logger.debug("File uploaded successfully");
					} catch (AnalyticsCancelledWorkflowException e) {
						logger.info("Caught cancellation.  Not sending any response.");
						asyncResponse.cancel();
						return;
					} catch (ProcessingException e) {
						logger.error(e.getMessage());
						asyncResponse.resume(Response.status(Response.Status.GATEWAY_TIMEOUT)
								.entity(bundle.getString("ie.server.timeout")).build());
					} catch (Exception ex) {
						logger.debug("File Upload Failed due to " + ex);
						logger.error(ex);
						asyncResponse.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE)
								.entity(bundle.getString("upload.failed")).build());
					}
				} else {
					request.setAttribute("message", "This Servlet only handles file upload requests");
					asyncResponse.resume(Response.status(Response.Status.METHOD_NOT_ALLOWED)
							.entity(bundle.getString("upload.failed")).build());
				}
				logger.info("Sample analysis completed for session " + sessionId + ".");
				if (!asyncResponse.isCancelled()) {
					asyncResponse.resume(Response.status(Response.Status.ACCEPTED).entity(jArray.toString()).build());
				}
			}
		}).start();
	} // uploadSamples

	/**
	 * Create a new domain object
	 *
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@POST
	@Path("/domain")
	@RolesAllowed({ ADMIN, USER })
	public Response createDomain(@Context HttpServletRequest request) {
		Response response;
		logger.debug("");
		logger.debug("createDomain request received");
		JSONObject jObject = new JSONObject(request.getParameter("data"));
		logger.debug("");
		logger.debug(jObject.toString());
		response = dataService.createDomain(jObject);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // createDomain

	/**
	 * Upload an updated domain object
	 *
	 * @param domainId
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@POST
	@Path("/domain/{domainId}")
	@RolesAllowed({ ADMIN, USER })
	public void updateDomain(@PathParam("domainId") String domainId) {
		logger.debug("");
		logger.debug("updateDomain request received");
		logger.debug("");
		logger.debug("domainId: " + domainId);
		logger.debug("");
	} // updateDomain

	/**
	 * Delete a domain object
	 *
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@DELETE
	@Path("/domain")
	@RolesAllowed({ ADMIN, USER })
	public Response deleteDomain(@Context HttpServletRequest request) {
		String returnValue = null;
		logger.debug("");
		logger.debug("deleteDomain request received");
		JSONObject jObject = new JSONObject(request.getParameter("data"));
		logger.debug(jObject.toString());
		logger.debug("");
		Response response = dataService.deleteDomain(jObject);
		returnValue = response.getEntity().toString();
		logger.debug("");
		logger.debug("Response code: " + response.getStatus());
		logger.debug("returnValue: " + returnValue);
		logger.debug("");
		return response;
	} // deleteDomain

	/**
	 * Query the Interpretation Engine for interpretations of a given domain.
	 * Return a JSON Object of the list of interpretations.
	 *
	 * @param domainId
	 * @return a map of <interpretation-descriptor> objects
	 */
	@GET
	@Path("/{domainId}/interpretation")
	@RolesAllowed({ ADMIN, USER })
	public Response getInterpretations(@PathParam("domainId") String domainId) {
		Response response;
		logger.debug("");
		logger.debug("getInterpretation request received");
		logger.debug("");
		logger.debug("domainId: " + domainId);
		logger.debug("");
		response = dataService.getDomainInterpretations(domainId);
		JSONObject json = new JSONObject(response.getEntity().toString());
		logger.debug("");
		logger.debug("Num Interpretations: " + json.length());
		logger.debug("");
		logger.debug(response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // getInterpretations

	/**
	 * Upload an updated interpretation object
	 *
	 * @param domainId
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@PUT
	@Path("/{domainId}/interpretation")
	@RolesAllowed({ ADMIN, USER })
	public Response updateInterpretation(@PathParam("domainId") String domainId, @Context HttpServletRequest request) {
		Response response;
		logger.debug("");
		logger.debug("updateInterpretation request received");
		logger.debug("");
		logger.debug("domainId: " + domainId);
		JSONObject jObject = new JSONObject(request.getParameter("data"));
		logger.debug(jObject.toString());
		logger.debug("");
		response = dataService.updateInterpretation(jObject);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // updateInterpretation

	/**
	 * Create an updated interpretation object
	 *
	 * @param domainId
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@POST
	@Path("/{domainId}/interpretation")
	@RolesAllowed({ ADMIN, USER })
	public Response createInterpretation(@PathParam("domainId") String domainId, @Context HttpServletRequest request) {
		Response response;
		logger.debug("");
		logger.debug("createInterpretation request received");
		logger.debug("");
		logger.debug("domainId: " + domainId);
		JSONObject jObject = new JSONObject(request.getParameter("data"));
		logger.debug(jObject.toString());
		logger.debug("");
		response = dataService.createInterpretation(jObject);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // createInterpretation

	/**
	 * Delete an interpretation object
	 *
	 * @param domainId
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@DELETE
	@Path("/{domainId}/interpretation/{interpretationId}")
	@RolesAllowed({ ADMIN, USER })
	public Response deleteInterpretation(@PathParam("domainId") String domainId,
			@PathParam("interpretationId") String interpretationId) {
		Response response;
		logger.debug("");
		logger.debug("deleteInterpretation request received");
		logger.debug("");
		logger.debug("domainId: " + domainId);
		logger.debug("");
		logger.debug("interpretationId: " + interpretationId);
		logger.debug("");
		JSONObject jObject = new JSONObject();
		jObject.put("iId", interpretationId);
		logger.debug(jObject.toString());
		logger.debug("");
		response = dataService.deleteInterpretation(jObject);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.getEntity().toString());
		logger.debug("");
		return response;
	} // deleteInterpretation

	/**
	 * Test a python script
	 *
	 * @param interpretation
	 *            object id
	 * @return true if the script is valid
	 *
	 *         Only available to Schema Wizard
	 */
	@GET
	@Path("/python/validate/{interpretationId}")
	@RolesAllowed({ ADMIN, USER })
	public Response validatePythonScript(@PathParam("interpretationId") String interpretationId) {
		Response response;
		logger.debug("");
		logger.debug("validatePythonScript request received");
		logger.debug("");
		logger.debug("interpretationId: " + interpretationId);
		response = dataService.validatePythonScript(interpretationId);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		JSONObject entity = new JSONObject(response.getEntity().toString());
		JSONArray annotations = new JSONArray(entity.get("returnValue").toString());
		JSONObject jObject = new JSONObject();
		jObject.put("annotations", annotations);
		logger.debug("annotations: " + jObject.toString());
		response = Response.status(response.getStatus()).entity(jObject.toString()).build();
		return response;
	} // validatePythonScript

	/**
	 * Test a python script
	 *
	 * @param interpretation
	 *            object id
	 * @return console output of test execution
	 *
	 *         Only available to Schema Wizard
	 */
	@GET
	@Path("/python/test/{interpretationId}")
	@RolesAllowed({ ADMIN, USER })
	public Response testPythonScript(@PathParam("interpretationId") String interpretationId) {
		Response response;
		logger.debug("");
		logger.debug("testPythonScript request received");
		logger.debug("");
		logger.debug("interpretationId: " + interpretationId);
		response = dataService.testPythonScript(interpretationId);
		logger.debug("");
		logger.debug("Status code: " + response.getStatus());
		logger.debug(response.toString());
		logger.debug("entity");
		logger.debug(response.getEntity().toString());
		JSONObject entity = new JSONObject(response.getEntity().toString());
		logger.debug("entity");
		logger.debug(entity);
		JSONObject consoleOutput = new JSONObject(entity.get("returnValue").toString());
		logger.debug("consoleOutput");
		logger.debug(consoleOutput);
		JSONObject jObject = new JSONObject();
		jObject.put("consoleOutput", consoleOutput);
		logger.debug("");
		logger.debug("consoleOutput: " + jObject.toString());
		response = Response.status(response.getStatus()).entity(jObject.toString()).build();
		return response;
	} // testPythonScript

	/**
	 * Test this REST interface
	 *
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@Override
	@GET
	@Path("/test")
	@RolesAllowed({ ADMIN, USER })
	public String test() {
		logger.debug("");
		logger.debug("Testing the Schema Wizard REST endpoint connection.");
		logger.debug("");
		JSONObject jObject = new JSONObject();
		logger.debug("");
		logger.debug(jObject.toString());
		logger.debug("");
		jObject.put("SchemaWizard", "Up");
		jObject.put("Secure", false);
		return jObject.toString();
	} // test

	/**
	 * Login for Shiro
	 *
	 * @param request
	 */
	@POST
	@Path("/login")
	public Response login(@Context HttpServletRequest request, String loginCredentials) {
		Response response = null;
		Subject currentUser = SecurityUtils.getSubject();
		@SuppressWarnings("unused")
		Session session = currentUser.getSession();

		JSONObject loginCredentialsJson = new JSONObject(loginCredentials);

		String username = loginCredentialsJson.getString("username");
		String password = loginCredentialsJson.getString("password");

		if (!currentUser.isAuthenticated()) {
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			token.setRememberMe(false);

			try {
				currentUser.login(token);
				response = dataService.getUser(username);
				logger.debug("Logging in " + currentUser.getPrincipal() + " successful of Session ID: "
						+ request.getSession().getId());
			} catch (UnknownAccountException uae) {
				response = Response.status(401).entity("Login unsuccessful.").build();
				logger.debug("There is no user with username of " + token.getPrincipal());
			} catch (IncorrectCredentialsException ice) {
				response = Response.status(401).entity("Login unsuccessful.").build();
				logger.debug("Password for account " + token.getPrincipal() + " was incorrect.");
			} catch (LockedAccountException lae) {
				response = Response.status(401).entity("Login unsuccessful.").build();
				logger.debug("The account for username " + token.getPrincipal() + " is locked.  "
						+ "Please contact your administrator to unlock it.");
			} catch (AuthenticationException ae) {
				response = Response.status(401).entity("Login unsuccessful.").build();
				logger.debug("Error authenticating user.");
			} catch (Exception e) {
				response = Response.status(401).entity("Login unsuccessful.").build();
				logger.debug("An unknown error has occured trying to login with the given credentials.");
			}
		} else {
			response = Response.status(200).entity("User " + currentUserString(currentUser) + " is already logged in.")
					.build();
			logger.debug("User " + currentUserString(currentUser) + " is already logged in.");
		}

		return response;
	}

	@GET
	@Path("/whoami")
	@RolesAllowed({ ADMIN })
	public String whoami() {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			@SuppressWarnings("unused")
			Session session = currentUser.getSession();
			String currentUserString = currentUserString(currentUser);
			logger.debug(currentUserString + " is logged in.");
			return currentUserString;

		} catch (UnknownSessionException e) {
			logger.error("The session has expired.");
			return "The session has expired.";
		}
	}

	@POST
	@Path("/logout")
	public Response logout() {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			@SuppressWarnings("unused")
			Session session = currentUser.getSession();

			currentUser.logout();
			response = Response.status(200).entity("Logout successful.").build();
			logger.debug("User sucessfully logged out.");
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

		return response;
	}

	@POST
	@Path("/createUser")
	@RolesAllowed({ ADMIN })
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUser(@Context HttpServletRequest request, String userJson) {
		Response response;
		logger.debug("Create user request received.");
		try {
			User user = SerializationUtility.deserialize(userJson, User.class);

			// set the role to all lowercase to better match the Roles enum
			user.setUserRole(user.getUserRole().toLowerCase());
			user.setUserName(user.getUserName().toLowerCase());
			user.setFirstName(user.getFirstName().toLowerCase());

			String salt = new BigInteger(250, new SecureRandom()).toString(32);
			Sha256Hash hashedPassword = new Sha256Hash(user.getPassword(), (new SimpleByteSource(salt)).getBytes());
			user.setPassword(hashedPassword.toHex());
			user.setSalt(salt);

			response = dataService.createUser(user);
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

		return response;
	}

	@GET
	@Path("/users")
	@RolesAllowed({ ADMIN })
	public Response getAllUsers() {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			@SuppressWarnings("unused")
			Session session = currentUser.getSession();

			response = dataService.getAllUsers();
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

		return response;
	}

	@POST
	@Path("/updateUser")
	@RolesAllowed({ ADMIN, USER })
	public Response updateUser(@Context HttpServletRequest request, String userJson) {
		Response response = null;
		User user = SerializationUtility.deserialize(userJson, User.class);
		user.setUserName(user.getUserName().toLowerCase());
		
		try {
			Subject currentUser = SecurityUtils.getSubject();
			@SuppressWarnings("unused")
			Session session = currentUser.getSession();

			JSONObject requestingUser = dataService.getUserJson(currentUserString(currentUser));
			logger.debug("Shiro identified the current user as: " + currentUserString(currentUser));
			logger.debug("The current user has the role: " + requestingUser.getString("userRole"));
			logger.debug("The client is requesting to modify user: " + requestingUser.toString());

			if (requestingUser.getString("userRole").equals(ADMIN)) {
				if (user.getPassword() != null) {
					String salt = new BigInteger(250, new SecureRandom()).toString(32);
					Sha256Hash hashedPassword = new Sha256Hash(user.getPassword(),
							(new SimpleByteSource(salt)).getBytes());
					user.setPassword(hashedPassword.toHex());
					user.setSalt(salt);
				}

				response = dataService.updateUser(user);
			} else if (requestingUser.getString("userRole").equals(USER)
					&& currentUserString(currentUser).equals(user.getUserName())) {
				// the USER role is only allowed to modify their own
				// password - not the password of others
				if (user.getPassword() != null) {
					String salt = new BigInteger(250, new SecureRandom()).toString(32);
					Sha256Hash hashedPassword = new Sha256Hash(user.getPassword(),
							(new SimpleByteSource(salt)).getBytes());
					user.setPassword(hashedPassword.toHex());
					user.setSalt(salt);
				}

				response = dataService.updateUser(user);
			} else if (requestingUser.getString("userRole").equals(USER)
					&& !currentUserString(currentUser).equals(user.getUserName())) {
				return Response.status(401)
						.entity("You are not allowed to perform this function. You may only modify your own password as a "
								+ requestingUser.getString("userRole"))
						.build();
			} else if (requestingUser.getString("userRole").equals(NOT_FOUND)) {
				return Response.status(503).entity("Could not find current user's role.").build();
			} else {
				return Response.status(503).entity("Unable to match current user's role to a defined role.").build();
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

		return response;
	}

	@DELETE
	@Path("/deleteUser")
	@RolesAllowed({ ADMIN })
	public Response deleteUser(@Context HttpServletRequest request, String username) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			@SuppressWarnings("unused")
			Session session = currentUser.getSession();

			response = dataService.deleteUser(username.toLowerCase());

		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

		return response;
	}

	@POST
	@Path("/modify/user/questions")
	@RolesAllowed({ ADMIN, USER })
	public Response addSecurityQuestionsForUser(@Context HttpServletRequest request, String sqJson) {
		Response response;
		logger.debug("Add security questions for user request received.");
		try {
			JSONObject securityQuestions = new JSONObject(sqJson);
			JSONObject questions = securityQuestions.getJSONObject("questions");
			JSONObject answers = securityQuestions.getJSONObject("answers");
			String username = securityQuestions.getString("userName").toLowerCase();

			User user = new User();
			user.setUserName(username);
			user.setSecurityQuestion1(questions.getString("securityQuestion1"));
			user.setSecurityQuestion2(questions.getString("securityQuestion2"));
			user.setSecurityQuestion3(questions.getString("securityQuestion3"));

			user.setSecurityQuestion1Answer(answers.getString("securityQuestion1Answer"));
			user.setSecurityQuestion2Answer(answers.getString("securityQuestion2Answer"));
			user.setSecurityQuestion3Answer(answers.getString("securityQuestion3Answer"));

			Subject currentUser = SecurityUtils.getSubject();
			@SuppressWarnings("unused")
			Session session = currentUser.getSession();

			JSONObject requestingUser = dataService.getUserJson(currentUserString(currentUser));
			logger.debug("Shiro identified the current user as: " + currentUserString(currentUser));
			logger.debug("The current user has the role: " + requestingUser.getString("userRole"));
			logger.debug("The client is requesting to modify user: " + requestingUser.toString());

			if (currentUserString(currentUser).equals(user.getUserName())) {
				response = dataService.addSecurityQuestionsForUser(user);
			} else if (requestingUser.getString("userRole").equals(NOT_FOUND)) {
				return Response.status(503).entity("Could not find current user's role.").build();
			} else {
				return Response.status(503).entity("Unable to match current user's role to a defined role.").build();
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

		return response;
	}

	@POST
	@Path("/recover/username")
	public Response getUsernameFromFirstName(@Context HttpServletRequest request, String receivedObject) {
		JSONObject receivedJObject = new JSONObject(receivedObject);

		logger.debug("Request Object: " + receivedJObject.toString());
		logger.debug("");

		return dataService.getUsernameFromFirstName(receivedJObject.getString("firstName").toLowerCase());
	}

	@POST
	@Path("/security/questions")
	public Response getAllSecurityQuestions(@Context HttpServletRequest request) {
		logger.debug("Received request to retrieve all security questions.");
		return dataService.getAllSecurityQuestions();
	}

	@POST
	@Path("/recover/questions")
	public Response getSecurityQuestionsForUser(@Context HttpServletRequest request, String receivedObject) {
		JSONObject receivedJObject = new JSONObject(receivedObject);

		logger.debug("Request Object: " + receivedJObject.toString());
		logger.debug("");

		return dataService.getSecurityQuestionsForUser(receivedJObject.getString("userName").toLowerCase());
	}

	@POST
	@Path("/submit/questions")
	public Response submitSecurityQuestions(@Context HttpServletRequest request, String sqJson) {

		logger.debug("Request Object: " + sqJson.toString());
		logger.debug("");

		JSONObject securityQuestions = new JSONObject(sqJson);
		JSONObject questions = securityQuestions.getJSONObject("questions");
		JSONObject answers = securityQuestions.getJSONObject("answers");
		String username = securityQuestions.getString("userName");

		User user = new User();
		user.setUserName(username.toLowerCase());
		user.setSecurityQuestion1(questions.getString("securityQuestion1"));
		user.setSecurityQuestion2(questions.getString("securityQuestion2"));
		user.setSecurityQuestion3(questions.getString("securityQuestion3"));

		user.setSecurityQuestion1Answer(answers.getString("securityQuestion1Answer"));
		user.setSecurityQuestion2Answer(answers.getString("securityQuestion2Answer"));
		user.setSecurityQuestion3Answer(answers.getString("securityQuestion3Answer"));

		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replace("-", "");
		uuid = uuid.substring(0, 12);

		// system generated password that is only used if the question/answer
		// combo matches
		String salt = new BigInteger(250, new SecureRandom()).toString(32);
		Sha256Hash hashedPassword = new Sha256Hash(uuid, (new SimpleByteSource(salt)).getBytes());
		user.setPassword(hashedPassword.toHex());
		user.setSalt(salt);

		return dataService.verifySecurityQuestionsForUser(user, uuid);
	}

	@POST
	@Path("/keepAlive")
	public Response keepAlive(@Context HttpServletRequest request) {
		return Response.status(200).entity("Keep alive received.").build();
	}

	@GET
	@Path("/health")
	public String health(@Context SecurityContext sc) {
		if (sc.isUserInRole("admin")) {
			return dataService.healthCheck(true);
		} else {
			return dataService.healthCheck();
		}
	}

	/**
	 * Upload a Schema Wizard Export and print the contents to the console. This
	 * is a testing method.
	 *
	 * @param request
	 */
	@POST
	@Path("/uploadExport")
	@RolesAllowed({ ADMIN })
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response uploadExport(@Context final HttpServletRequest request) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				logger.debug(sCurrentLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private String currentUserString(Subject currentUser) {
		return Optional.of(currentUser.getPrincipal()).orElse("Unknown").toString();
	}

	private User defaultUser() {
		User user = new User();
		user.setUserName(ADMIN_USER_NAME);
		user.setPassword(ADMIN_PASSWORD);
		user.setFirstName("Default");
		user.setLastName("System Generated");
		user.setUserRole(ADMIN);

		String salt = new BigInteger(250, new SecureRandom()).toString(32);
		Sha256Hash hashedPassword = new Sha256Hash(user.getPassword(), (new SimpleByteSource(salt)).getBytes());
		user.setPassword(hashedPassword.toHex());
		user.setSalt(salt);

		return user;
	}

	@RolesAllowed({ ADMIN, USER })
	@Override
	@POST
	@Path("/export")
	public Response export(@Context HttpServletRequest request, String parameterMapping) {
		try {
			Map<String, Object> parameters = SerializationUtility.deserialize(parameterMapping,
					new TypeReference<Map<String, Object>>() {
					});
			return dataService.exportSchema(parameters);
		} catch (Exception e) {
			return Response.serverError().build();
		}
	}
} // SchemaWizardController
