package com.riverside.tamarind.dto;

import java.util.Date;

import com.riverside.tamarind.enums.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employees {
	
	 private String userId;

	    private String name;

	    private String mobileNo;

	    private String email;
	    
	    private Status status;
	    
	    private Date registeredDate;
	    
	    private String departmentName;
	    
	   

}
