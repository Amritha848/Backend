package com.riverside.tamarind.entity;

import java.util.LinkedList;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

public class UserUpdate {
	
	private String userId;
	
	@NotNull(message = "Enter the new password")
    private String password;
    
	@NotNull(message = "Enter the password again to confirm")
    private String confirmPassword;
    
    private String employeeName;
    
//    private String name;

    
//    @Pattern(regexp = "[6789]\\d{9}",message = "WRONG MOBILE NUMBER")
//    private String mobileNo;

    
//    @Email(message = "WRONG EMAIL ADDRESS")
//    private String email;
   
    
//    @Enumerated(EnumType.STRING)
//    private Status status;
    

	public String[] notNullColumns() {
		
		List<String> list=new LinkedList<>();
		
		if(password == null) {
			list.add("password");
		}
		
//		if(mobileNo == null) {
//			list.add("mobileNo");
//		}
//		if(email==null) {
//			list.add("email");
//		}
//		if(status==null) {
//			list.add("status");
//		}
//		
//		if(name == null) {
//			list.add("name");
//		}
		
		return list.toArray(new String[0]);
		
	}
    
    

}
