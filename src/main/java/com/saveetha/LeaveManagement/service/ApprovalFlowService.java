package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.ApprovalFlow;
import com.saveetha.LeaveManagement.repository.ApprovalFlowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApprovalFlowService {

    @Autowired
    private ApprovalFlowRepository approvalFlowRepository;

    // Create a new approval flow
    public ApprovalFlow createApprovalFlow(ApprovalFlow approvalFlow) {
        if (approvalFlowRepository.existsByName(approvalFlow.getName())) {
            throw new RuntimeException("Approval Flow with this name already exists.");
        }
        return approvalFlowRepository.save(approvalFlow);
    }

    // Get all approval flows
    public List<ApprovalFlow> getAllApprovalFlows() {
        return approvalFlowRepository.findAll();
    }

    // Get active approval flows
    public List<ApprovalFlow> getActiveApprovalFlows() {
        return approvalFlowRepository.findByActiveTrue();
    }

    // Get approval flow by ID
    public Optional<ApprovalFlow> getApprovalFlowById(Integer id) {
        return approvalFlowRepository.findById(id);
    }

    // Update an approval flow
    public ApprovalFlow updateApprovalFlow(Integer id, ApprovalFlow updatedApprovalFlow) {
        return approvalFlowRepository.findById(id).map(approvalFlow -> {
            approvalFlow.setName(updatedApprovalFlow.getName());
            approvalFlow.setFinalApprover(updatedApprovalFlow.getFinalApprover());
            approvalFlow.setActive(updatedApprovalFlow.isActive());
            return approvalFlowRepository.save(approvalFlow);
        }).orElseThrow(() -> new RuntimeException("Approval Flow not found"));
    }

    // Delete an approval flow
    public void deleteApprovalFlow(Integer id) {
        approvalFlowRepository.deleteById(id);
    }
}
