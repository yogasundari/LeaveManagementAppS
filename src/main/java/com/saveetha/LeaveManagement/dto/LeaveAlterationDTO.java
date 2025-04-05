package com.saveetha.LeaveManagement.dto;

import com.saveetha.LeaveManagement.enums.AlterationType;
import com.saveetha.LeaveManagement.enums.NotificationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveAlterationDTO {

    private Integer alterationId; // Primary Key
    private Integer requestId; // Foreign Key reference to LeaveRequest
    private String empId; // Employee requesting the alteration
    private AlterationType alterationType; // Enum for alteration type
    private String moodleActivityLink;
    private Integer replacementEmpId; // Replacement employee ID
    private NotificationStatus notificationStatus = NotificationStatus.PENDING; // Default: Pending
    private Boolean active = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

