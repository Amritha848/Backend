package com.riverside.tamarind.authenticateresponse;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

@Data
@JsonPropertyOrder({"userid","name","jwtToken","role","expirationTime"})
public class AuthenticateResponse {
	
	private String jwtToken;
	
	private String name;
	
	private String expirationTime;
	
	private String role;
	
	private String id;
	
	private String message;
	
	private int statusCode;
	

}
