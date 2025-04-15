package com.saveetha.LeaveManagement.entity;

import com.saveetha.LeaveManagement.enums.LeaveDuration;
import com.saveetha.LeaveManagement.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "LeaveRequest")
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false, unique = true)
    private Integer requestId; // Primary Key

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee; // Foreign Key reference to Employee

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType; // Foreign Key reference to LeaveType

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;


    @Getter
    @Column(nullable = true)
    private LocalTime halfDayStartTime;
    @Getter
    @Column(nullable = true)
    private LocalTime halfDayEndTime;

    private LocalTime startTime;
    private LocalTime endTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = true)
    private LocalDate earnedDate;

    @Column(nullable = true)
    private String fileUpload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING; // Default status: Pending

    @Column(nullable = false)
    private Boolean active = true;

    @Getter
    private LocalDateTime createdAt;
    @Getter
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private LeaveDuration leaveDuration;       // FULL_DAY or HALF_DAY_MORNING / HALF_DAY_AFTERNOON

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getEarnedDate() {
        return earnedDate;
    }

    public void setEarnedDate(LocalDate earnedDate) {
        this.earnedDate = earnedDate;
    }


    public String getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(String fileUpload) {
        this.fileUpload = fileUpload;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public  double getLeaveDuration() {
        // Check if it's a full-day leave
        if (this.leaveDuration == LeaveDuration.FULL_DAY) {
            // Calculate the duration in days (start to end date)
            return ChronoUnit.DAYS.between(this.startDate, this.endDate) + 1; // Add 1 to include the start day
        }

        // Check if it's a half-day leave
        if (this.leaveDuration == LeaveDuration.HALF_DAY_MORNING || this.leaveDuration == LeaveDuration.HALF_DAY_AFTERNOON) {
            return 0.5; // Half-day leave is considered as 0.5 day
        }

        // Default return (if there's no valid duration type)
        return 0;
    }

    public void setHalfDayStartTime(LocalTime halfDayStartTime) {
        this.halfDayStartTime = halfDayStartTime;
    }

    public void setHalfDayEndTime(LocalTime halfDayEndTime) {
        this.halfDayEndTime = halfDayEndTime;
    }

    public void setLeaveDuration(LeaveDuration leaveDuration) {
        this.leaveDuration = leaveDuration;
    }
}



