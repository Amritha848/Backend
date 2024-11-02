package com.riverside.tamarind.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponseDTO {
	
	private String token;
	
	private String accessToken;
	
	private String role;

}
