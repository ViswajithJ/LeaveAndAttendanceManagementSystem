package com.godigit.LeaveAndAttendanceManagementSystem.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.godigit.LeaveAndAttendanceManagementSystem.config.CustomUserDetails;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceStatusDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.mapper.AttendanceMapper;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.Role;
import com.godigit.LeaveAndAttendanceManagementSystem.service.Impl.AttendanceServiceImpl;
import com.godigit.LeaveAndAttendanceManagementSystem.util.PermissionUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceServiceImpl attendanceService;
    private final PermissionUtil permissionUtil;

    @PostMapping("/punchin")
    // @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    // @PreAuthorize("#userId == principal.id or hasAnyRole('MANAGER','ADMIN')")
    public AttendanceResponseDTO punchIn(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return AttendanceMapper.toDto(attendanceService.punchIn(userId));
    }

    @PostMapping("/punchout")
    // @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    // @PreAuthorize("#userId == principal.id or hasAnyRole('MANAGER','ADMIN')")
    public AttendanceResponseDTO punchOut(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return AttendanceMapper.toDto(attendanceService.punchOut(userId));
    }

    @GetMapping("/employee/{userId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    public List<AttendanceResponseDTO> getEmployeeLogs(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long loggedInUserId = userDetails.getId();
        Role role = userDetails.getRole(); // assuming you expose role in CustomUserDetails

        permissionUtil.checkViewPermission(loggedInUserId, role, userId);

        return attendanceService.getMyAttendance(userId)
                .stream()
                .map(AttendanceMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/status/{userId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    // @PreAuthorize("#userId == principal.id or hasAnyRole('MANAGER','ADMIN')")
    public AttendanceStatusDTO getAttendanceStatus(@PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long loggedInUserId = userDetails.getId();
        Role role = userDetails.getRole(); // assuming you expose role in CustomUserDetails

        permissionUtil.checkViewPermission(loggedInUserId, role, userId);

        return attendanceService.getAttendanceStatus(userId);
    }

    // for managers/admins later
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public List<AttendanceResponseDTO> getAllLogs() {
        return attendanceService.getAllAttendance()
                .stream()
                .map(AttendanceMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<AttendanceResponseDTO> getTeamLogs(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User manager = userDetails.getUser();
        return attendanceService.getTeamAttendance(manager.getId())
                .stream()
                .map(AttendanceMapper::toDto)
                .collect(Collectors.toList());
    }
}
