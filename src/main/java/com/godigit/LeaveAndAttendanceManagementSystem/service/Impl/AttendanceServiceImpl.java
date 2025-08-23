package com.godigit.LeaveAndAttendanceManagementSystem.service.Impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceStatusDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.exception.ResourceNotFoundException;
import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.AttendanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.AttendanceService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public Attendance punchIn(Long userId) {
        log.info("Punch in attempt for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Punch in failed - User not found: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        // check if already punched in and not punched out
        attendanceRepository.findByUserAndPunchOutTimeIsNull(user)
                .ifPresent(a -> {
                    log.warn("User {} attempted to punch in without punching out", userId);
                    throw new IllegalStateException("Already punched in. Punch out first.");
                });

        Attendance attendance = Attendance.builder()
                .user(user)
                .punchInTime(LocalDateTime.now())
                .build();
        log.info("User {} punched in successfully at {}", userId, attendance.getPunchInTime());

        return attendanceRepository.save(attendance);
    }

    public Attendance punchOut(Long userId) {
        log.info("Punch out attempt for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Punch out failed - User not found: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        Attendance attendance = attendanceRepository.findByUserAndPunchOutTimeIsNull(user)
                .orElseThrow(() -> {
                    log.warn("Punch out failed - No active punch-in found for user {}", userId);
                    return new IllegalStateException("No active punch-in found");
                });

        attendance.setPunchOutTime(LocalDateTime.now());
        log.info("User {} punched out successfully at {}", userId, attendance.getPunchOutTime());

        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getMyAttendance(Long userId) {
        log.debug("Fetching attendance records for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Attendance fetch failed - User not found: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        log.info("Found attendance records for user {}", userId);

        return attendanceRepository.findByUser(user);
    }

    public AttendanceStatusDTO getAttendanceStatus(Long userId) {
        log.debug("Checking attendance status for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Status check failed - User not found: {}", userId);
                    return new ResourceNotFoundException("User not found");
                });

        boolean isPunchedIn = attendanceRepository.findByUserAndPunchOutTimeIsNull(user).isPresent();

        String status = isPunchedIn ? "PUNCHED IN" : "PUNCHED OUT";
        log.info("User {} attendance status: {}", userId, status);

        return new AttendanceStatusDTO(user.getId(), status);
    }

    // for managers/admin
    public List<Attendance> getAllAttendance() {
        log.debug("Fetching all attendance records");

        return attendanceRepository.findAll();
    }

    // for manager
    public List<Attendance> getTeamAttendance(Long managerId) {
        log.debug("Fetching team attendance for managerId={}", managerId);

        return attendanceRepository.findByManagerId(managerId);
    }
}
