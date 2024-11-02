package com.riverside.tamarind.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.riverside.tamarind.entity.RefreshToken;
import com.riverside.tamarind.jwtToken.JwtService1;
import com.riverside.tamarind.repository.RefreshTokenRepository;
import com.riverside.tamarind.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v6")
public class RefreshTokenController {

	@Autowired
	private JwtService1 jwtService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserController userController;

	@Autowired
	private RefreshTokenRepository refreshTokenRepository;

	@PostMapping("/refresh-token")
	public void refreshTokens(HttpServletRequest request, HttpServletResponse response)
			throws StreamWriteException, DatabindException, IOException {

		String authHeader = request.getHeader(org.springframework.http.HttpHeaders.AUTHORIZATION);
		String refreshToken = null;
		String userId = null;

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			refreshToken = authHeader.substring(7);
			userId = jwtService.extractUsername(refreshToken);
		}

		if (userId != null) {
			var user = userRepository.findByUserId(userId).orElseThrow();

			System.out.println(user);

			if (jwtService.isValid(refreshToken, user)) {

				var accessToken = jwtService.generateRefreshToken(user);

				userController.removeAllUserTokens(user);

				userController.saveUserToken(accessToken, user);

				Map<String, Object> map = new HashMap<>();

				map.put("accessToken", accessToken);

				map.put("refreshToken", refreshToken);

				response.setContentType("application/json");

				new ObjectMapper().writeValue(response.getOutputStream(), map);

				refreshTokenSaveToDatabase(refreshToken, userId, accessToken);

			}
		}

	}

	public void refreshTokenSaveToDatabase(String refreshToken, String userId, String accessToken) {
		Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userId);

		if (existingToken.isPresent()) {

			RefreshToken token = existingToken.get();

			token.setAccessToken(accessToken);
			token.setRefreshToken(refreshToken);

			refreshTokenRepository.save(token);

		} else {

			RefreshToken refreshToken2 = new RefreshToken();

			refreshToken2.setAccessToken(accessToken);

			refreshToken2.setRefreshToken(refreshToken);

			refreshToken2.setUser(userRepository.findByUserId(userId).get());

			refreshTokenRepository.save(refreshToken2);

		}
	}

}
