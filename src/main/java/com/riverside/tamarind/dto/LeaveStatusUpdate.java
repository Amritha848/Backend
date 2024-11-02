package com.riverside.tamarind.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.riverside.tamarind.enums.LeaveStatus;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class LeaveStatusUpdate {
	
	@Enumerated(EnumType.STRING)
	private  LeaveStatus status;
	
	private String leaveId;
	
    private boolean present;
	
	private String message;
	
	private LocalDate date;
	
public String[] ignoreProperties() {
		
	List<Object> list=new ArrayList<>();
	
	if(LeaveStatus.values() == null) {
		
		list.add(LeaveStatus.values());
	}
	
	if(leaveId == null) {
		list.add(leaveId);
	}

	if(message == null)
	{
		list.add(message);
	}
	return list.toArray(new String[0]);

}

}
