package com.deleidos.dp.exceptions;

/**
 * Exception for errors that are caused by a problem accessing Schema Wizard data components (H2 and Interpretation Engine).
 * @author leegc
 *
 */
public class DataAccessException extends Exception {
	
	public DataAccessException(String message, Throwable throwable) {
		super(message, throwable);
	}
	
	public DataAccessException(String message) {
		super(message);
	}
	
}
