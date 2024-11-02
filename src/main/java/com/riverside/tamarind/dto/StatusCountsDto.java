package com.riverside.tamarind.dto;

import java.util.Date;

import com.riverside.tamarind.enums.Status;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class StatusCountsDto {

	private String userId;

	

	public StatusCountsDto(String userId, String name, String mobileNo, String email, String departmentName,
			Date registeredDate, Long sickLeaveCounts, Long casualLeaveCounts, Long privilegeLeaveCounts,
			Status status) {
		
		this.userId = userId;
		this.name = name;
		this.mobileNo = mobileNo;
		this.email = email;
		this.departmentName = departmentName;
		this.registeredDate = registeredDate;
		this.sickLeaveCounts = sickLeaveCounts;
		this.casualLeaveCounts = casualLeaveCounts;
		this.privilegeLeaveCounts = privilegeLeaveCounts;
		this.status = status;
	}

	

	private String name;

	private String mobileNo;

	private String email;
	
	private String departmentName;
	
	private Date registeredDate;

	private Long sickLeaveCounts;

	private Long casualLeaveCounts;

	private Long privilegeLeaveCounts;
	
	private Status status;

}
