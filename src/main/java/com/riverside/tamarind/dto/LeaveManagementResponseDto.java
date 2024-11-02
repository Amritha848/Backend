package com.riverside.tamarind.dto;

import java.time.LocalDate;

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
@AllArgsConstructor
@NoArgsConstructor
public class LeaveManagementResponseDto {
	


		
//
//		public LeaveManagementResponseDto(String leaveId, LocalDate fromDate, LocalDate toDate, String reason,
//			LeaveType type, LeaveStatus status, String appliedDate, String userId, String employeeName,String message) {
//		this.leaveId = leaveId;
//		this.fromDate = fromDate;
//		this.toDate = toDate;
//		this.reason = reason;
//		this.type = type;
//		this.status = status;
//		this.appliedDate = appliedDate;
//		this.userId = userId;
//		this.employeeName = employeeName;
//		this.message=message;
//		
//	}



		private String leaveId;
		
		private LocalDate fromDate;
		
		private LocalDate toDate;
		
		private String reason;
		
		@Enumerated(EnumType.STRING)
		private LeaveType type;
		
		private LeaveStatus status;
		
		private String appliedDate;
		
		private String userId;
		
		private String employeeName;
		
		private String message;
		

}
