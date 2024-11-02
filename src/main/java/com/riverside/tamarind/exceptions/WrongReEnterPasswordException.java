package com.riverside.tamarind.exceptions;

@SuppressWarnings("serial")
public class WrongReEnterPasswordException extends RuntimeException{
	
	public WrongReEnterPasswordException(String msg) {
		super(msg);
	}

}
