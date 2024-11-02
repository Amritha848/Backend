package com.riverside.tamarind.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.riverside.tamarind.enums.LeaveStatus;
import com.riverside.tamarind.enums.LeaveType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveDto {
    private String leaveId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private LeaveType type;
    private String userId;
    private LeaveStatus status;
    private LocalDateTime appliedDate;

    // Constructor, getters, and setters
}
