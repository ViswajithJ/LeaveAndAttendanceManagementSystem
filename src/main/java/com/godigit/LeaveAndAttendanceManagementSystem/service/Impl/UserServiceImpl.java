package com.godigit.LeaveAndAttendanceManagementSystem.service.Impl;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserCreateDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.dto.UserDTO;
import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.service.UserService;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO createUser(UserCreateDTO dto) {
        User manager = null;
        if (dto.getManagerId() != null) {
            manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        // user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setManager(manager);

        User saved = userRepository.save(user);
        // Initialize leave balance for the new user
        LeaveBalance balance = new LeaveBalance();
        balance.setUser(saved);
        balance.setTotalLeaves(20); // default allocation, can be customized
        balance.setLeavesTaken(0);

        leaveBalanceRepository.save(balance);
        return mapToDTO(saved);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserDTO updateUser(Long id, UserCreateDTO dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        User manager = null;
        if (dto.getManagerId() != null) {
            manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Manager not found"));
        }

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setRole(dto.getRole());
        user.setManager(manager);


        User upadated=userRepository.save(user);
        return mapToDTO(upadated);
    }

    @Override
    public void deleteUser(Long id) {
        User existing =userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("User not found"));

        leaveBalanceRepository.findByUser(existing).ifPresent(leaveBalanceRepository::delete);
        userRepository.delete(existing);
    }

    public UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setManager_id(user.getManager() != null ? user.getManager().getId() : null);
        return dto;
    }
}
