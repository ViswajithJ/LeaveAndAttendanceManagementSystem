package com.godigit.LeaveAndAttendanceManagementSystem.mapper;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;

public class UserMapper {
    private UserMapper() {
        // prevent instantiation
    }

    public static UserDTO toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .manager_id(user.getManager() != null ? user.getManager().getId() : null)
                .build();
    }
}
