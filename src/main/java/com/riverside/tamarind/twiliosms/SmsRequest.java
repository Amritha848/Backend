package com.riverside.tamarind.twiliosms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SmsRequest {
	
	private String mobileNo;
	
	private String message;
	
	

}
