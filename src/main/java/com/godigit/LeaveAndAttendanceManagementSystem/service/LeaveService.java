package com.godigit.LeaveAndAttendanceManagementSystem.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveBalanceDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveRequestDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveApplication;

public interface LeaveService {
    LeaveResponseDTO applyLeave(LeaveRequestDTO dto);

    LeaveResponseDTO approveLeave(Long leaveId, Long approverId, boolean isAdmin);

    LeaveResponseDTO rejectLeave(Long leaveId,Long approverId, boolean isAdmin);

    List<LeaveResponseDTO> getLeavesByEmployee(Long userId);

    List<LeaveResponseDTO> getPendingLeaves();

    List<LeaveResponseDTO> getAllLeaves();

    LeaveBalanceDTO getLeaveBalance(Long userId);

    LeaveApplication getLeaveOrThrow(Long leaveId);

    LeaveResponseDTO mapToResponseDTO(LeaveApplication leave);
    LeaveApplication revokeLeave(Long leaveId, Authentication authentication);
}
