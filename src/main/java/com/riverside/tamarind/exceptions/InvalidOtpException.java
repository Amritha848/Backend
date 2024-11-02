package com.riverside.tamarind.exceptions;

@SuppressWarnings("serial")
public class InvalidOtpException extends RuntimeException{
	
	public InvalidOtpException(String message) {
		
		super(message);
	}

}
