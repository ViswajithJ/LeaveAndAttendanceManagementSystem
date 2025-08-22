package com.godigit.LeaveAndAttendanceManagementSystem.mapper;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveApplication;

public class LeaveMapper {
    private LeaveMapper() {
        // prevent instantiation
    }

    public static LeaveResponseDTO toResponseDto(LeaveApplication leave) {
        if (leave == null) {
            return null;
        }

        return LeaveResponseDTO.builder()
                .id(leave.getId())
                .userId(leave.getUser().getId())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .build();
    }
}
