package com.saveetha.LeaveManagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "approval_flow_level")
public class ApprovalFlowLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flowLevelId;

    @ManyToOne
    @JoinColumn(name = "approval_flow_id", nullable = true)
    private ApprovalFlow approvalFlow;

    @Column(nullable = false)
    private Integer sequence;

    @ManyToOne
    @JoinColumn(name = "approver_id")
    private Employee approver; // Reference to Employee who is the approver

    private Timestamp createdAt;
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }
    @Column(nullable = false)
    private boolean active = true; // Default: ApprovalflowLevel  is active
}
