package com.deleidos.sw;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.json.JSONObject;

import com.deleidos.dmf.accessor.ServiceLayerAccessor;
import com.deleidos.dmf.analyzer.TikaAnalyzer;
import com.deleidos.sw.enums.Roles;

/**
 * The secure Schema Wizard Controller. This class performs all authorization
 * and calls on the unsecured schema wizard controller if users are authorized.
 *
 */
public class SecureSchemaWizardController extends SchemaWizardController {

	private static Logger logger = Logger.getLogger(SecureSchemaWizardController.class);

	/**
	 * Constructor
	 *
	 * @param service
	 */
	public SecureSchemaWizardController(TikaAnalyzer analyzerService, ServiceLayerAccessor dataService,
			String uploadDirectory) {
		super(analyzerService, dataService, uploadDirectory);

		// Set up shiro security manager
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);
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
	public Response getSessionId(@Context HttpServletRequest request) {
		return super.getSessionId(request);
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
	public Response getCatalog() {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getCatalog();
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response saveSchema(String schema) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.saveSchema(schema);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response getSchema(@PathParam("schemaId") String schemaId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getSchema(schemaId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response getSchema(@PathParam("schemaId") String schemaId, @PathParam("nohistogram") String nohistogram) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getSchema(schemaId, nohistogram);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response deleteSchema(@PathParam("schemaId") String schemaId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.deleteSchema(schemaId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response getSchemaMetaData(@PathParam("schemaId") String schemaId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getSchemaMetaData(schemaId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response addSchemaField(@PathParam("schemaId") String schemaId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.addSchemaField(schemaId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response getSchemaField(@PathParam("schemaId") String schemaId, @PathParam("fieldId") String fieldId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getSchemaField(schemaId, fieldId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response deleteSchemaField(@PathParam("schemaId") String schemaId, @PathParam("fieldId") String fieldId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.deleteSchemaField(schemaId, fieldId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response getSchemaFieldMetaData(@PathParam("schemaId") String schemaId,
			@PathParam("fieldId") String fieldId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getSchemaFieldMetaData(schemaId, fieldId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response saveSampleData(@PathParam("sampleDataId") String sampleDataId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.saveSampleData(sampleDataId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response getSampleData(@PathParam("sampleDataId") String sampleDataId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getSampleData(sampleDataId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response deleteSampleData(@PathParam("sampleDataId") String sampleDataId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.deleteSampleData(sampleDataId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response getSampleDataMetaData(@PathParam("sampleDataId") String sampleDataId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getSampleDataMetaData(sampleDataId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response getSampleDataField(@PathParam("sampleDataId") String sampleDataId,
			@PathParam("fieldId") String fieldId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getSampleDataField(sampleDataId, fieldId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response deleteSampleDataField(@PathParam("sampleDataId") String sampleDataId,
			@PathParam("fieldId") String fieldId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.deleteSampleDataField(sampleDataId, fieldId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response getSampleDataMetaData(@PathParam("sampleDataId") String sampleDataId,
			@PathParam("fieldId") String fieldId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getSampleDataMetaData(sampleDataId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public void uploadModifiedSamples(@Suspended final AsyncResponse asyncResponse,
			@Context final HttpServletRequest request, final String schemaAnalysisData) {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				super.uploadModifiedSamples(asyncResponse, request, schemaAnalysisData);
			} else {
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			logger.error("The session has expired.");
		}
	} // uploadModifiedSamples

	/**
	 * Upload one or more files for analysis. Return a JSON Array of the Data
	 * Sample Descriptors (JSON Object) for each file.
	 *
	 * @param request
	 */
	@POST
	@Path("/upload")
	public void uploadSamples(@Suspended final AsyncResponse asyncResponse, @Context final HttpServletRequest request) {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				super.uploadSamples(asyncResponse, request);
			} else {
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			logger.error("The session has expired.");
		}
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
	public Response createDomain(@Context HttpServletRequest request) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getCatalog();
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
		}

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
	public void updateDomain(@PathParam("domainId") String domainId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				super.updateDomain(domainId);
			} else {
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			logger.error("The session has expired.");
		}
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
	public Response deleteDomain(@Context HttpServletRequest request) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.deleteDomain(request);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response getInterpretations(@PathParam("domainId") String domainId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.getInterpretations(domainId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response updateInterpretation(@PathParam("domainId") String domainId, @Context HttpServletRequest request) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.updateInterpretation(domainId, request);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response createInterpretation(@PathParam("domainId") String domainId, @Context HttpServletRequest request) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.createInterpretation(domainId, request);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response deleteInterpretation(@PathParam("domainId") String domainId,
			@PathParam("interpretationId") String interpretationId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.deleteInterpretation(domainId, interpretationId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response validatePythonScript(@PathParam("interpretationId") String interpretationId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.validatePythonScript(interpretationId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

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
	public Response testPythonScript(@PathParam("interpretationId") String interpretationId) {
		Response response;

		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			if (checkPermission(currentUser, Roles.admin)) {
				response = super.testPythonScript(interpretationId);
			} else {
				response = Response.status(401).entity("You are not authorized to view this page.").build();
				logger.warn("User " + getUserString(currentUser) + " is not authorized to access this function.");
			}
		} catch (UnknownSessionException e) {
			response = Response.status(403).entity("The session has expired.").build();
			logger.error("The session has expired.");
		}

		return response;
	} // testPythonScript

	/**
	 * Test this REST interface
	 *
	 * @return
	 *
	 * 		Only available to Schema Wizard
	 */
	@GET
	@Path("/test")
	public String test() {
		logger.debug("");
		logger.debug("Testing the Schema Wizard REST endpoint connection.");
		logger.debug("");
		JSONObject jObject = new JSONObject();
		logger.debug("");
		logger.debug(jObject.toString());
		logger.debug("");
		jObject.put("SchemaWizard", "Up");
		jObject.put("Secure", true);
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
		Session session = currentUser.getSession();
		// session.setAttribute("Steve", "Aoki");

		JSONObject loginCredentialsJson = new JSONObject(loginCredentials);

		String username = loginCredentialsJson.getString("username");
		String password = loginCredentialsJson.getString("password");

		if (!currentUser.isAuthenticated()) {
			// UsernamePasswordToken token = new UsernamePasswordToken("admin",
			// "12345");
			UsernamePasswordToken token = new UsernamePasswordToken(username, password);
			token.setRememberMe(false);

			try {
				currentUser.login(token);
				response = Response.status(200).entity("Login successful.").build();
				logger.debug("Logging in " + currentUser.getPrincipal() + " successful of Session ID: "
						+ request.getSession().getId());
			} catch (UnknownAccountException uae) {
				response = Response.status(401).entity("Login unsuccessful.").build();
				logger.error("There is no user with username of " + token.getPrincipal());
			} catch (IncorrectCredentialsException ice) {
				response = Response.status(401).entity("Login unsuccessful.").build();
				logger.error("Password for account " + token.getPrincipal() + " was incorrect!");
			} catch (LockedAccountException lae) {
				response = Response.status(401).entity("Login unsuccessful.").build();
				logger.error("The account for username " + token.getPrincipal() + " is locked.  "
						+ "Please contact your administrator to unlock it.");
			} catch (AuthenticationException ae) {
				response = Response.status(401).entity("Login unsuccessful.").build();
				logger.error("Error authenticating user.");
			} catch (Exception e) {
				response = Response.status(401).entity("Login unsuccessful.").build();
				logger.error("An unknown error has occured trying to login with the given credentials.");
			}
		} else {
			response = Response.status(200).build();
		}

		return response;
	}

	@GET
	@Path("/whoami")
	public String whoami() {
		try {
			Subject currentUser = SecurityUtils.getSubject();
			Session session = currentUser.getSession();

			logger.debug(getUserString(currentUser) + " is logged in.");
			return getUserString(currentUser);

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

	// Private methods
	/**
	 * This method checks the Shiro's current Subject against a Role.
	 * 
	 * @param currentUser
	 *            Current Shiro Subject
	 * @param requiredRole
	 *            Enumeration of the required role
	 * @return True if the user possess the required role
	 */
	private boolean checkPermission(Subject currentUser, Roles requiredRole) {
		if (currentUser.hasRole(requiredRole.toString())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the user String. This method is preferable to
	 * Subject.getPrincipal().toString() because there is no possibility of a
	 * null pointer.
	 * 
	 * @param user
	 *            Shiro Subject
	 * @return A String representation of the user, 'Unknown' if user is null.
	 */
	private String getUserString(Subject currentUser) {
		if (currentUser.getPrincipal() != null) {
			return currentUser.getPrincipal().toString();
		} else {
			return "Unknown";
		}
	}
}
