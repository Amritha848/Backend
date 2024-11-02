package com.riverside.tamarind.entity;





import com.fasterxml.jackson.annotation.JsonBackReference;
import com.riverside.tamarind.enums.TokenType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "Token")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(length = 1000)
	private String token;
	
	@Enumerated(EnumType.STRING)
	private TokenType tokenType;
	
	private boolean expired;
	
	private boolean revoked;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	@JsonBackReference
	private User user;

}
