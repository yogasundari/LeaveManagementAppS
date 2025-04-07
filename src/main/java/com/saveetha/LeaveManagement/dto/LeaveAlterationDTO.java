package com.saveetha.LeaveManagement.dto;

import com.saveetha.LeaveManagement.enums.AlterationType;
import com.saveetha.LeaveManagement.enums.NotificationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveAlterationDTO {


    private Integer alterationId; // Primary Key
    private Integer requestId; // Foreign Key reference to LeaveRequest
    private String empId; // Employee requesting the alteration
    private AlterationType alterationType; // Enum for alteration type
    private String moodleActivityLink;
    private String replacementEmpId; // Replacement employee ID
    private NotificationStatus notificationStatus = NotificationStatus.PENDING; // Default: Pending
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public Integer getAlterationId() {
        return alterationId;
    }

    public void setAlterationId(Integer alterationId) {
        this.alterationId = alterationId;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public AlterationType getAlterationType() {
        return alterationType;
    }

    public void setAlterationType(AlterationType alterationType) {
        this.alterationType = alterationType;
    }

    public String getMoodleActivityLink() {
        return moodleActivityLink;
    }

    public void setMoodleActivityLink(String moodleActivityLink) {
        this.moodleActivityLink = moodleActivityLink;
    }

    public String getReplacementEmpId() {
        return replacementEmpId;
    }

    public void setReplacementEmpId(String replacementEmpId) {
        this.replacementEmpId = replacementEmpId;
    }

    public NotificationStatus getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(NotificationStatus notificationStatus) {
        this.notificationStatus = notificationStatus;
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
}

