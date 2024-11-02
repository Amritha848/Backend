package com.riverside.tamarind.twilioconfiguration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("twilio")
@Data
public class TwilioConfiguration {
	
	private String accountSid;
	
	private String authToken;
	
	private String phoneNumber;

}
