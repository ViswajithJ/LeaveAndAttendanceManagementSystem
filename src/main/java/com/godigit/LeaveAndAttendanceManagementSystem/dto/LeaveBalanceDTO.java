package com.godigit.LeaveAndAttendanceManagementSystem.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveBalanceDTO {
    private Long userId;
    private int totalLeaves;
    private int leavesTaken;
    private int leavesRemaining;
}
