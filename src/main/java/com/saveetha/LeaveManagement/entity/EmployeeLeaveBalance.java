package com.saveetha.LeaveManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "EmployeeLeaveBalance")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLeaveBalance {

    @EmbeddedId
    private EmployeeLeaveBalanceId id;

    @ManyToOne
    @MapsId("empId") // Maps composite key to Employee
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @MapsId("leaveTypeId") // Maps composite key to LeaveType
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(nullable = false)
    private Double usedLeaves =0.0;

    @Column(nullable = false)
    private Double balanceLeave =0.0;

    @Column(nullable = false)
    private Double carryForwardLeave=0.0;

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Column(nullable = false)
    private String currentYear ;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    // Default constructor (needed by JPA)
    public EmployeeLeaveBalance(Employee employee, LeaveType leaveType, String currentYear, Double proratedLeave) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.currentYear = currentYear;
        this.balanceLeave = proratedLeave;
        this.usedLeaves = 0.0;
        this.carryForwardLeave = 0.0;
        this.id = new EmployeeLeaveBalanceId(employee.getEmpId(), leaveType.getLeaveTypeId());
    }

}
