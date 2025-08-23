package com.godigit.LeaveAndAttendanceManagementSystem.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceStatusDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;

public interface AttendanceService {

    Attendance punchIn(Long userId);

    Attendance punchOut(Long userId);

    List<Attendance> getMyAttendance(Long userId);

    AttendanceStatusDTO getAttendanceStatus(Long userId);

    Page<AttendanceResponseDTO> getAllAttendance(Pageable pageable);
    // Web socket

    List<Attendance> getTeamAttendance(Long managerId);

}
