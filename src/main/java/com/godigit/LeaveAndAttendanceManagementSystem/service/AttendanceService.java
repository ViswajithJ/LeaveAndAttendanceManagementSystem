package com.godigit.LeaveAndAttendanceManagementSystem.service;

import java.util.List;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceStatusDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.Role;

public interface AttendanceService {

    Attendance punchIn(Long userId);

    Attendance punchOut(Long userId);

    List<Attendance> getMyAttendance(Long userId);

    AttendanceStatusDTO getAttendanceStatus(Long userId);

    List<Attendance> getAllAttendance();
    
    List<Attendance> getTeamAttendance(Long managerId);

    Boolean isTeamMember(Long managerId, Long userId);

    void checkViewPermission(Long loggedInUserId, Role role, Long targetUserId);
}
