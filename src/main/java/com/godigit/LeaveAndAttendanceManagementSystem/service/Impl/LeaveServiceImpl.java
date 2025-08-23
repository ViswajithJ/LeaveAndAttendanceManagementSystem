package com.godigit.LeaveAndAttendanceManagementSystem.service.Impl;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.godigit.LeaveAndAttendanceManagementSystem.config.CustomUserDetails;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveBalanceDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveRequestDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.LeaveResponseDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.exception.ResourceNotFoundException;
import com.godigit.LeaveAndAttendanceManagementSystem.mapper.LeaveMapper;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveApplication;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.LeaveStatus;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.LeaveService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepo;
    private final UserRepository userRepo;
    private final LeaveBalanceRepository leaveBalanceRepo;

    public LeaveResponseDTO applyLeave(LeaveRequestDTO dto) {
        log.info("Applying leave for userId={} from {} to {}", dto.getUserId(), dto.getStartDate(), dto.getEndDate());

        LocalDate start = dto.getStartDate();
        LocalDate end = dto.getEndDate();

        if (start.isAfter(end)) {
            log.warn("Invalid leave request: start date {} after end date {}", start, end);
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        log.debug("Found user {}", user.getId());

        LeaveBalance balance = leaveBalanceRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));

        int daysRequested = (int) ChronoUnit.DAYS.between(start, end) + 1;
        if (daysRequested > balance.getLeavesRemaining()) {
            log.warn("User {} requested {} days, but only {} remain",
                    user.getId(), daysRequested, balance.getLeavesRemaining());
            throw new IllegalStateException("Insufficient leave balance. Remaining: " + balance.getLeavesRemaining());
        }

        // check overlapping leaves
        var conflicts = leaveRepo.findByUserAndStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                user, List.of(LeaveStatus.PENDING, LeaveStatus.APPROVED), end, start);
        if (!conflicts.isEmpty()) {
            log.warn("User {} has overlapping leave requests", user.getId());
            throw new IllegalStateException("Overlapping leave exists");
        }

        LeaveApplication leave = new LeaveApplication();
        leave.setUser(user);
        leave.setStartDate(start);
        leave.setEndDate(end);
        leave.setReason(dto.getReason());
        leave.setStatus(LeaveStatus.PENDING);
        log.info("Leave applied successfully with id={} for userId={}", leave.getId(), user.getId());

        return LeaveMapper.toResponseDto(leaveRepo.save(leave));
    }

    public LeaveResponseDTO approveLeave(Long leaveId, Long approverId, boolean isAdmin) {
        log.info("Approving leaveId={} by approverId={} (isAdmin={})", leaveId, approverId, isAdmin);

        LeaveApplication leave = getLeaveOrThrow(leaveId);
        if (leave.getStatus() != LeaveStatus.PENDING) {
            log.warn("Attempt to approve non-pending leaveId={} with status={}", leaveId, leave.getStatus());
            throw new IllegalStateException("Only PENDING leaves can be approved");
        }
        if (!isAdmin) {
            // Manager approval check
            Long employeeManagerId = leave.getUser().getManager().getId(); // employee’s manager
            if (!employeeManagerId.equals(approverId)) {
                log.warn("Unauthorized approval attempt by approverId={} for leaveId={}", approverId, leaveId);
                throw new RuntimeException("You are not authorized to approve this leave");
            }
        }
        leave.setStatus(LeaveStatus.APPROVED);

        LeaveApplication saved = leaveRepo.save(leave);

        // update leave balance
        LeaveBalance balance = leaveBalanceRepo.findByUser(leave.getUser())
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));
        int days = (int) ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
        balance.setLeavesTaken(balance.getLeavesTaken() + days);
        leaveBalanceRepo.save(balance);

        log.info("Leave id={} approved successfully by approverId={}", leaveId, approverId);
        return LeaveMapper.toResponseDto(saved);
    }

    public LeaveResponseDTO rejectLeave(Long leaveId, Long approverId, boolean isAdmin) {
        log.info("Rejecting leaveId={} by approverId={} (isAdmin={})", leaveId, approverId, isAdmin);
        LeaveApplication leave = getLeaveOrThrow(leaveId);
        if (leave.getStatus() != LeaveStatus.PENDING) {
            log.warn("Attempt to reject non-pending leaveId={} with status={}", leaveId, leave.getStatus());
            throw new IllegalStateException("Only PENDING leaves can be rejected");
        }
        if (!isAdmin) {
            // Manager approval check
            Long employeeManagerId = leave.getUser().getManager().getId(); // employee’s manager
            if (!employeeManagerId.equals(approverId)) {
                log.warn("Unauthorized rejection attempt by approverId={} for leaveId={}", approverId, leaveId);
                throw new RuntimeException("You are not authorized to approve this leave");
            }
        }
        leave.setStatus(LeaveStatus.REJECTED);
        log.info("Leave id={} rejected successfully", leaveId);
        return LeaveMapper.toResponseDto(leaveRepo.save(leave));
    }

    public List<LeaveResponseDTO> getLeavesByEmployee(Long userId) {
        log.info("Fetching leaves for userId={}", userId);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return leaveRepo.findByUser(user).stream()
                .map(LeaveMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<LeaveResponseDTO> getPendingLeaves() {
        log.info("Fetching all pending leaves");
        return leaveRepo.findByStatus(LeaveStatus.PENDING).stream()
                .map(LeaveMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<LeaveResponseDTO> getAllLeaves() {
        log.info("Fetching all leaves");
        return leaveRepo.findAll().stream()
                .map(LeaveMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public LeaveBalanceDTO getLeaveBalance(Long userId) {
        log.info("Fetching leave balance for userId={}", userId);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        LeaveBalance balance = leaveBalanceRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance not found"));
        log.debug("Leave balance for userId={} -> total={}, taken={}, remaining={}",
                userId, balance.getTotalLeaves(), balance.getLeavesTaken(), balance.getLeavesRemaining());

        return LeaveBalanceDTO.builder()
                .userId(user.getId())
                .totalLeaves(balance.getTotalLeaves())
                .leavesTaken(balance.getLeavesTaken())
                .leavesRemaining(balance.getLeavesRemaining())
                .build();
    }

    public LeaveApplication getLeaveOrThrow(Long leaveId) {
        log.debug("Fetching leave by id={}", leaveId);
        return leaveRepo.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found"));
    }

    public LeaveApplication revokeLeave(Long leaveId, Authentication authentication) {
        log.info("Revoking leaveId={} by logged-in user", leaveId);

        // Find leave by ID
        LeaveApplication leave = leaveRepo.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        // Ensure the logged-in user is the owner of this leave
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (!leave.getUser().getId().equals(userDetails.getId())) {
            log.warn("Unauthorized revoke attempt by userId={} for leaveId={}", userDetails.getId(), leaveId);
            throw new RuntimeException("You can only revoke your own leave requests");
        }

        // Allow revoke only if leave is still pending
        if (leave.getStatus() != LeaveStatus.PENDING) {
            log.warn("Revoke denied: leaveId={} not pending (status={})", leaveId, leave.getStatus());
            throw new RuntimeException("Only pending leave requests can be revoked");
        }

        // Mark as revoked
        leave.setStatus(LeaveStatus.REVOKED);
        log.info("Leave id={} revoked successfully", leaveId);

        return leaveRepo.save(leave);
    }
}
