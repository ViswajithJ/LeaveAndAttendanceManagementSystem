package com.godigit.LeaveAndAttendanceManagementSystem.dto;

import lombok.Data;

@Data
public class AttendanceRequestDTO {
    private Long userId; // which user is punching in/out

    //not used now as just userId is passed which is availabe as a pathvariable/.
    //can be useful later if more data is to be passed in 
}
