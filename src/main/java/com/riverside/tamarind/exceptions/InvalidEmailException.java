package com.riverside.tamarind.exceptions;


@SuppressWarnings("serial")
public class InvalidEmailException extends RuntimeException{
	
	public InvalidEmailException(String msg) {
		
		super(msg);
	}

}
