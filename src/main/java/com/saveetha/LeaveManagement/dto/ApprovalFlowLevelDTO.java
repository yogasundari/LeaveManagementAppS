package com.saveetha.LeaveManagement.dto;

import com.saveetha.LeaveManagement.entity.ApprovalFlowLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalFlowLevelDTO {

    private Integer flowLevelId;
    private Integer approvalFlowId;
    private String approverId;
    private Integer sequence;
    private boolean active;

    public ApprovalFlowLevelDTO() {}

    public ApprovalFlowLevelDTO(Integer flowLevelId, Integer approvalFlowId, Integer sequence, String approverId, boolean active) {
        this.flowLevelId = flowLevelId;
        this.approvalFlowId = approvalFlowId;
        this.sequence = sequence;
        this.approverId = approverId;
        this.active = active;
    }

    //  Constructor that accepts entity
    public ApprovalFlowLevelDTO(ApprovalFlowLevel entity) {
        this.flowLevelId = entity.getFlowLevelId();
        this.approvalFlowId = entity.getApprovalFlow().getApprovalFlowId();
        this.sequence = entity.getSequence();
        this.approverId = entity.getApprover().getEmpId();  // or getName(), if needed
        this.active = entity.isActive();
    }
}
