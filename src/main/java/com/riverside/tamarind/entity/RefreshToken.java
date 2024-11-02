package com.riverside.tamarind.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "RefreshTokens")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int sNo;
	
	@Column(length = 1000)
	private String refreshToken;
	
	@Column(length = 1000)
    private String accessToken;
	
	@OneToOne
	@JoinColumn(name = "user_id",referencedColumnName = "userId")
	private User user;

}
