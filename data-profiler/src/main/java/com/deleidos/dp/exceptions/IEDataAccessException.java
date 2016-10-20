package com.deleidos.dp.exceptions;

public class IEDataAccessException extends DataAccessException {

	public IEDataAccessException(String message) {
		super(message);
	}
	
	public static class DeadMongo extends IEDataAccessException {
		public DeadMongo(String message) {
			super(message);
		}
	}
}
