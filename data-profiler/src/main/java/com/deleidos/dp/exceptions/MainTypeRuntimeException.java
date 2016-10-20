package com.deleidos.dp.exceptions;

/**
 * RuntimeException that represents an error in how the program handles main types.
 * @author leegc
 *
 */
public class MainTypeRuntimeException extends RuntimeException {
	
	public MainTypeRuntimeException() {
		this("An expected attribute of a main type was not found.");
	}
	
	public MainTypeRuntimeException(String message) {
		super(message);
	}
	
	public MainTypeRuntimeException(String message, Throwable e) {
		super(message, e);
	}
}
