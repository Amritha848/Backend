package com.riverside.tamarind.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.riverside.tamarind.enums.TaskStatus;

import lombok.Data;

@Data
public class TaskDTO {

	
	private String title;
	
	private String description;
	
	private LocalDate deadline;
	
	private TaskStatus taskStatus;

	private String employee_id;
	
	private String employeeName;

    private LocalDateTime taskCreationTime;
}
