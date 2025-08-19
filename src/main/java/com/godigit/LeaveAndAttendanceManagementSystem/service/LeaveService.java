package com.godigit.LeaveAndAttendanceManagementSystem.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveBalanceDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveRequestDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.enums.LeaveStatus;
import com.godigit.LeaveAndAttendanceManagementSystem.exception.ResourceNotFoundException;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveApplication;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepo;
    private final UserRepository userRepo;
    private final LeaveBalanceRepository leaveBalanceRepo;

    public LeaveResponseDTO applyLeave(LeaveRequestDTO dto) {
        LocalDate start = dto.getStartDate();
        LocalDate end = dto.getEndDate();

        if (start.isAfter(end)) throw new IllegalArgumentException("Start date cannot be after end date");

        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LeaveBalance balance = leaveBalanceRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        int daysRequested = (int) ChronoUnit.DAYS.between(start, end) + 1;
        if (daysRequested > balance.getLeavesRemaining()) {
            throw new IllegalStateException("Insufficient leave balance. Remaining: " + balance.getLeavesRemaining());
        }

        // check overlapping leaves
        var conflicts = leaveRepo.findByUserAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                user, List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED), end, start
        );
        if (!conflicts.isEmpty()) throw new IllegalStateException("Overlapping leave exists");

        LeaveApplication leave = new LeaveApplication();
        leave.setUser(user);
        leave.setStartDate(start);
        leave.setEndDate(end);
        leave.setReason(dto.getReason());
        leave.setStatus(LeaveStatus.PENDING);

        return mapToResponseDTO(leaveRepo.save(leave));
    }

    public LeaveResponseDTO approveLeave(Long leaveId) {
        LeaveApplication leave = getLeaveOrThrow(leaveId);
        if (leave.getStatus() != LeaveStatus.PENDING)
            throw new IllegalStateException("Only PENDING leaves can be approved");

        leave.setStatus(LeaveStatus.APPROVED);
        LeaveApplication saved = leaveRepo.save(leave);

        // update leave balance
        LeaveBalance balance = leaveBalanceRepo.findByUser(leave.getUser())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));
        int days = (int) ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
        balance.setLeavesTaken(balance.getLeavesTaken() + days);
        leaveBalanceRepo.save(balance);

        return mapToResponseDTO(saved);
    }

    public LeaveResponseDTO rejectLeave(Long leaveId) {
        LeaveApplication leave = getLeaveOrThrow(leaveId);
        if (leave.getStatus() != LeaveStatus.PENDING)
            throw new IllegalStateException("Only PENDING leaves can be rejected");

        leave.setStatus(LeaveStatus.REJECTED);
        return mapToResponseDTO(leaveRepo.save(leave));
    }

    public List<LeaveResponseDTO> getLeavesByEmployee(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return leaveRepo.findByUser(user).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveResponseDTO> getPendingLeaves() {
        return leaveRepo.findByStatus(LeaveStatus.PENDING).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveResponseDTO> getAllLeaves() {
        return leaveRepo.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    public LeaveBalanceDTO getLeaveBalance(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        LeaveBalance balance = leaveBalanceRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        return LeaveBalanceDTO.builder()
                .userId(user.getId())
                .totalLeaves(balance.getTotalLeaves())
                .leavesTaken(balance.getLeavesTaken())
                .leavesRemaining(balance.getLeavesRemaining())
                .build();
    }

    private LeaveApplication getLeaveOrThrow(Long leaveId) {
        return leaveRepo.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));
    }

    private LeaveResponseDTO mapToResponseDTO(LeaveApplication leave) {
        return LeaveResponseDTO.builder()
                .id(leave.getId())
                .userId(leave.getUser().getId())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .build();
    }
}
