//package com.riverside.tamarind.service;
//
//import java.time.Instant;
//import java.util.Optional;
//import java.util.UUID;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import com.riverside.tamarind.entity.RefreshToken;
//import com.riverside.tamarind.exceptions.UserNotFoundException;
//import com.riverside.tamarind.repository.RefreshTokenRepository;
//import com.riverside.tamarind.repository.UserRepository;
//
//@Service
//public class RefreshTokenService {
//	
//	@Value("${refresh.token.expiration.time.in.milliseconds}")
//	private long refreshTokenExpirationTime;
//	
//	@Autowired
//	private RefreshTokenRepository refreshTokenRepository;
//	
//	@Autowired
//	private UserRepository userRepository;
//	
//	public RefreshToken createRefreshToken(String userId) {
//		
//		System.out.println(userId);
//		
//		System.out.println(userRepository.findByUserId(userId).get());
//		
//		RefreshToken refreshToken=RefreshToken.builder()
//				.user(userRepository.findByUserId(userId).get())
//				.uuidToken(UUID.randomUUID().toString())
//				.expiredTime(Instant.now().plusMillis(refreshTokenExpirationTime))
//				.build();
//		return refreshTokenRepository.save(refreshToken);
//	}
//
//	
//
//	public Optional<RefreshToken> findByUuidToken(String token) {
//		
//		return refreshTokenRepository.findByUuidToken(token);
//	}
//	
//	public RefreshToken verifyExpiration(RefreshToken token) {
//		
//		if(token.getExpiredTime().compareTo(Instant.now())<0) {
//			
//			System.out.println(token.getExpiredTime());
//			
//			System.out.println(Instant.now());
//			
//			System.out.println(token.getExpiredTime().compareTo(Instant.now()));
//			
//			refreshTokenRepository.delete(token);
//			
//			throw new UserNotFoundException("Refresh Token is expired. make a new Login!..");
//			
//		}
//		
//		return token;
//		
//	}
//	
//	
////	
////	public RefreshToken createRefreshToken(String userName) {
////		RefreshToken refreshToken=RefreshToken.builder()
////		.user(userRepository.findByUserId(userName).get())
////		.expiredTime(new Instant(System.currentTimeMillis()+24*60*60*1000))
////		.uuidToken(UUID.randomUUID().toString())
////		.build();
////		
////		return refreshTokenRepository.save(refreshToken);
////	}
////	
////	public Optional<RefreshToken> findByToken(String token){
////		return refreshTokenRepository.findByUuidToken(token);
////	}
////	
////	public RefreshToken verifyExpiration(RefreshToken token) {
////		if(token.getExpiredTime().compareTo(new Date(System.currentTimeMillis()))<0) {
////			refreshTokenRepository.delete(token);
////				throw new RuntimeException(token.getUuidToken() + " Refresh token is expired. Please make a new login..!");
////		}
////		return token;
////	}
////	
//	
//	
//	
//
//}
