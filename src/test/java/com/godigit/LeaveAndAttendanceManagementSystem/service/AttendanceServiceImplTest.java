package com.godigit.LeaveAndAttendanceManagementSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.AttendanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.Impl.AttendanceServiceImpl;

@ExtendWith(MockitoExtension.class)
public class AttendanceServiceImplTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // initializes @Mock and @InjectMocks
        mockUser = new User();
        // mockUser.setId(1L);
    }

    @Test
    void punchIn_ShouldSaveAttendance_WhenUserExistsAndNotPunchedIn() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(attendanceRepository.findByUserAndPunchOutTimeIsNull(mockUser))
                .thenReturn(Optional.empty());

        Attendance savedAttendance = new Attendance();
        savedAttendance.setUser(mockUser);
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(savedAttendance);

        // Act
        Attendance result = attendanceService.punchIn(1L);

        // Assert
        assertNotNull(result);
        assertEquals(mockUser, result.getUser());
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

}
