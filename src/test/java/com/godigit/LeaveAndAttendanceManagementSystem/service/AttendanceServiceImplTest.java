package com.godigit.LeaveAndAttendanceManagementSystem.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceStatusDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.exception.ResourceNotFoundException;
import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.AttendanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.Impl.AttendanceServiceImpl;

// @ExtendWith(MockitoExtension.class)
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
        mockUser.setId(1L);
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

    @Test
    void punchIn_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            attendanceService.punchIn(99L);
        });

        assertEquals("User not found", exception.getMessage());
        verify(attendanceRepository, times(0)).save(any(Attendance.class));
    }

    @Test
    void punchIn_ShouldNotAllowDuplicatePunchIn_WhenUserAlreadyPunchedIn() {
        // Arrange
        mockUser.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        Attendance existingAttendance = new Attendance();
        existingAttendance.setUser(mockUser);
        existingAttendance.setPunchInTime(java.time.LocalDateTime.now());
        when(attendanceRepository.findByUserAndPunchOutTimeIsNull(mockUser))
                .thenReturn(Optional.of(existingAttendance));

        // Act
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            attendanceService.punchIn(1L);
        });

        // Assert
        assertEquals("Already punched in. Punch out first.", exception.getMessage());
        verify(attendanceRepository, times(0)).save(any(Attendance.class));
    }

    // tests for punch out
    @Test
    void punchOut_ShouldSaveAttendance_WhenUserExistsAndHasActivePunchIn() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        Attendance existingAttendance = Attendance.builder()
                .user(mockUser)
                .punchInTime(LocalDateTime.now().minusHours(2))
                .build();
        when(attendanceRepository.findByUserAndPunchOutTimeIsNull(mockUser))
                .thenReturn(Optional.of(existingAttendance));
        when(attendanceRepository.save(existingAttendance)).thenReturn(existingAttendance);

        Attendance result = attendanceService.punchOut(1L);

        assertNotNull(result.getPunchOutTime());
        verify(attendanceRepository).save(existingAttendance);
    }

    @Test
    void punchOut_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> attendanceService.punchOut(99L));
    }

    @Test
    void punchOut_ShouldThrowException_WhenNoActivePunchIn() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(attendanceRepository.findByUserAndPunchOutTimeIsNull(mockUser))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> attendanceService.punchOut(1L));
    }

    // tests for getMyAttendance

    @Test
    void getMyAttendance_ShouldReturnAttendanceList_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        List<Attendance> mockList = List.of(new Attendance(), new Attendance());
        when(attendanceRepository.findByUser(mockUser)).thenReturn(mockList);

        List<Attendance> result = attendanceService.getMyAttendance(1L);

        assertEquals(2, result.size());
        verify(attendanceRepository).findByUser(mockUser);
    }

    @Test
    void getMyAttendance_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> attendanceService.getMyAttendance(99L));
    }

    // tests for getattendancestatus
    @Test
    void getAttendanceStatus_ShouldReturnPunchedIn_WhenActivePunchInExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(attendanceRepository.findByUserAndPunchOutTimeIsNull(mockUser))
                .thenReturn(Optional.of(new Attendance()));

        AttendanceStatusDTO result = attendanceService.getAttendanceStatus(1L);

        assertEquals("PUNCHED IN", result.getStatus());
    }

    @Test
    void getAttendanceStatus_ShouldReturnPunchedOut_WhenNoActivePunchIn() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(attendanceRepository.findByUserAndPunchOutTimeIsNull(mockUser))
                .thenReturn(Optional.empty());

        AttendanceStatusDTO result = attendanceService.getAttendanceStatus(1L);

        assertEquals("PUNCHED OUT", result.getStatus());
    }

    @Test
    void getAttendanceStatus_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> attendanceService.getAttendanceStatus(99L));
    }

    // test case for getallattendance
    @Test
    void getAllAttendance_ShouldReturnAllRecords() {
        // Arrange
        User user = new User();
        user.setId(1L); // whatever your User has

        Attendance att1 = new Attendance();
        att1.setUser(user);

        Attendance att2 = new Attendance();
        att2.setUser(user);

        Attendance att3 = new Attendance();
        att3.setUser(user);

        List<Attendance> mockList = List.of(att1, att2, att3);
        Pageable pageable = PageRequest.of(0, 10); // simulate first page, size 10

        Page<Attendance> mockPage = new PageImpl<>(mockList, pageable, mockList.size());

        when(attendanceRepository.findAll(pageable)).thenReturn(mockPage);

        // Act
        Page<AttendanceResponseDTO> result = attendanceService.getAllAttendance(pageable);

        // Assert
        assertEquals(3, result.getContent().size()); // content size = 3
        assertEquals(3, result.getTotalElements()); // total elements = 3
        verify(attendanceRepository).findAll(pageable);
    }

    // test cases for getteamattendance
    @Test
    void getTeamAttendance_ShouldReturnTeamRecords() {
        List<Attendance> mockList = List.of(new Attendance(), new Attendance());
        when(attendanceRepository.findByManagerId(10L)).thenReturn(mockList);

        List<Attendance> result = attendanceService.getTeamAttendance(10L);

        assertEquals(2, result.size());
        verify(attendanceRepository).findByManagerId(10L);
    }

}
