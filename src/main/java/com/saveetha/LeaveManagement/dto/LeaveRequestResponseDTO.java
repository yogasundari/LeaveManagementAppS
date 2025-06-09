package com.saveetha.LeaveManagement.dto;

import com.saveetha.LeaveManagement.entity.LeaveRequest;

import java.time.LocalDate;
import java.time.LocalTime;

public class LeaveRequestResponseDTO {
    private String empId;
    private String email;
    private String password;
    private Integer leaveTypeId;
    private String typeName;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason;
    private boolean isHalfDay;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    private String Status;
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    private LocalDate earnedDate;
    private String fileUpload;
    private Boolean hasClass;
    private String session;
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

    public Boolean getHasClass() {
        return hasClass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHasClass(Boolean hasClass) {
        this.hasClass = hasClass;
    }

    public boolean isHalfDay() {
        return isHalfDay;
    }

    public void setHalfDay(boolean halfDay) {
        isHalfDay = halfDay;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public static LeaveRequestResponseDTO fromEntity(LeaveRequest leaveRequest) {
        LeaveRequestResponseDTO dto = new LeaveRequestResponseDTO();

        if (leaveRequest.getLeaveType() != null) {
            dto.setLeaveTypeId(leaveRequest.getLeaveType().getLeaveTypeId());
            dto.setTypeName(leaveRequest.getLeaveType().getTypeName());
        }

        dto.setStartDate(leaveRequest.getStartDate());
        dto.setEndDate(leaveRequest.getEndDate());
        dto.setStartTime(leaveRequest.getStartTime());
        dto.setEndTime(leaveRequest.getEndTime());
        dto.setReason(leaveRequest.getReason());
        dto.setEarnedDate(leaveRequest.getEarnedDate());
        dto.setFileUpload(leaveRequest.getFileUpload());
        dto.setHalfDay(leaveRequest.isHalfDay());

        if (leaveRequest.getEmployee() != null) {
            dto.setEmpId(leaveRequest.getEmployee().getEmpId());
            dto.setEmail(leaveRequest.getEmployee().getEmail());
        }
        dto.setSession(leaveRequest.getSession());
        dto.setStatus(leaveRequest.getStatus().name());
        dto.setFileUpload(leaveRequest.getFileUpload());

        return dto;
    }


}
