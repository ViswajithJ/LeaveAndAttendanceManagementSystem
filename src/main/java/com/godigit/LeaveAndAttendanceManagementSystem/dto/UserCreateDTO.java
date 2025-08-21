package com.godigit.LeaveAndAttendanceManagementSystem.dto;

import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDTO {
    @NotBlank
    private String fullName;

    @Email
    private String email;

    @NotBlank
    private String password;

    private Role role;
     
    private Long managerId;
}
