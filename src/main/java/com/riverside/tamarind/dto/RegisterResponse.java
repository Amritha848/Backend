package com.riverside.tamarind.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterResponse {
	
	private int statusCode;
	
	private String token;
	
	private String role;
	
	private String message;
	
	private String refreshToken;

}
