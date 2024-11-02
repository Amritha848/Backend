package com.riverside.tamarind.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riverside.tamarind.generateotp.GenerateOtp;
import com.riverside.tamarind.service.TwilioService;
import com.riverside.tamarind.twiliosms.ValidateOtp;

@RestController
@RequestMapping("/api/v1")
public class TwilioOtpController {
	
	@Autowired
	private TwilioService twilioService;
	
	
	@PostMapping("/sendOtp")
	
	
	public ResponseEntity<?> sendOtp(@RequestBody GenerateOtp generateOtp){
	
		var map=twilioService.sendOtp(generateOtp);
		
		return new ResponseEntity<>(map,HttpStatus.OK);
	  
		
	}
	
	@PostMapping("/verifyOtp")
    
	public ResponseEntity<?> verifyOtp(@RequestBody ValidateOtp validateOtp){
		
		
		var map=twilioService.verifyOtp(validateOtp);
		
       return new ResponseEntity<>(map,HttpStatus.OK);
			
	}
	 

}
