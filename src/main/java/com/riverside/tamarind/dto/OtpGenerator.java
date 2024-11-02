package com.riverside.tamarind.dto;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class OtpGenerator {
	
	
	public Map<String,Object> maps=new HashMap<>();
	
	public String subject() {
		return "OTP for Password Reset - Leave Management Application";
	}
	
	public  String otpGenerator() {
	
		return  new DecimalFormat("000000")
				.format(new Random().nextInt(999999));
	}
	
	public  String body() {
		
	maps.put("otp",otpGenerator());
		
		return "Dear "+"To reset your riverside account password. Enter the Otp and change your password. your OTP= "+maps.get("otp");
	}

}
