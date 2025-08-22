package com.godigit.LeaveAndAttendanceManagementSystem.mapper;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;

public class AttendanceMapper {
    private AttendanceMapper() {
        // private constructor to prevent instantiation
    }

    public static AttendanceResponseDTO toDto(Attendance attendance) {
        if (attendance == null) {
            return null;
        }

        return AttendanceResponseDTO.builder()
                .id(attendance.getId())
                .punchInTime(attendance.getPunchInTime())
                .punchOutTime(attendance.getPunchOutTime())
                .userId(attendance.getUser().getId())
                .build();
    }
}
