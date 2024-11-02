package com.riverside.tamarind.exceptions;

@SuppressWarnings("serial")
public class AlreadyOtpSendException extends RuntimeException{

	public AlreadyOtpSendException(String message) {
		super(message);
	}
}
