package com.saveetha.LeaveManagement.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLeaveBalanceId implements Serializable {
    private String empId; // Matches emp_id
    private Integer leaveTypeId; // Matches leave_type_id
}
