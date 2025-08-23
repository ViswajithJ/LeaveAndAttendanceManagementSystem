package com.godigit.LeaveAndAttendanceManagementSystem.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserCreateDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserDTO;

public interface UserService {
    UserDTO createUser(UserCreateDTO dto);

    Page<UserDTO> getAllUsers(Pageable pageable);

    UserDTO getUserById(Long id);

    UserDTO updateUser(Long id, UserCreateDTO dto);

    void deleteUser(Long id);

}
