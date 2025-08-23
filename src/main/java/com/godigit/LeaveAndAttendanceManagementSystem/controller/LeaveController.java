package com.godigit.LeaveAndAttendanceManagementSystem.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.godigit.LeaveAndAttendanceManagementSystem.config.CustomUserDetails;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveBalanceDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveRequestDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.mapper.LeaveMapper;
import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.Role;
import com.godigit.LeaveAndAttendanceManagementSystem.service.Impl.LeaveServiceImpl;
import com.godigit.LeaveAndAttendanceManagementSystem.util.PermissionUtil;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveApplication;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveServiceImpl leaveService;
    private final PermissionUtil permissionUtil;

    // Apply leave
    @PostMapping("/apply")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    public LeaveResponseDTO applyLeave(@RequestBody LeaveRequestDTO dto) {
        return leaveService.applyLeave(dto);
    }

    @PutMapping("/{id}/revoke")
    @PreAuthorize("hasRole('EMPLOYEE', 'ADMIN', 'MANAGER')")
    public LeaveResponseDTO revokeLeave(@PathVariable Long id, Authentication authentication) {
        LeaveApplication leave = leaveService.revokeLeave(id, authentication);
        return LeaveMapper.toResponseDto(leave);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public LeaveResponseDTO approveLeave(@PathVariable Long id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long approverId = userDetails.getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return leaveService.approveLeave(id, approverId, isAdmin);
    }

    // Reject leave
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public LeaveResponseDTO rejectLeave(@PathVariable Long id, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long approverId = userDetails.getId();
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        return leaveService.rejectLeave(id, approverId, isAdmin);
    }

    // Get leaves of specific employee
    @GetMapping("/employee/{userId}")
    // @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    @PreAuthorize("#userId == principal.id or hasAnyRole('MANAGER','ADMIN')")
    public List<LeaveResponseDTO> getLeavesByEmployee(@PathVariable Long userId) {
        return leaveService.getLeavesByEmployee(userId);
    }

    // Get pending leaves
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<LeaveResponseDTO> getPendingLeaves() {
        return leaveService.getPendingLeaves();
    }

    // Get all leaves
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LeaveResponseDTO> getAllLeaves() {
        return leaveService.getAllLeaves();
    }

    // Get leave balance
    @GetMapping("/{userId}/balance")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    // @PreAuthorize("#userId == principal.id or hasAnyRole('MANAGER','ADMIN')")
    public LeaveBalanceDTO getLeaveBalance(@PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long loggedInUserId = userDetails.getId();
        Role role = userDetails.getRole(); // assuming you expose role in CustomUserDetails

        permissionUtil.checkViewPermission(loggedInUserId, role, userId);

        return leaveService.getLeaveBalance(userId);
    }

}
