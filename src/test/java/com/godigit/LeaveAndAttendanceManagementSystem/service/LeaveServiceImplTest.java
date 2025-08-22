package com.godigit.LeaveAndAttendanceManagementSystem.service;


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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    }
}