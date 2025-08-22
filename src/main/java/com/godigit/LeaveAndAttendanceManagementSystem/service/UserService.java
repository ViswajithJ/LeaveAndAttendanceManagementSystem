package com.godigit.LeaveAndAttendanceManagementSystem.service;

import java.util.List;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserCreateDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;

public interface UserService {
    UserDTO createUser(UserCreateDTO dto);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    UserDTO updateUser(Long id,UserCreateDTO dto);

    void deleteUser(Long id);

    UserDTO mapToDTO(User user);


}
