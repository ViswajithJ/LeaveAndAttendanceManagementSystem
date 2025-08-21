package com.godigit.LeaveAndAttendanceManagementSystem.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceStatusDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;
import com.godigit.LeaveAndAttendanceManagementSystem.service.AttendanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/punchin/{userId}")
    // @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    @PreAuthorize("#userId == principal.id or hasAnyRole('MANAGER','ADMIN')")
    public AttendanceResponseDTO punchIn(@PathVariable Long userId) {
        return mapToDto(attendanceService.punchIn(userId));
    }

    @PostMapping("/punchout/{userId}")
    // @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    @PreAuthorize("#userId == principal.id or hasAnyRole('MANAGER','ADMIN')")
    public AttendanceResponseDTO punchOut(@PathVariable Long userId) {
        return mapToDto(attendanceService.punchOut(userId));
    }

    @GetMapping("/mylogs/{userId}")
    // @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    @PreAuthorize("#userId == principal.id or hasAnyRole('MANAGER','ADMIN')")
    public List<AttendanceResponseDTO> getMyLogs(@PathVariable Long userId) {
        return attendanceService.getMyAttendance(userId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/status/{userId}")
    // @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER','ADMIN')")
    @PreAuthorize("#userId == principal.id or hasAnyRole('MANAGER','ADMIN')")
    public AttendanceStatusDTO getAttendanceStatus(@PathVariable Long userId) {
        return attendanceService.getAttendanceStatus(userId);
}

    // for managers/admins later
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<AttendanceResponseDTO> getAllLogs() {
        return attendanceService.getAllAttendance()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    // === DTO mapping kept here itself ===
    private AttendanceResponseDTO mapToDto(Attendance attendance) {
        return AttendanceResponseDTO.builder()
                .id(attendance.getId())
                .punchInTime(attendance.getPunchInTime())
                .punchOutTime(attendance.getPunchOutTime())
                .userId(attendance.getUser().getId())
                .build();
    }
}
