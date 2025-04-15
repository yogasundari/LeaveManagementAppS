package com.saveetha.LeaveManagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LeaveType")
@NoArgsConstructor
@AllArgsConstructor
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer leaveTypeId;

    @Column(nullable = false, length = 50)
    private String typeName;

    @Column(nullable = false)
    private Integer maxAllowedPerYear;

    @Column(nullable = true)
    private Integer maxAllowedPerMonth;

    @Column(nullable = true)
    private Integer minAllowedDays;

    private LocalDate academicYearStart;
    private LocalDate academicYearEnd;

    @Column(nullable = false)
    private Boolean canBeCarriedForward = false;

    private Integer maxCarryForward = 0;

    @Column(nullable = false)
    private Boolean active = true; // New field to indicate if leave type is active

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
    public LocalDate getAcademicYearEnd() {return academicYearEnd;}

    public void setAcademicYearEnd(LocalDate academicYearEnd) {
        this.academicYearEnd = academicYearEnd;

    }

    public Integer getMaxAllowedPerYear() {
        return maxAllowedPerYear;
    }

    public void setMaxAllowedPerYear(Integer maxAllowedPerYear) {
        this.maxAllowedPerYear = maxAllowedPerYear;
    }

    public Integer getLeaveTypeId() {
        return leaveTypeId;
    }

    public Integer getMinAllowedDays() {
        return minAllowedDays;
    }

    public void setMinAllowedDays(Integer minAllowedDays) {
        this.minAllowedDays = minAllowedDays;
    }

    public Integer getMaxCarryForward() {
        return maxCarryForward;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setMaxCarryForward(Integer maxCarryForward) {
        this.maxCarryForward = maxCarryForward;
    }

    public LocalDate getAcademicYearStart() {
        return academicYearStart;
    }

    public void setAcademicYearStart(LocalDate academicYearStart) {
        this.academicYearStart = academicYearStart;
    }

    public Integer getMaxAllowedPerMonth() {
        return maxAllowedPerMonth;
    }

    public Boolean getCanBeCarriedForward() {
        return canBeCarriedForward;
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

    public void setCanBeCarriedForward(Boolean canBeCarriedForward) {
        this.canBeCarriedForward = canBeCarriedForward;
    }

    public void setMaxAllowedPerMonth(Integer maxAllowedPerMonth) {
        this.maxAllowedPerMonth = maxAllowedPerMonth;
    }

    public void setLeaveTypeId(Integer leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
    }
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

}
//without authorization