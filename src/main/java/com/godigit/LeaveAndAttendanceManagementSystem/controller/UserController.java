package com.godigit.LeaveAndAttendanceManagementSystem.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserCreateDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private PasswordEncoder passwordEncoder;


    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // Only ADMIN can create new users
    @PreAuthorize("hasRole('ADMIN')") //uncomment later after proper security
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        return ResponseEntity.ok(userService.createUser(dto));
    }

    // ADMIN and MANAGER can list all users
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") //here tooo
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }
}
