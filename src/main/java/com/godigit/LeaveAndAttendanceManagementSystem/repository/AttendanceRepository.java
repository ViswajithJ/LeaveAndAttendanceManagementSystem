package com.godigit.LeaveAndAttendanceManagementSystem.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByUser(User user);

    Optional<Attendance> findByUserAndPunchOutTimeIsNull(User user);
    
    // @Query("SELECT a FROM Attendance a WHERE a.employee.team.manager.id = :managerId")
    // List<Attendance> findTeamAttendance(Long managerId);

    // @Query("SELECT a FROM Attendance a WHERE a.user.manager.id = :managerId")
    // List<Attendance> findByManagerId(Long managerId);
     @Query("SELECT a FROM Attendance a WHERE a.user.manager.id = :managerId")
    List<Attendance> findByManagerId(@Param("managerId") Long managerId);


    @Modifying
    @Transactional
    @Query("DELETE FROM Attendance a WHERE a.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}
