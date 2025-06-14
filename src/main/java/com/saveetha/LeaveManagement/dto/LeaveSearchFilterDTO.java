package com.saveetha.LeaveManagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveSearchFilterDTO {
    private String empId;
    private String email;
    private String typeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String reason;
    private String keyword; // For general search across multiple fi
}