package com.deleidos.sw.filter;

import java.security.Principal;
import java.util.Optional;

import javax.ws.rs.core.SecurityContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

public class SchemaWizardSecurityContext implements SecurityContext {
	private final Subject user;

	public SchemaWizardSecurityContext(Subject user) {
		this.user = user;
	}
	
	@Override
	public Principal getUserPrincipal() {
		return ()->Optional.of(user.getPrincipal()).orElse("Unknown").toString();
	}

	@Override
	public boolean isUserInRole(String role) {
		return user.hasRole(role);
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public String getAuthenticationScheme() {
		return SecurityContext.BASIC_AUTH;
	}

}
