package com.riverside.tamarind.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
	
	private String token;
	
	private String role;
	
	private int statusCode;
	
	private String userid;
	
	private String employeeName;
	
	private String refreshToken;
	
	private String message;
	

	
	
	

}
