package com.riverside.tamarind.exceptions;

@SuppressWarnings("serial")
public class ManagerNotPresentInDatabaseException extends RuntimeException{
	
	public ManagerNotPresentInDatabaseException(String msg) {
		super(msg);
	}

}
