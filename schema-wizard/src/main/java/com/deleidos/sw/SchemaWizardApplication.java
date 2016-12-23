package com.deleidos.sw;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import com.deleidos.analytics.websocket.WebSocketServlet;
import com.deleidos.dmf.accessor.ServiceLayerAccessor;
import com.deleidos.dmf.analyzer.TikaAnalyzer;
import com.deleidos.dmf.web.SchemaWizardSessionUtility;
import com.deleidos.sw.exception.mapper.UnauthorizedExceptionMapper;
import com.deleidos.sw.exception.mapper.UnknownSessionExceptionMapper;
import com.deleidos.sw.filter.SchemaWizardAuthFilter;

/**
 * Implementation of Glassfish's default web application class. Instantiates
 * SchemaWizardController and WebSocketServlet.
 * 
 * @author leegc
 * 
 * @see <a href="https://jersey.java.net/documentation/latest/deployment.html">
 *      https://jersey.java.net/documentation/latest/deployment.html</a>
 */
public class SchemaWizardApplication extends ResourceConfig {
	private static Logger logger = Logger.getLogger(SchemaWizardApplication.class);
	private final static String BUILD_PROPERTIES_FILE_NAME = "build.properties";
	private final static String UPLOAD_DIR = "sw.upload.directory";
	private final static String USE_SECURE = "sw.use.secure";
	private static Map<String, Object> configurationMap;

	public SchemaWizardApplication() {
		// These methods run first
		loadConfiguration();
		logger.info(logConfigurationReport());

		String uploadDirectory = configurationMap.get(UPLOAD_DIR).toString();
		boolean isSecure = (Boolean) configurationMap.get(USE_SECURE);
		
		if (isSecure) {
			// register the authorization filter if deployment is secure
			register(new SchemaWizardAuthFilter());
			register(RolesAllowedDynamicFeature.class);
		} 
		
		// register handlers for Unauthorized or UnknownSession Exceptions thrown by the controller
		register(new UnauthorizedExceptionMapper());
		register(new UnknownSessionExceptionMapper());
		
		// register the SchemaWizardController
		register(new SchemaWizardController(new TikaAnalyzer(), new ServiceLayerAccessor(),
				SchemaWizardSessionUtility.getInstance(), uploadDirectory));
		
		// register the WebSocketServlet
		register(new WebSocketServlet());

	}

	private void loadConfiguration() {
		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(BUILD_PROPERTIES_FILE_NAME);
		Properties prop = new Properties();
		configurationMap = new HashMap<String, Object>();

		try {
			if (inputStream != null) {
				prop.load(inputStream);
				// Add additional configuration options here
				configurationMap.put(UPLOAD_DIR, prop.getProperty(UPLOAD_DIR));
				configurationMap.put(USE_SECURE, Boolean.valueOf(prop.getProperty(USE_SECURE)));
				inputStream.close();
			} else {
				throw new FileNotFoundException(
						"Property file '" + BUILD_PROPERTIES_FILE_NAME + "' not found in the classpath.");
			}
		} catch (IOException e) {
			logger.error("Exception: " + e);
		} catch (Exception e) {
			logger.error("Exception: " + e);
		}
	}

	private String logConfigurationReport() {
		StringBuilder sb = new StringBuilder();

		sb.append("Schema Wizard Configuration:");
		configurationMap.forEach((k, v) -> sb.append("\n\t" + k + "=" + v));

		return sb.toString();
	}
}
