package com.godigit.LeaveAndAttendanceManagementSystem.service;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.Impl.LeaveAccrualServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LeaveAccrualServiceImplTest {

    @Mock
    private LeaveBalanceRepository leaveBalanceRepo;

    @InjectMocks
    private LeaveAccrualServiceImpl leaveAccrualService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAccrueMonthlyLeave() {
        LeaveBalance user1 = new LeaveBalance();
        user1.setTotalLeaves(5);

        LeaveBalance user2 = new LeaveBalance();
        user2.setTotalLeaves(10);

        List<LeaveBalance> balances = Arrays.asList(user1, user2);

        when(leaveBalanceRepo.findAll()).thenReturn(balances);

        leaveAccrualService.accrueMonthlyLeave();

        // Verify that each user's leave was incremented and saved
        verify(leaveBalanceRepo, times(1)).save(user1);
        verify(leaveBalanceRepo, times(1)).save(user2);

        assert(user1.getTotalLeaves() == 6);
        assert(user2.getTotalLeaves() == 11);
    }
}

