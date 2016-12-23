package com.deleidos.sw.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;

public class UnauthorizedExceptionMapper implements ExceptionMapper<UnauthorizedException> {
	private static final Logger logger = Logger.getLogger(UnauthorizedExceptionMapper.class);

	@Override
	public Response toResponse(UnauthorizedException exception) {
		logger.error(exception);
		return Response.status(401).entity("You are not authorized to view this page.").build();
	}

}
