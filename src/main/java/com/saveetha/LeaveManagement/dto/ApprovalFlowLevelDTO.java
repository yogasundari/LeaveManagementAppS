package com.saveetha.LeaveManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApprovalFlowLevelDTO {

    private Integer approvalFlowId;
    private String approverId;
    private Integer sequence;
    private boolean active;  // ✅ Add this field

    // ✅ Constructor (optional)
    public ApprovalFlowLevelDTO() {
    }

    // ✅ Parameterized constructor (if needed)
    public ApprovalFlowLevelDTO(Integer approvalFlowId, String approverId, Integer sequence, boolean active) {
        this.approvalFlowId = approvalFlowId;
        this.approverId = approverId;
        this.sequence = sequence;
        this.active = active;
    }
}
