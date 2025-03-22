package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.ApprovalFlow;
import com.saveetha.LeaveManagement.entity.ApprovalFlowLevel;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.repository.ApprovalFlowLevelRepository;
import com.saveetha.LeaveManagement.repository.ApprovalFlowRepository;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApprovalFlowLevelService {

    @Autowired
    private ApprovalFlowLevelRepository approvalFlowLevelRepository;

    @Autowired
    private ApprovalFlowRepository approvalFlowRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<ApprovalFlowLevel> getAllApprovalFlowLevels() {
        return approvalFlowLevelRepository.findAll();
    }

    public List<ApprovalFlowLevel> getApprovalFlowLevelsByFlowId(Integer approvalFlowId) {
        return approvalFlowLevelRepository.findByApprovalFlowApprovalFlowId(approvalFlowId);
    }

    public Optional<ApprovalFlowLevel> getApprovalFlowLevelById(Integer id) {
        return approvalFlowLevelRepository.findById(id);
    }

    public ApprovalFlowLevel createApprovalFlowLevel(Integer approvalFlowId, String approverId, Integer sequence) {
        Optional<ApprovalFlow> approvalFlowOpt = approvalFlowRepository.findById(approvalFlowId);
        Optional<Employee> approverOpt = employeeRepository.findById(approverId);

        if (approvalFlowOpt.isEmpty() || approverOpt.isEmpty()) {
            throw new RuntimeException("Approval Flow or Approver not found!");
        }

        // Check if sequence already exists for this ApprovalFlow
        List<ApprovalFlowLevel> existingLevels = approvalFlowLevelRepository.findByApprovalFlowApprovalFlowId(approvalFlowId);
        boolean sequenceExists = existingLevels.stream().anyMatch(level -> level.getSequence().equals(sequence));
        if (sequenceExists) {
            throw new RuntimeException("Sequence number already exists in this approval flow!");
        }

        ApprovalFlowLevel approvalFlowLevel = new ApprovalFlowLevel();
        approvalFlowLevel.setApprovalFlow(approvalFlowOpt.get());
        approvalFlowLevel.setApprover(approverOpt.get());
        approvalFlowLevel.setSequence(sequence);
        approvalFlowLevel.setActive(true);

        return approvalFlowLevelRepository.save(approvalFlowLevel);
    }

    public void deleteApprovalFlowLevel(Integer id) {
        approvalFlowLevelRepository.deleteById(id);
    }
}
