package com.godigit.LeaveAndAttendanceManagementSystem.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class LeaveBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    private int totalLeaves;      // allocated leaves for the year
    private int leavesTaken;      // approved leaves

    public int getLeavesRemaining() {
        return totalLeaves - leavesTaken;
    }

}
