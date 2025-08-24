package com.godigit.LeaveAndAttendanceManagementSystem.service;

import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.Impl.LeaveAccrualServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class LeaveAccrualServiceImplTest {

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @InjectMocks
    private LeaveAccrualServiceImpl leaveAccrualService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAccrueMonthlyLeave_success() {
        when(leaveBalanceRepository.incrementLeaveBalanceForAll()).thenReturn(100);

        leaveAccrualService.accrueMonthlyLeave();

        verify(leaveBalanceRepository, times(1)).incrementLeaveBalanceForAll();
    }

    @Test
    void testResetYearlyLeave_success() {
        when(leaveBalanceRepository.resetLeaveBalanceForAll()).thenReturn(200);

        leaveAccrualService.resetYearlyLeave();

        verify(leaveBalanceRepository, times(1)).resetLeaveBalanceForAll();
    }
}
