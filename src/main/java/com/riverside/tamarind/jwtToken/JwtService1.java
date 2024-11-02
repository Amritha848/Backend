package com.riverside.tamarind.jwtToken;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.ToString;

@Component
@ToString
public class JwtService1 {

	@Autowired
	private UserRepository repo;

	@Value("${spring.security.token.expiration.time}")
	private long accessTokenExpirationTime;
	
	@Value("${refresh.token.expiration.time.in.milliseconds}")
	private long refreshTokenExpirationTime;
	
	

	@Value("${spring.security.secret.key}")
	private String secretKey;

	public String extractUsername(String token) {
		return extractClaims(token, Claims::getSubject);
	}

	public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
		Claims allClaims = extractAllClaims(token);
		return claimsResolver.apply(allClaims);
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();

	}

	public String extractEmail(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get("email", String.class);
	}

	public String extractRole(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get("roles", String.class);
	}

	public String extractMobileNo(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get("mobileNo", String.class);
	}

	public Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public Date extractExpiration(String token) {
		return extractClaims(token, Claims::getExpiration);
	}

	public Boolean isValid(String token, User userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUserId()) && !isTokenExpired(token);
	}
	
	public Boolean isValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	public String generateAccessToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		return createAccessToken(claims, user);
	}

	public String generateRefreshToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		return createRefreshToken(claims, user);
	}

	private String createAccessToken(Map<String, Object> claims, User user) {

		return accessToken(claims, user, accessTokenExpirationTime);
	}

	private String createRefreshToken(Map<String, Object> claims, User user) {

		return accessToken(claims, user, 7 * refreshTokenExpirationTime);
	}

	private String accessToken(Map<String, Object> claims, User user, long expirationTime) {

		return Jwts.builder().setClaims(claims).claim("email", user.getEmail()).claim("mobileNo", user.getMobileNo())
				.claim("roles", user.getRole()).setSubject(user.getUserId())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(getSignKey(), SignatureAlgorithm.HS256).setHeaderParam("type", "JWT").compact();

	}

	private Key getSignKey() {
		byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);

	}

	
}
