package com.saveetha.LeaveManagement.dto;

import com.saveetha.LeaveManagement.enums.ApprovalStatus;
import lombok.Data;

@Data
public class ApprovalRequestDTO {
    private ApprovalStatus status;
    private String reason;
}