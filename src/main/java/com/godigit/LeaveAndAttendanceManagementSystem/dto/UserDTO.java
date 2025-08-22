package com.godigit.LeaveAndAttendanceManagementSystem.dto;

import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.Role;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    @NotBlank(message = "Full name is mandatory")
    private String fullName;
    private String email;
    private Role role;
    private Long manager_id;
}