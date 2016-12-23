package com.deleidos.dmf.exception;

public class JobQueueException extends Exception {

	public JobQueueException(String string) {
		super(string);
	}

	public JobQueueException(String message, Exception e) {
		super(message, e);
	}
}
