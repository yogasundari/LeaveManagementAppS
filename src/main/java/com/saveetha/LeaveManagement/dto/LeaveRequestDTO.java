package com.saveetha.LeaveManagement.dto;

import com.saveetha.LeaveManagement.entity.LeaveRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class LeaveRequestDTO {

    private String empId;
    private Integer leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
    private LocalDate earnedDate;
    private String fileUpload;
    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public Integer getLeaveTypeId() {
        return leaveTypeId;
    }

    public void setLeaveTypeId(Integer leaveTypeId) {
        this.leaveTypeId = leaveTypeId;
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
    public static LeaveRequestDTO fromEntity(LeaveRequest leaveRequest) {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setLeaveTypeId(leaveRequest.getLeaveType().getLeaveTypeId());  // Assuming LeaveRequest has a LeaveType entity with an ID
        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setStartTime(leaveRequest.getStartTime());
        dto.setEndTime(leaveRequest.getEndTime());
        dto.setReason(leaveRequest.getReason());
        dto.setEarnedDate(leaveRequest.getEarnedDate());
        dto.setFileUpload(leaveRequest.getFileUpload());
        // If class periods are not directly stored in the entity, we can remove this or handle it separately
        return dto;
    }

}
