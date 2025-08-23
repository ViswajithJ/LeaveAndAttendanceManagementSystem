package com.godigit.LeaveAndAttendanceManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;

import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByUser(User user);

    @Modifying
    @Query("UPDATE LeaveBalance lb SET lb.totalLeaves = lb.totalLeaves + 1")
    int incrementLeaveBalanceForAll();

    @Modifying
    @Query("UPDATE LeaveBalance lb SET lb.totalLeaves = 20, lb.leavesTaken = 0")
    int resetLeaveBalanceForAll();

}
