package com.riverside.tamarind.dto;

import java.util.List;

import com.riverside.tamarind.entity.LeaveManagement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataReturn {
	
	private List<LeaveManagement> leaveManagement;
	
	private String response;

}
