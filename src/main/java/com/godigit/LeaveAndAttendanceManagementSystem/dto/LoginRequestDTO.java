package com.godigit.LeaveAndAttendanceManagementSystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginRequestDTO {
    private String email;
    private String password;
}
