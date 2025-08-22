package com.godigit.LeaveAndAttendanceManagementSystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;

import com.godigit.LeaveAndAttendanceManagementSystem.model.enums.LeaveStatus;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LeaveApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.PENDING;
}
