package com.saveetha.LeaveManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalFlowLevelDTO {

    private Integer flowLevelId;  // Add this field

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

}
