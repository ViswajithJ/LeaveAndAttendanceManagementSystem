package com.godigit.LeaveAndAttendanceManagementSystem.service.Impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserCreateDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.mapper.UserMapper;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LeaveRepository leaveRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO createUser(UserCreateDTO dto) {
        log.info("Creating user with email: {}", dto.getEmail());

        User manager = null;
        if (dto.getManagerId() != null) {
            manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> {
                        log.error("Manager not found with ID: {}", dto.getManagerId());
                        return new RuntimeException("Manager not found");
                    });
            log.debug("Assigned manager: {}", manager.getEmail());

        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        // user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setManager(manager);

        User saved = userRepository.save(user);
        log.info("User created successfully with ID: {}", saved.getId());

        // Initialize leave balance for the new user
        LeaveBalance balance = new LeaveBalance();
        balance.setUser(saved);
        balance.setTotalLeaves(20); // default allocation, can be customized
        balance.setLeavesTaken(0);

        leaveBalanceRepository.save(balance);
        log.debug("Leave balance initialized for user ID: {}", saved.getId());

        return UserMapper.toDto(saved);
    }

    public List<UserDTO> getAllUsers() {
        log.info("Fetching all users");

        return userRepository.findAll().stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        log.info("Fetching user by ID: {}", id);

        return userRepository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });
    }

    @Override
    public UserDTO updateUser(Long id, UserCreateDTO dto) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            log.error("User not found with ID: {}", id);
            return new RuntimeException("User not found");
        });

        User manager = null;
        if (dto.getManagerId() != null) {
            manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> {
                        log.error("Manager not found with ID: {}", dto.getManagerId());
                        return new RuntimeException("Manager not found");
                    });
            log.debug("Reassigning manager to: {}", manager.getEmail());

        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            log.debug("Updating password for user ID: {}", id);

            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setRole(dto.getRole());
        user.setManager(manager);

        User updated = userRepository.save(user);
        log.info("User updated successfully with ID: {}", updated.getId());

        return UserMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.warn("Deleting user with ID: {}", id);

        User existing = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", id);
                    return new RuntimeException("User not found");
                });
        leaveBalanceRepository.findByUser(existing).ifPresent(leaveBalanceRepository::delete);
        log.debug("Deleted leave balance for user ID: {}", id);

        leaveRepository.deleteAllByUser(existing);
        log.debug("Deleted all leaves for user ID: {}", id);

        userRepository.delete(existing);
        log.info("User deleted successfully with ID: {}", id);

    }
}
