package com.godigit.LeaveAndAttendanceManagementSystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.godigit.LeaveAndAttendanceManagementSystem.model.LeaveBalance;
import com.godigit.LeaveAndAttendanceManagementSystem.model.User;
import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.Role;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.LeaveBalanceRepository;
import com.godigit.LeaveAndAttendanceManagementSystem.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FirstAdminCreator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final LeaveBalanceRepository leaveBalanceRepository;
    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setFullName("Default Admin");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            // admin.setPassword("admin123");
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);

            // Initialize leave balance
            LeaveBalance balance = new LeaveBalance();
            balance.setUser(admin);
            balance.setTotalLeaves(20); // default allocation
            balance.setLeavesTaken(0);

            leaveBalanceRepository.save(balance);
            System.out.println("Default admin created -> email: admin@example.com, password: admin123");
        }
    }
}
