package com.riverside.tamarind.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.riverside.tamarind.enums.LeaveStatus;
import com.riverside.tamarind.enums.LeaveType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "leave_management")
public class LeaveManagement {
	
	@Id
	private String leaveId;
	
	@NotNull(message = "select fromDate")
	private LocalDate fromDate;
	
	@NotNull(message = "select toDate")
	private LocalDate toDate;
	
	private String reason;
	
	@NotNull(message = "Select the leave type")
	@Enumerated(EnumType.STRING)
	private LeaveType type;
	
	@ManyToOne
	@JoinColumn(name = "employeeId")
	@JsonBackReference
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private LeaveStatus status;
	
	@Column(columnDefinition = "bigint default 0")
	private long sickLeavesCount;
	
	@Column(columnDefinition = "bigint default 0")
	private long casualLeavesCount;
	
	private LocalDateTime appliedDate;
	
	private String message;
		
	

}
