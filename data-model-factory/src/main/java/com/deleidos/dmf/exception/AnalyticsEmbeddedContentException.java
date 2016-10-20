package com.deleidos.dmf.exception;

public class AnalyticsEmbeddedContentException extends AnalyticsTikaProfilingException {

	public AnalyticsEmbeddedContentException(String message, Exception exception) {
		super(message, exception);
	}
	
	public AnalyticsEmbeddedContentException(String message) {
		super(message);
	}
}
