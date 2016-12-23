package com.deleidos.sw.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.log4j.Logger;
import org.apache.shiro.session.UnknownSessionException;

public class UnknownSessionExceptionMapper implements ExceptionMapper<UnknownSessionException> {
	private static final Logger logger = Logger.getLogger(UnknownSessionException.class);
	
	@Override
	public Response toResponse(UnknownSessionException exception) {
		logger.error(exception);
		return Response.status(403).entity("Session has expired.").build();
	}

}
