package com.godigit.LeaveAndAttendanceManagementSystem.service.Impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.LeaveAccrualService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveAccrualServiceImpl implements LeaveAccrualService {

    private final LeaveBalanceRepository leaveBalanceRepo;

    /**
     * Runs at 12:00 AM on the first day of every month.
     * Adds 1 leave to all users' leave balances.
     */
    @Scheduled(cron = "0 0 0 1 * *") // second minute hour day-of-month month day-of-week
    @Transactional
    public void accrueMonthlyLeave() {
        leaveBalanceRepo.findAll().forEach(balance -> {
            balance.setTotalLeaves(balance.getTotalLeaves() + 1);
            leaveBalanceRepo.save(balance);
        });
        System.out.println("Monthly leave accrual completed for all users.");
    }
}
