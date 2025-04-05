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
    @MapsId("empId") // Maps composite key to Employee
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @MapsId("leaveTypeId") // Maps composite key to LeaveType
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

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public Integer getBalanceLeave() {
        return balanceLeave;
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setBalanceLeave(Integer balanceLeave) {
        this.balanceLeave = balanceLeave;
    }

    public Integer getCarryForwardLeave() {
        return carryForwardLeave;
    }

    public void setCarryForwardLeave(Integer carryForwardLeave) {
        this.carryForwardLeave = carryForwardLeave;
    }

    public EmployeeLeaveBalanceId getId() {
        return id;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public Integer getUsedLeaves() {
        return usedLeaves;
    }

    public void setUsedLeaves(Integer usedLeaves) {
        this.usedLeaves = usedLeaves;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setId(EmployeeLeaveBalanceId id) {
        this.id = id;
    }

    @Column(nullable = false)
    private int currentYear ;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
