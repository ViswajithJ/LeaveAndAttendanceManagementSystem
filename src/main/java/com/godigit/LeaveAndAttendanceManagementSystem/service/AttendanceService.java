package com.godigit.LeaveAndAttendanceManagementSystem.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.AttendanceStatusDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.exception.ResourceNotFoundException;
import com.godigit.LeaveAndAttendanceManagementSystem.model.Attendance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.AttendanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;

    public Attendance punchIn(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // check if already punched in and not punched out
        attendanceRepository.findByUserAndPunchOutTimeIsNull(user)
                .ifPresent(a -> { throw new IllegalStateException("Already punched in. Punch out first."); });

        Attendance attendance = Attendance.builder()
                .user(user)
                .punchInTime(LocalDateTime.now())
                .build();

        return attendanceRepository.save(attendance);
    }

    public Attendance punchOut(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Attendance attendance = attendanceRepository.findByUserAndPunchOutTimeIsNull(user)
                .orElseThrow(() -> new IllegalStateException("No active punch-in found"));

        attendance.setPunchOutTime(LocalDateTime.now());
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> getMyAttendance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return attendanceRepository.findByUser(user);
    }

    public AttendanceStatusDTO getAttendanceStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isPunchedIn = attendanceRepository.findByUserAndPunchOutTimeIsNull(user).isPresent();

        String status = isPunchedIn ? "PUNCHED IN" : "PUNCHED OUT";

        return new AttendanceStatusDTO(user.getId(), status);
    }


    // for managers/admin
    public List<Attendance> getAllAttendance() {
        return attendanceRepository.findAll();
    }
}
