package com.godigit.LeaveAndAttendanceManagementSystem.dto;

import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private Long manager_id;
}