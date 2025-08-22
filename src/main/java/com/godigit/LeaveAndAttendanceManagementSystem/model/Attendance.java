package com.godigit.LeaveAndAttendanceManagementSystem.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime punchInTime;

    private LocalDateTime punchOutTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
