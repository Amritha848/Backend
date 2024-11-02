package com.riverside.tamarind.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import com.riverside.tamarind.repository.TokenRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLogoutHandler implements LogoutHandler{
	
	@Autowired
	private TokenRepository tokenRepository;

	@SuppressWarnings("null")
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		
	final String authHeader=request.getHeader("Authorization");
	if(authHeader == null && !authHeader.startsWith("Bearer ")) {
		return;
	}
	String token=authHeader.substring(7);
	
	var jwt=tokenRepository.findByToken(token).orElse(null);
	
	if(jwt != null) {
		jwt.setExpired(true);
		jwt.setRevoked(true);
		tokenRepository.save(jwt);
	}
	
	
	try {
		response.getWriter().write("{\"statusCode\":200,\"message\":\"Successfully logged out\"}");         
		response.setContentType("application/json");
		response.getWriter().flush();
	} catch (IOException e) {
		e.printStackTrace();
	}
	
		
	}

}
