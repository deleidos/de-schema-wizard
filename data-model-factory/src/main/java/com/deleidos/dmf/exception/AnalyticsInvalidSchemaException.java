package com.deleidos.dmf.exception;

/**
 * Exception denoting a programming error processing a schema.
 * @author leegc
 *
 */
public class AnalyticsInvalidSchemaException extends Exception {

	public AnalyticsInvalidSchemaException() {
		super();
	}
	
	public AnalyticsInvalidSchemaException(String message) {
		super(message);
	}
	
	public AnalyticsInvalidSchemaException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
