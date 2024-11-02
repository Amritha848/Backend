package com.riverside.tamarind.thymaleaf;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.service.UserDetailsServiceInfo;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import net.bytebuddy.utility.RandomString;


@RestController
@RequestMapping("api/v7")
public class ThymaleafController {
	
	@Autowired
	UserDetailsServiceInfo service;
	
	@Autowired
	private JavaMailSender mailSender;
	
	 
	    @GetMapping("/forgot-password")
	    public String showForgotPassword() {
	    	
	    	return "forgotPasswordForm";
	    	
	    }
	    
	   @PostMapping("/forgot-password") 
	    public Map<?,?> processForgotPassword(@RequestParam String email) throws MessagingException, jakarta.mail.MessagingException {
	    	
	    	
	    	
	    	System.out.println(email);
	    	
	    	Map<String,Object> map = new HashMap<>();
	    		    	
	    	String token = RandomString.make(200);
	    	
	    	try {
	    		
	    		service.updateResetPasswordToken(token, email);
	    		
	    		map.put("statusCode", HttpStatus.OK.value());
	    		
	    		String resetPasswordLink ="/api/v7/reset-password?token="+token;
	    			    		
	    		sendEmail(email,resetPasswordLink);
	    		
	    		map.put("message", "Email has been sent to your "+email);
	    		
	    	}catch(NoSuchElementException ex) {
	    		
	    		throw new NoSuchElementException("Email is not found In our DataBase");
	    		
	    		
	    	}catch(UnsupportedEncodingException ex) {
	    			    		
	    		throw new IllegalArgumentException("Unsupported Format");
	    		
	    	}
	    	
	    	return map;
	    	
	    }
	    
	    public void sendEmail(String recipientEmail, String link)
	            throws MessagingException, UnsupportedEncodingException, jakarta.mail.MessagingException {
	        MimeMessage message = mailSender.createMimeMessage();             
	        MimeMessageHelper helper = new MimeMessageHelper(message);
	         
	        helper.setFrom("adnim@ivis.com", "Riverside IVIS");
	        helper.setTo(recipientEmail);
	         
	        String subject = "Here's the link to reset your password";
	         
	        String content = "<p>Hello,</p>"
	                + "<p>You have requested to reset your password.</p>"
	                + "<p>Click the link below to change your password:</p>"
	                + "<p><a href=\"" + link + "\">Change my password</a></p>"
	                + "<br>"
	                + "<p>Ignore this email if you do remember your password, "
	                + "or you have not made the request.</p>";
	         
	        helper.setSubject(subject);
	         
	        helper.setText(content, true);
	         
	        mailSender.send(message);
	    }
	    
	    @GetMapping("/reset-password")
		public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
	    User user = service.getByResetPasswordToken(token);
	    model.addAttribute("token", token);
	     
	    if (user == null) {
	        model.addAttribute("message", "Invalid Token");
	        return "message";
	    }
	     
	    return "resetPassword";
		}
	    
	    @PutMapping("/reset-password")
		public String processResetPassword(HttpServletRequest request, Model model,@RequestBody User customer) {
			String token = request.getParameter("token");
            String password = customer.getPassword();
			
			
			User user = service.getByResetPasswordToken(token);
			model.addAttribute("title", "Reset your password");
			
			if (user == null) {
				model.addAttribute("message", "Invalid Token");
				return "resetPassword";
			} else {           
				service.updatePassword(user, password);
				
				model.addAttribute("message", "You have successfully changed your password.");
			}
			
			return "password updated successfully";
		}
	    

}
