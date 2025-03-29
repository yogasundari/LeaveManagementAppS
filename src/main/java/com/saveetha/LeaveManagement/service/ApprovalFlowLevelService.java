package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.ApprovalFlowLevel;
import com.saveetha.LeaveManagement.repository.ApprovalFlowLevelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApprovalFlowLevelService {

    @Autowired
    private ApprovalFlowLevelRepository approvalFlowLevelRepository;

    // Get all approval flow levels
    public List<ApprovalFlowLevel> getAllApprovalFlowLevels() {
        return approvalFlowLevelRepository.findAll();
    }

    // Get approval levels by Approval Flow ID
    public List<ApprovalFlowLevel> getApprovalFlowLevelsByFlowId(Integer approvalFlowId) {
        return approvalFlowLevelRepository.findByApprovalFlowApprovalFlowId(approvalFlowId);
    }

    // Get a specific approval level by ID
    public Optional<ApprovalFlowLevel> getApprovalFlowLevelById(Integer id) {
        return approvalFlowLevelRepository.findById(id);
    }

    // Save a new approval flow level
    public ApprovalFlowLevel saveApprovalFlowLevel(ApprovalFlowLevel approvalFlowLevel) {
        return approvalFlowLevelRepository.save(approvalFlowLevel);
    }

    // Delete an approval flow level
    public void deleteApprovalFlowLevel(Integer id) {
        approvalFlowLevelRepository.deleteById(id);
    }
}
