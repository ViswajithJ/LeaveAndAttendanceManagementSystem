package com.godigit.LeaveAndAttendanceManagementSystem.service;

<<<<<<< Updated upstream


import com.godigit.LeaveAndAttendanceManagementSystem.config.CustomUserDetails;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveRequestDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.exception.ResourceNotFoundException;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveApplication;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.LeaveStatus;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.Impl.LeaveServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
=======
import com.godigit.LeaveAndAttendanceManagementSystem.config.CustomUserDetails;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveRequestDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveApplication;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.LeaveStatus;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.Impl.LeaveServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

>>>>>>> Stashed changes
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

<<<<<<< Updated upstream
class LeaveServiceImplTest {

    @Mock
    private LeaveRepository leaveRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepo;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LeaveServiceImpl leaveService;

    private User mockUser;
    private LeaveBalance mockBalance;
    private LeaveApplication mockLeave;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);

        mockBalance = new LeaveBalance();
        mockBalance.setUser(mockUser);
        mockBalance.setTotalLeaves(10);
        mockBalance.setLeavesTaken(0);

        mockLeave = new LeaveApplication();
        mockLeave.setId(100L);
        mockLeave.setUser(mockUser);
        mockLeave.setStartDate(LocalDate.now());
        mockLeave.setEndDate(LocalDate.now().plusDays(2));
        mockLeave.setReason("Vacation");
        mockLeave.setStatus(LeaveStatus.PENDING);
    }

    @Test
    void testApplyLeave_success() {
        LeaveRequestDTO request = new LeaveRequestDTO();
        request.setUserId(1L);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setReason("Vacation");

        when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser));
        when(leaveBalanceRepo.findByUser(mockUser)).thenReturn(Optional.of(mockBalance));
        when(leaveRepo.findByUserAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                any(), any(), any(), any())).thenReturn(List.of());
        when(leaveRepo.save(any(LeaveApplication.class))).thenReturn(mockLeave);

        LeaveResponseDTO response = leaveService.applyLeave(request);

        assertNotNull(response);
        assertEquals("Vacation", response.getReason());
        assertEquals(100L, response.getId());
        verify(leaveRepo, times(1)).save(any(LeaveApplication.class));
    }

    @Test
    void testApplyLeave_insufficientBalance() {
        LeaveRequestDTO request = new LeaveRequestDTO();
        request.setUserId(1L);
        request.setStartDate(LocalDate.now());
        request.setEndDate(LocalDate.now().plusDays(15)); // more than balance

        when(userRepo.findById(1L)).thenReturn(Optional.of(mockUser));
        when(leaveBalanceRepo.findByUser(mockUser)).thenReturn(Optional.of(mockBalance));

        assertThrows(IllegalStateException.class, () -> leaveService.applyLeave(request));
    }

    @Test
    void testApproveLeave_success() {
        mockUser.setManager(new User());
        mockUser.getManager().setId(2L);

        mockLeave.setStatus(LeaveStatus.PENDING);

        when(leaveRepo.findById(100L)).thenReturn(Optional.of(mockLeave));
        when(leaveBalanceRepo.findByUser(mockUser)).thenReturn(Optional.of(mockBalance));
        when(leaveRepo.save(mockLeave)).thenReturn(mockLeave);

        LeaveResponseDTO response = leaveService.approveLeave(100L, 2L, false);

        assertEquals(LeaveStatus.APPROVED, response.getStatus());
        assertEquals(3, mockBalance.getLeavesTaken()); // 2 + 1 day (since 1 day leave requested)
    }

    @Test
    void testRejectLeave_success() {
        mockUser.setManager(new User());
        mockUser.getManager().setId(2L);

        mockLeave.setStatus(LeaveStatus.PENDING);

        when(leaveRepo.findById(100L)).thenReturn(Optional.of(mockLeave));
        when(leaveRepo.save(mockLeave)).thenReturn(mockLeave);

        LeaveResponseDTO response = leaveService.rejectLeave(100L, 2L, false);

        assertEquals(LeaveStatus.REJECTED, response.getStatus());
    }

    @Test
    void testRevokeLeave_success() {
        CustomUserDetails userDetails = new CustomUserDetails(mockUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(leaveRepo.findById(100L)).thenReturn(Optional.of(mockLeave));
        when(leaveRepo.save(mockLeave)).thenReturn(mockLeave);

        LeaveApplication revoked = leaveService.revokeLeave(100L, authentication);

        assertEquals(LeaveStatus.REVOKED, revoked.getStatus());
    }

    @Test
    void testRevokeLeave_notOwner_shouldFail() {
        User anotherUser = new User();
        anotherUser.setId(99L);
        mockLeave.setUser(anotherUser);

        CustomUserDetails userDetails = new CustomUserDetails(mockUser);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(leaveRepo.findById(100L)).thenReturn(Optional.of(mockLeave));

        assertThrows(RuntimeException.class, () -> leaveService.revokeLeave(100L, authentication));
=======
@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {

    @Mock private LeaveRepository leaveRepo;
    @Mock private UserRepository userRepo;
    @Mock private LeaveBalanceRepository leaveBalanceRepo;

    @InjectMocks private LeaveServiceImpl leaveService;

    private User user;
    private LeaveBalance balance;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        balance = new LeaveBalance();
        balance.setUser(user);
        balance.setTotalLeaves(20);
        balance.setLeavesTaken(5);
    }

    @Test
    void applyLeave_shouldCreateLeaveRequest_whenValid() {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setUserId(1L);
        dto.setStartDate(LocalDate.of(2025, 8, 25));
        dto.setEndDate(LocalDate.of(2025, 8, 27));
        dto.setReason("Vacation");

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(leaveBalanceRepo.findByUser(user)).thenReturn(Optional.of(balance));
        when(leaveRepo.findByUserAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                eq(user), anyList(), any(), any())).thenReturn(List.of());

        LeaveApplication savedLeave = new LeaveApplication();
        savedLeave.setId(100L);
        savedLeave.setUser(user);
        savedLeave.setStartDate(dto.getStartDate());
        savedLeave.setEndDate(dto.getEndDate());
        savedLeave.setReason(dto.getReason());
        savedLeave.setStatus(LeaveStatus.PENDING);

        when(leaveRepo.save(any())).thenReturn(savedLeave);

        LeaveResponseDTO response = leaveService.applyLeave(dto);

        assertEquals(100L, response.getId());
        assertEquals(LeaveStatus.PENDING, response.getStatus());
    }

    @Test
    void approveLeave_shouldUpdateStatusAndBalance_whenAdminApproves() {
        LeaveApplication leave = new LeaveApplication();
        leave.setId(101L);
        leave.setUser(user);
        leave.setStartDate(LocalDate.of(2025, 8, 25));
        leave.setEndDate(LocalDate.of(2025, 8, 27));
        leave.setStatus(LeaveStatus.PENDING);

        when(leaveRepo.findById(101L)).thenReturn(Optional.of(leave));
        when(leaveBalanceRepo.findByUser(user)).thenReturn(Optional.of(balance));
        when(leaveRepo.save(any())).thenReturn(leave);

        LeaveResponseDTO response = leaveService.approveLeave(101L, 999L, true);

        assertEquals(LeaveStatus.APPROVED, response.getStatus());
        verify(leaveBalanceRepo).save(any());
    }

    @Test
    void rejectLeave_shouldThrowException_whenUnauthorizedManager() {
        LeaveApplication leave = new LeaveApplication();
        leave.setId(102L);
        leave.setUser(user);
        leave.setStatus(LeaveStatus.PENDING);

        User manager = new User();
        manager.setId(2L);
        user.setManager(manager);

        when(leaveRepo.findById(102L)).thenReturn(Optional.of(leave));

        assertThrows(RuntimeException.class, () -> {
            leaveService.rejectLeave(102L, 3L, false);
        });
    }

    @Test
    void revokeLeave_shouldUpdateStatus_whenUserIsOwnerAndPending() {
        LeaveApplication leave = new LeaveApplication();
        leave.setId(103L);
        leave.setUser(user);
        leave.setStatus(LeaveStatus.PENDING);

        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(leaveRepo.findById(103L)).thenReturn(Optional.of(leave));
        when(leaveRepo.save(any())).thenReturn(leave);

        LeaveApplication result = leaveService.revokeLeave(103L, auth);

        assertEquals(LeaveStatus.REVOKED, result.getStatus());
    }

    @Test
    void getLeaveBalance_shouldReturnCorrectBalance() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(leaveBalanceRepo.findByUser(user)).thenReturn(Optional.of(balance));

        var dto = leaveService.getLeaveBalance(1L);

        assertEquals(20, dto.getTotalLeaves());
        assertEquals(5, dto.getLeavesTaken());
        assertEquals(15, dto.getLeavesRemaining());
    }

    @Test
    void getLeavesByEmployee_shouldReturnLeaveList() {
        LeaveApplication leave = new LeaveApplication();
        leave.setId(104L);
        leave.setUser(user);
        leave.setStartDate(LocalDate.of(2025, 8, 25));
        leave.setEndDate(LocalDate.of(2025, 8, 27));
        leave.setStatus(LeaveStatus.APPROVED);

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(leaveRepo.findByUser(user)).thenReturn(List.of(leave));

        var leaves = leaveService.getLeavesByEmployee(1L);

        assertEquals(1, leaves.size());
        assertEquals(104L, leaves.get(0).getId());
    }

    @Test
    void getPendingLeaves_shouldReturnPendingList() {
        LeaveApplication leave = new LeaveApplication();
        leave.setId(105L);
        leave.setUser(user);
        leave.setStatus(LeaveStatus.PENDING);

        when(leaveRepo.findByStatus(LeaveStatus.PENDING)).thenReturn(List.of(leave));

        var result = leaveService.getPendingLeaves();

        assertEquals(1, result.size());
        assertEquals(LeaveStatus.PENDING, result.get(0).getStatus());
>>>>>>> Stashed changes
    }
}

