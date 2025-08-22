package com.godigit.LeaveAndAttendanceManagementSystem.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.godigit.LeaveAndAttendanceManagementSystem.model.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);


}
