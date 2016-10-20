package com.deleidos.dp.exceptions;

/**
 * Exception that means there is an inconsistency with the expected main type and the
 * actual main type.
 * @author leegc
 *
 */
public class MainTypeException extends Exception {

	public MainTypeException() {
		
	}
	
	public MainTypeException(String message) {
		super(message);
	}
	
}
