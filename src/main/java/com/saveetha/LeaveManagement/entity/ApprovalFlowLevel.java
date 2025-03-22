package com.saveetha.LeaveManagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "approval_flow_level")
public class ApprovalFlowLevel {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flowLevelId;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_flow_id", nullable = false)
    private ApprovalFlow approvalFlow;

    @Setter
    @Column(nullable = false)
    private Integer sequence;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private Employee approver;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Setter
    @Column(nullable = false)
    private boolean active = true;

    // Getters and Setters
    public Integer getFlowLevelId() {
        return flowLevelId;
    }

    public ApprovalFlow getApprovalFlow() {
        return approvalFlow;
    }

    public Integer getSequence() {
        return sequence;
    }

    public Employee getApprover() {
        return approver;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
