package com.godigit.LeaveAndAttendanceManagementSystem.service;

import java.util.List;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveBalanceDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveRequestDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveApplication;

public interface LeaveService {
    LeaveResponseDTO applyLeave(LeaveRequestDTO dto);

    LeaveResponseDTO approveLeave(Long leaveId);

    LeaveResponseDTO rejectLeave(Long leaveId);

    List<LeaveResponseDTO> getLeavesByEmployee(Long userId);

    List<LeaveResponseDTO> getPendingLeaves();

    List<LeaveResponseDTO> getAllLeaves();

    LeaveBalanceDTO getLeaveBalance(Long userId);

    LeaveApplication getLeaveOrThrow(Long leaveId);

    LeaveResponseDTO mapToResponseDTO(LeaveApplication leave);
}
