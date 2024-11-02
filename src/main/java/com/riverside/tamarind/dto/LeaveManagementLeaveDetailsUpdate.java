package com.riverside.tamarind.dto;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.riverside.tamarind.entity.User;
import com.riverside.tamarind.enums.LeaveStatus;
import com.riverside.tamarind.enums.LeaveType;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveManagementLeaveDetailsUpdate {

	private String leaveId;
	
	private LocalDate fromDate;
	
	private LocalDate toDate;
	
	private String reason;
	
	@Enumerated(EnumType.STRING)
	private LeaveType type;
	
	@ManyToOne
	@JoinColumn(name = "employeeId")
	@JsonBackReference
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private LeaveStatus status;
	
	private long sickLeavesCount;
	
	private long casualLeavesCount;
		
	private String appliedDate;
	
	private String message;
	
	public String[] notNullColumns() {
		
		List<Object> list = new LinkedList<>();
		
		if(leaveId == null) {
			
			list.add("leaveId");
		}
		
		if(fromDate == null) {
			
			list.add("fromDate");
		}
		
		if(toDate == null) {
			
			list.add("toDate");
		}
		
		if(reason== null) {
			
			list.add("reason");
		}
		
		if(type == null) {
			
			list.add("type");
		}
		
		if(user == null) {
			
			list.add("user");
		}
		
		if(status == null) {
			
			list.add("status");
		}
		
		if(Long.valueOf(sickLeavesCount) == null) {
			
			list.add("sickLeavesCount");
		}
		
		if(Long.valueOf(casualLeavesCount) == null) {
			
			list.add("casualLeavesCount");
		}
		
	
		if(appliedDate == null) {
			
			list.add("appliedDate");
		}
		
		if(message == null) {
			
			list.add("message");
		}
		
		return list.toArray(new String[0]);
		
	}

}




