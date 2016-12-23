package com.deleidos.dp.exceptions;

public class H2DataAccessException extends DataAccessException {

	public H2DataAccessException(String message) {
		super(message);
	}

	public H2DataAccessException(String string, Exception e) {
		super(string, e);
	}

}
