package com.riverside.tamarind.exceptions;

@SuppressWarnings("serial")
public class TokenNotFoundInDtabaseException extends RuntimeException{
	
	public TokenNotFoundInDtabaseException(String msg) {
		super(msg);
	}

}
