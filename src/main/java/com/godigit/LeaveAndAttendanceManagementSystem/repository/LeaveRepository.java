package com.godigit.LeaveAndAttendanceManagementSystem.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveApplication;
import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.LeaveStatus;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByUser(User user);

    List<LeaveApplication> findByUserAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            User user, List<LeaveStatus> statuses, LocalDate end, LocalDate start);

    List<LeaveApplication> findByStatus(LeaveStatus status);

    @Modifying
    @Transactional
    @Query("DELETE FROM LeaveApplication l WHERE l.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);


}
