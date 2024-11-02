package com.riverside.tamarind.dto;

import com.riverside.tamarind.enums.LeaveStatus;
import com.riverside.tamarind.enums.LeaveType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveManagementDTO {
   
	private String leaveId;

	private String fromDate;
    private String toDate;
    private String reason;
    @Enumerated(EnumType.STRING)	
	private LeaveType type;
    @Enumerated(EnumType.STRING)
    private LeaveStatus status;
    private long sickLeavesCount;
    private long casualLeavesCount;
    private String appliedDate;
    private String message;
    private String user;
    private String employeeName;
    

}
