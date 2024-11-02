package com.riverside.tamarind.dto;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.riverside.tamarind.enums.Status;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class UpdateProfileDTO {

    @Column(name = "user_names", length = 17 , unique = true)
    @Pattern(regexp = "^[A-Z][A-za-z]{6,16}$",message = "The userName should contain first character must be capital letter and character length should between 6 and 16")
    private String name;
    
    @Column(length = 15, unique = true, updatable = false)
    @Pattern(regexp = "^\\d{4}[A-Z]{2}\\d{2}[A-Z]\\d$",message = "The userId should be in the format of ex: 2427RS01A0")
    private String userId;
    
    @Column(name = "password",length = 200)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[#$@!%&*?])[A-Za-z\\d#$@!%&*?]{8,}$",message = "The password should contain one uppercase, one lowercase"
    		+ ", one special character, one digit at least and more than 8 characters")
    private String password;
    
    @Column(name = "roles")
    private String role;
    
    @Column(name="registered_Date", nullable = false ,insertable = false,updatable = false, columnDefinition = "datetime(6) default current_timestamp(6)")
    private Date registeredDate;

    @Column(name = "mobilenos" , length = 15 ,unique = true)
    @Pattern(regexp = "[6789]\\d{9}",message = "WRONG MOBILE NUMBER")
    private String mobileNo;

    @Column(name = "emails" , length = 50 , unique = true)
    @Email(message = "WRONG EMAIL ADDRESS")
    private String email;
    
    @JsonInclude(value=Include.NON_EMPTY, content=Include.NON_NULL)
    @Transient
    private String jwtToken;
    
    private String attendance;
        
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Column(name = "departmentName")
    private String departmentName;
    
    public String[] notNullColumns() {
    	
    	List<Object> list = new LinkedList<>();
    	
    	if(userId == null) {
    		
    		list.add("userId");
    		
    	}
    	
    	if(name == null) {
    		
    		list.add("name");
    	}
    	
    	if(password == null) {
    		
    		list.add("password");
    	}
    	
    	if(role == null) {
    		
    		list.add("role");
    	}
    	
    	if(registeredDate == null) {
    		
    		list.add("registeredDate");
    	}
    	
    	if(mobileNo == null) {
    		
    		list.add("mobileNo");
    	}
    	
    	if(email == null) {
    		
    		list.add("email");
    	}
    	
    	if(attendance == null) {
    		
    		list.add("attendance");
    	}
    	
    	if(status == null) {
    		
    		list.add("status");
    	}
    	
    	if(departmentName == null) {
    		
    		list.add("departmentName");
    	}
    	
    	System.out.println(list);
    	
    	return list.toArray(new String[0]);
    	
    }

}
