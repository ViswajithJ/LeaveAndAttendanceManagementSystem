package com.godigit.LeaveAndAttendanceManagementSystem.service.Impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.LeaveAccrualService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.info("Starting monthly leave accrual job...");

        int updatedRows = leaveBalanceRepo.incrementLeaveBalanceForAll();

        log.info("Monthly leave accrual completed successfully. {} leave balances updated.", updatedRows);

    }

    @Scheduled(cron = "0 0 0 1 1 *") // At midnight on January 1st every year
    @Transactional
    public void resetYearlyLeave() {
        log.info("Starting yearly leave reset job...");

        int updatedRows = leaveBalanceRepo.resetLeaveBalanceForAll();

        log.info("Yearly leave reset completed. {} leave balances reset to default.", updatedRows);
    }
}