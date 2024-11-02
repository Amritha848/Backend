package com.riverside.tamarind.exceptions;

@SuppressWarnings("serial")
public class ClickSendOtpBeforeVerifyOtp extends RuntimeException{
	
	public ClickSendOtpBeforeVerifyOtp(String message) {
		super(message);
	}

}
