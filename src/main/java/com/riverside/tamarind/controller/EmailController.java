package com.riverside.tamarind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riverside.tamarind.entity.Email;
import com.riverside.tamarind.service.EmailService;

@RestController
@RequestMapping("/api/v2")
public class EmailController {
	
	@Autowired
	EmailService service;
	
	
	public EmailController(EmailService service) {
		this.service = service;
	}


	@PostMapping("/sendOtp")
	public ResponseEntity<?> sendEmailOtp(@RequestBody Email email){
		
		var map=service.sendOtp(email);
		
		return new ResponseEntity<>(map,HttpStatus.OK);
		
	}
	
	
	@PostMapping("/verifyOtp")
	public ResponseEntity<?> verifyEmailOtp(@RequestBody Email email){
		
		var map=service.verifyOtp(email);
		
		return new ResponseEntity<>(map,HttpStatus.OK);
	}
	

}
