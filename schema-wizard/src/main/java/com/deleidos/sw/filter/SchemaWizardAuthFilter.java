package com.deleidos.sw.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;

/**
 * Basic implementation of a javax-ws class.
 *
 */
public class SchemaWizardAuthFilter implements ContainerRequestFilter {
	/**
	 * Intercepts and reads/modifies the incoming request before it is
	 * dispatched.
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws UnauthorizedException {
		Subject currentSubject = SecurityUtils.getSubject();
		
		if (currentSubject.isAuthenticated()) { } // future implementation

		requestContext.setSecurityContext(new SchemaWizardSecurityContext(currentSubject));
	}
}