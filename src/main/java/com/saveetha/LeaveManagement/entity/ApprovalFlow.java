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
@Table(name = "approval_flow") // Explicit table name
public class ApprovalFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer approvalFlowId;

    @Column(nullable = false, length = 50)
    private String name;

    @ManyToOne
    @JoinColumn(name = "final_approver")
    private Employee finalApprover; // Final decision-maker (e.g., Principal)

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
    private boolean active = true; // Default: Approval flow  is active
}
