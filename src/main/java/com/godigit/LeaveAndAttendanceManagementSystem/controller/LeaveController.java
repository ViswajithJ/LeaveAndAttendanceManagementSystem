package com.godigit.LeaveAndAttendanceManagementSystem.controller;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveRequestDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveBalanceDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.service.LeaveService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // Apply leave
    @PostMapping("/apply")
    public LeaveResponseDTO applyLeave(@RequestBody LeaveRequestDTO dto) {
        return leaveService.applyLeave(dto);
    }

    // Approve leave
    @PutMapping("/{id}/approve")
    public LeaveResponseDTO approveLeave(@PathVariable Long id) {
        return leaveService.approveLeave(id);
    }

    // Reject leave
    @PutMapping("/{id}/reject")
    public LeaveResponseDTO rejectLeave(@PathVariable Long id) {
        return leaveService.rejectLeave(id);
    }

    // Get leaves of specific employee
    @GetMapping("/employee/{userId}")
    public List<LeaveResponseDTO> getLeavesByEmployee(@PathVariable Long userId) {
        return leaveService.getLeavesByEmployee(userId);
    }

    // Get pending leaves
    @GetMapping("/pending")
    public List<LeaveResponseDTO> getPendingLeaves() {
        return leaveService.getPendingLeaves();
    }

    // Get all leaves
    @GetMapping("/all")
    public List<LeaveResponseDTO> getAllLeaves() {
        return leaveService.getAllLeaves();
    }

    // Get leave balance
    @GetMapping("/{userId}/balance")
    public LeaveBalanceDTO getLeaveBalance(@PathVariable Long userId) {
        return leaveService.getLeaveBalance(userId);
    }
}
