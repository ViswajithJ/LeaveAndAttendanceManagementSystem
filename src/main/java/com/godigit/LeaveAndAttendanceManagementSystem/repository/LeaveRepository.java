package com.godigit.LeaveAndAttendanceManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveApplication;
import com.godigit.LeaveAndAttendanceManagementSystem.enums.LeaveStatus;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByUser(User user);

    List<LeaveApplication> findByUserAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            User user, List<LeaveStatus> statuses, LocalDate end, LocalDate start);

    List<LeaveApplication> findByStatus(LeaveStatus status);
}
