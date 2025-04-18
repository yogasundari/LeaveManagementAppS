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

    public EmployeeLeaveBalanceId getId() {
        return id;
    }

    public void setId(EmployeeLeaveBalanceId id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public Double getUsedLeaves() {
        return usedLeaves;
    }

    public void setUsedLeaves(Double usedLeaves) {
        this.usedLeaves = usedLeaves;
    }

    public Double getBalanceLeave() {
        return balanceLeave;
    }

    public void setBalanceLeave(Double balanceLeave) {
        this.balanceLeave = balanceLeave;
    }

    public Double getCarryForwardLeave() {
        return carryForwardLeave;
    }

    public void setCarryForwardLeave(Double carryForwardLeave) {
        this.carryForwardLeave = carryForwardLeave;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(String currentYear) {
        this.currentYear = currentYear;
    }
}
