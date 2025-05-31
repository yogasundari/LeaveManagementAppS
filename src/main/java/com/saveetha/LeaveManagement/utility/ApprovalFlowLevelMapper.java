package com.saveetha.LeaveManagement.utility;

import com.saveetha.LeaveManagement.dto.ApprovalFlowLevelDTO;
import com.saveetha.LeaveManagement.entity.ApprovalFlowLevel;

public class ApprovalFlowLevelMapper {

    public static ApprovalFlowLevelDTO toDTO(ApprovalFlowLevel level) {
        ApprovalFlowLevelDTO dto = new ApprovalFlowLevelDTO();
        dto.setFlowLevelId(level.getFlowLevelId());
        dto.setApprovalFlowId(level.getApprovalFlow().getApprovalFlowId());
        dto.setApproverId(level.getApprover().getEmpId());
        dto.setSequence(level.getSequence());
        dto.setActive(level.isActive());
        return dto;
    }
}

