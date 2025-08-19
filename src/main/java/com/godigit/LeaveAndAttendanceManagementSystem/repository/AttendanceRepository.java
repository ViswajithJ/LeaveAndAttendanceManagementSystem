package com.godigit.LeaveAndAttendanceManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUser(User user);

    Optional<Attendance> findByUserAndPunchOutTimeIsNull(User user);
}
