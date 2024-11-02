package com.riverside.tamarind.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.riverside.tamarind.entity.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer>{
	
	@Query("select t from Token t INNER JOIN User u on t.user.userId=u.userId where t.user.userId=:userId and (t.expired=false and revoked=false)")
	List<Token> findAllTokenByUser(String userId);
	
	Optional<Token> findByToken(String token);

}
