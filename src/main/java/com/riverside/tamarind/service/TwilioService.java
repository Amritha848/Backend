package com.riverside.tamarind.service;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.exceptions.InvalidOtpException;
import com.riverside.tamarind.generateotp.GenerateOtp;
import com.riverside.tamarind.repository.UserRepository;
import com.riverside.tamarind.twilioconfiguration.TwilioConfiguration;
import com.riverside.tamarind.twiliosms.SmsRequest;
import com.riverside.tamarind.twiliosms.ValidateOtp;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

@Service
public class TwilioService {
	
	Map<String,Object> map=new HashMap<>();
	
	public Map<String,Object> mobileNo=new HashMap<>();
	
	 public static Set<String> mobileNos=new HashSet<>();

	
	@Autowired
	private TwilioConfiguration twilioConfiguration;
	
	@Autowired
	private UserRepository repo;

	public Message sendNotification(SmsRequest smsRequest) {
	
		
		String userName=twilioConfiguration.getAccountSid();
		
		String password=twilioConfiguration.getAuthToken();
		
		Twilio.init(userName, password);
		
		PhoneNumber to=new PhoneNumber(smsRequest.getMobileNo());
		
		PhoneNumber from=new PhoneNumber(twilioConfiguration.getPhoneNumber());
		
		String message=smsRequest.getMessage();
		
		MessageCreator creator=Message.creator(to,from,message);
		
		return creator.create();
	}
		
	

	public Map<?,?> sendOtp(GenerateOtp generateOtp) {
		
		SmsRequest smsRequest1=new SmsRequest();
	
		User user=repo.findByMobileNo(generateOtp.getMobileNo().substring(3).trim()).orElseThrow();
		
		mobileNo.put("mobileNo",generateOtp.getMobileNo().substring(3));
		
		String mobileNumbers=generateOtp.getMobileNo().substring(3);
		
		 mobileNos=Stream.of(mobileNumbers).collect(Collectors.toSet());
		
	    String otp=generateOtp();
	    
	    String message="Hi "+user.getName()+" , The otp to reset your Leave Management Appplication password is "+otp+
	    		" . Please don't share this OTP to anyone";
	    		
        smsRequest1.setMobileNo(generateOtp.getMobileNo());
		
		smsRequest1.setMessage(message);
		
		sendNotification(smsRequest1);
	    
	    map.put("otp", otp);
	    
	    System.out.println(otp);
	    
        Map<String,Object> map=new HashMap<>();
		
		map.put("message","OTP has sent successfully to "+"XXXXXX"+generateOtp.getMobileNo().substring(9));
		
		map.put("statusCode",HttpStatus.OK.value());
		
		return map;
	
	}
	public  String generateOtp() {
		return new DecimalFormat("000000")
				.format(new Random().nextInt(999999));
    }

	public Map<?,?> verifyOtp(ValidateOtp validateOtp) {
	
           if(!validateOtp.getEnterOtp().equals(map.get("otp"))) 
        	   
        	   throw new InvalidOtpException("INVALID OTP. THE OTP YOU ENTERED IS "
        	   		+ "WRONG OR EXPIRED"); 
           
          Map<String,Object> maps=new HashMap<>();
   		
   		maps.put("message","OTP verified successfully. Now you can proceed to change your password");
   		
   		maps.put("statusCode",HttpStatus.OK.value());
   		
   		return maps;
   			   
	}
	
	
	
	
	

}
