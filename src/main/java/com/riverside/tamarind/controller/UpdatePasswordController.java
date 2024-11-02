package com.riverside.tamarind.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riverside.tamarind.entity.UpdatePasswordEntity;
import com.riverside.tamarind.service.UpdatePasswordService;



@RestController
@RequestMapping("api/v1")
public class UpdatePasswordController {
	
	@Autowired
	private UpdatePasswordService updatePasswordService;
	
	@PatchMapping("/update/password")
	@PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
	public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordEntity updatePasswordEntity,Principal principal){
		
		var user=updatePasswordService.updatePassword(updatePasswordEntity,principal);
		
		return new ResponseEntity<>(user,HttpStatus.OK);
		
	}
	

}
