package com.godigit.LeaveAndAttendanceManagementSystem.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttendanceStatusDTO {
    private Long userId;
    private String status;
}