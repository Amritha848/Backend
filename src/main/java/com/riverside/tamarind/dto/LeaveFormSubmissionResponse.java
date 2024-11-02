package com.riverside.tamarind.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LeaveFormSubmissionResponse {
	
	private String message;
	
	private int statusCode;
	
	
	

}
