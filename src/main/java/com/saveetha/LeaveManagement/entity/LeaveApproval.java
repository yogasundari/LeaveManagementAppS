package com.saveetha.LeaveManagement.entity;

import com.saveetha.LeaveManagement.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "LeaveApproval")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id", nullable = false, unique = true)
    private Integer approvalId; // Primary Key

    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private LeaveRequest leaveRequest; // Foreign Key reference to LeaveRequest

    @ManyToOne
    @JoinColumn(name = "flow_level_id", nullable = false)
    private ApprovalFlowLevel approvalFlowLevel; // Foreign Key reference to ApprovalFlowLevel

    @ManyToOne
    @JoinColumn(name = "approver_id", nullable = false)
    private Employee approver; // Foreign Key reference to Employee (approver)

    @Column(columnDefinition = "TEXT" ,nullable = true)
    private String reason; // Optional approval/rejection reason

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus status = ApprovalStatus.PENDING; // Default status: Pending

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

    public Integer getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(Integer approvalId) {
        this.approvalId = approvalId;
    }

    public LeaveRequest getLeaveRequest() {
        return leaveRequest;
    }

    public void setLeaveRequest(LeaveRequest leaveRequest) {
        this.leaveRequest = leaveRequest;
    }

    public ApprovalFlowLevel getApprovalFlowLevel() {
        return approvalFlowLevel;
    }

    public void setApprovalFlowLevel(ApprovalFlowLevel approvalFlowLevel) {
        this.approvalFlowLevel = approvalFlowLevel;
    }

    public Employee getApprover() {
        return approver;
    }

    public void setApprover(Employee approver) {
        this.approver = approver;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalStatus status) {
        this.status = status;
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
}
