package com.riverside.tamarind.corsconfig;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class CorsConfigurations implements CorsConfigurationSource {

	@Override
	public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
		CorsConfiguration corsConfiguration=new CorsConfiguration();
		    corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
	        corsConfiguration.setAllowedOriginPatterns(List.of("**"));
	        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE"));
	        corsConfiguration.setAllowCredentials(true);
	        corsConfiguration.setExposedHeaders(List.of("Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
		return corsConfiguration;
	}

}
