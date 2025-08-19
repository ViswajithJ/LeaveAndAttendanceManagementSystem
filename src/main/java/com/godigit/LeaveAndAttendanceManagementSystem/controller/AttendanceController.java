package com.godigit.LeaveAndAttendanceManagementSystem.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;
import com.godigit.LeaveAndAttendanceManagementSystem.service.AttendanceService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/punchin/{userId}")
    public Attendance punchIn(@PathVariable Long userId) {
        return attendanceService.punchIn(userId);
    }

    @PostMapping("/punchout/{userId}")
    public Attendance punchOut(@PathVariable Long userId) {
        return attendanceService.punchOut(userId);
    }

    @GetMapping("/mylogs/{userId}")
    public List<Attendance> getMyLogs(@PathVariable Long userId) {
        return attendanceService.getMyAttendance(userId);
    }

    // for managers/admins later
    @GetMapping("/all")
    public List<Attendance> getAllLogs() {
        return attendanceService.getAllAttendance();
    }
}

