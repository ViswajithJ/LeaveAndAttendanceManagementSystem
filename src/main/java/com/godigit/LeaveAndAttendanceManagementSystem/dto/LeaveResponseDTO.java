package com.godigit.LeaveAndAttendanceManagementSystem.dto;

import java.time.LocalDate;

import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.LeaveStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LeaveResponseDTO {
    private Long id;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
}
