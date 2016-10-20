package com.deleidos.sw.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.json.JSONObject;

import com.deleidos.dmf.accessor.ServiceLayerAccessor;

public class SchemaWizardServlet extends DefaultServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(SchemaWizardServlet.class);
	ServiceLayerAccessor dataService;

	public SchemaWizardServlet() {
		super();
		logger.info("Initializing custom Jetty servlet.");
		dataService = new ServiceLayerAccessor();
	}

	/**
	 * Custom handling of the GET request.
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (super.getServletContext().getContextPath().equals("/schwiz")) {
			logger.debug("Performing health check of Schema Wizard backend services.");

			JSONObject topLevelObject = new JSONObject(dataService.healthCheck());
			JSONObject serverStatuses = topLevelObject.getJSONObject("Server Statuses");

			logger.debug("Server status: " + topLevelObject.toString());

			if (serverStatuses.getString("H2").equals("Up") && serverStatuses.getString("MongoDB").equals("Up")
					&& serverStatuses.getString("Interpretation Engine").equals("Up")) {
				super.doGet(request, response);
			} else {
				// Go to error.html
				logger.error("A core Schema Wizard service is unavailable.");
				logger.error("Redirecting to the error page.");
				response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/error.html"));
			}
		}
	}
}
