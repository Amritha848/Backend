package com.riverside.tamarind.dto;

import com.riverside.tamarind.entity.User;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class Employee{
	
	public Employee(User user) {
		employeeId=user.getUserId();
	}
	
	@Column(length = 15, unique = true, updatable = false)
    @Pattern(regexp = "^\\d{4}[A-Z]{2}\\d{2}[A-Z]\\d$")
    @NotNull(message = "Enter your user_id") 
    private String employeeId;

}
