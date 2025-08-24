package com.godigit.LeaveAndAttendanceManagementSystem.service;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserCreateDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.mapper.UserMapper;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.Role;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.Impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private LeaveRepository leaveRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User mockUser;
    private User managerUser;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        managerUser = new User();
        managerUser.setId(10L);
        managerUser.setEmail("manager@test.com");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFullName("John Doe");
        mockUser.setEmail("john@example.com");
        mockUser.setRole(Role.EMPLOYEE);
    }

    @Test
    void testCreateUser_success() {
        UserCreateDTO dto = new UserCreateDTO("John Doe", "john@example.com", "password", Role.EMPLOYEE, 10L);

        when(userRepository.findById(10L)).thenReturn(Optional.of(managerUser));
        when(passwordEncoder.encode("password")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = userService.createUser(dto);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).save(any(User.class));
        verify(leaveBalanceRepository, times(1)).save(any(LeaveBalance.class));
    }

    @Test
    void testGetAllUsers_pagination() {
        when(userRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(mockUser)));

        Page<UserDTO> result = userService.getAllUsers(PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getFullName());
    }

    @Test
    void testGetUserById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getFullName());
    }

    @Test
    void testGetUserById_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserById(99L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testUpdateUser_success() {
        UserCreateDTO dto = new UserCreateDTO("Updated Name", "updated@example.com", "newPass", Role.MANAGER, null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = userService.updateUser(1L, dto);

        assertEquals("Updated Name", result.getFullName());
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testUpdateUser_managerAssigned() {
        UserCreateDTO dto = new UserCreateDTO("With Manager", "withmanager@example.com", null, Role.EMPLOYEE, 10L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.findById(10L)).thenReturn(Optional.of(managerUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = userService.updateUser(1L, dto);

        assertEquals(10L, result.getManager_id());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testDeleteUser_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(leaveBalanceRepository.findByUser(mockUser)).thenReturn(Optional.of(new LeaveBalance()));

        userService.deleteUser(1L);

        verify(leaveBalanceRepository, times(1)).delete(any(LeaveBalance.class));
        verify(leaveRepository, times(1)).deleteAllByUser(mockUser);
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void testDeleteUser_notFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(99L));
        assertEquals("User not found", ex.getMessage());
    }
}
