package com.saveetha.LeaveManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "EmployeeLeaveBalance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeLeaveBalance {

    @EmbeddedId
    private EmployeeLeaveBalanceId id;

    @ManyToOne
    @MapsId("employee") // Maps composite key to Employee
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @MapsId("leaveType") // Maps composite key to LeaveType
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private Integer usedLeaves = 0;

    @Column(nullable = false)
    private Integer balanceLeave = 0;

    @Column(nullable = false)
    private Integer carryForwardLeave = 0;

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
