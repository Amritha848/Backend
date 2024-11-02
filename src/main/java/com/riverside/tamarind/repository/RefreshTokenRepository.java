package com.riverside.tamarind.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.riverside.tamarind.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Integer>{

	@Query("select r from RefreshToken r where r.user.id = :userId")
	Optional<RefreshToken> findByUserId(String userId);
	
//	Optional<RefreshToken> findByUuidToken(String token);

}
