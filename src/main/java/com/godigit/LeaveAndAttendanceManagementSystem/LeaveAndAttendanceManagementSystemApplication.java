package com.godigit.LeaveAndAttendanceManagementSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LeaveAndAttendanceManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeaveAndAttendanceManagementSystemApplication.class, args);
	}

}
