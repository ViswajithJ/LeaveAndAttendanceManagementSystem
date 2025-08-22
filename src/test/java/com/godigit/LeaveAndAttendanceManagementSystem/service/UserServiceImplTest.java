package com.godigit.LeaveAndAttendanceManagementSystem.service;


import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserCreateDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.Role;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.AttendanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.Impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepo;
    @Mock private LeaveBalanceRepository leaveBalanceRepo;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private LeaveRepository leaveRepo;
    @Mock private AttendanceRepository attendanceRepo;

    @InjectMocks private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setEmail("john@example.com");
        user.setRole(Role.EMPLOYEE);
    }

    @Test
    void createUser_shouldSaveUserAndInitializeLeaveBalance() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setFullName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPassword("password123");
        dto.setRole(Role.EMPLOYEE);

        when(passwordEncoder.encode("password123")).thenReturn("encodedPass");
        when(userRepo.save(any())).thenReturn(user);

        UserDTO result = userService.createUser(dto);

        assertEquals("John Doe", result.getFullName());
        assertEquals(Role.EMPLOYEE, result.getRole());
        verify(leaveBalanceRepo).save(any());
    }

    @Test
    void getAllUsers_shouldReturnUserDTOList() {
        when(userRepo.findAll()).thenReturn(List.of(user));

        List<UserDTO> users = userService.getAllUsers();

        assertEquals(1, users.size());
        assertEquals("John Doe", users.get(0).getFullName());
    }

    @Test
    void getUserById_shouldReturnUserDTO_whenUserExists() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        UserDTO dto = userService.getUserById(1L);

        assertEquals("John Doe", dto.getFullName());
        assertEquals(Role.EMPLOYEE, dto.getRole());
    }

    @Test
    void updateUser_shouldModifyUserDetails() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setFullName("Jane Doe");
        dto.setEmail("jane@example.com");
        dto.setPassword("newpass");
        dto.setRole(Role.MANAGER);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");
        when(userRepo.save(any())).thenReturn(user);

        UserDTO updated = userService.updateUser(1L, dto);

        assertEquals("Jane Doe", updated.getFullName());
        assertEquals("jane@example.com", updated.getEmail());
        assertEquals(Role.MANAGER, updated.getRole());
    }

    @Test
    void deleteUser_shouldRemoveUserAndLeaveBalance() {
        LeaveBalance balance = new LeaveBalance();
        balance.setUser(user);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(leaveBalanceRepo.findByUser(user)).thenReturn(Optional.of(balance));

        userService.deleteUser(1L);

        verify(leaveBalanceRepo).delete(balance);
        verify(userRepo).delete(user);
    }

    @Test
    void deleteUserAndRelatedData_shouldCascadeDelete() {
        userService.deleteUserAndRelatedData(1L);

        verify(leaveRepo).deleteByUserId(1L);
        verify(attendanceRepo).deleteByUserId(1L);
        verify(userRepo).deleteById(1L);
    }
}
