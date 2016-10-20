package com.deleidos.dmf.exception;

/**
 * Exception to denote that a workflow was cancelled and a response should not be sent to the frontend.
 * @author leegc
 *
 */
public class AnalyticsCancelledWorkflowException extends AnalyticsTikaProfilingException{

	public AnalyticsCancelledWorkflowException(String string) {
		super(string);
	}
	
	public AnalyticsCancelledWorkflowException(String string, Throwable throwable) {
		super(string, throwable);
	}

}
