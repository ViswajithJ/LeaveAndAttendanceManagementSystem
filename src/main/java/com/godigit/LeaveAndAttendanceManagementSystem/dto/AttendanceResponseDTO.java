package com.godigit.LeaveAndAttendanceManagementSystem.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceResponseDTO {
    private Long id;
    private LocalDateTime punchInTime;
    private LocalDateTime punchOutTime;
    private Long userId; // to identify the employee
}
