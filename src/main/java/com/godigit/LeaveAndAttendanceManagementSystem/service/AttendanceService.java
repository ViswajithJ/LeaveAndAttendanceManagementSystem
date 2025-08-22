package com.godigit.LeaveAndAttendanceManagementSystem.service;

import java.util.List;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceStatusDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;

public interface AttendanceService {

    Attendance punchIn(Long userId);

    Attendance punchOut(Long userId);

    List<Attendance> getMyAttendance(Long userId);

    AttendanceStatusDTO getAttendanceStatus(Long userId);

    List<Attendance> getAllAttendance();

    //Web socket

    User findUserById(Long userId);  // Must return User
    void saveAttendance(Attendance attendance);

    List<Attendance> getTeamAttendance(Long managerId);

}
