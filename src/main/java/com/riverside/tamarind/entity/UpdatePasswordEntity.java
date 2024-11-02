package com.riverside.tamarind.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class UpdatePasswordEntity {
	
	private String oldPassword;
	
	private String newPassword;
	
	private String confirmPaSsword;

}
