package com.godigit.LeaveAndAttendanceManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;

import java.util.Optional;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, Long> {
    Optional<LeaveBalance> findByUser(User user);
}
