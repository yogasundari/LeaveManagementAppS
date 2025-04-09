package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.*;
import com.saveetha.LeaveManagement.enums.ApprovalStatus;
import com.saveetha.LeaveManagement.enums.LeaveStatus;
import com.saveetha.LeaveManagement.repository.ApprovalFlowLevelRepository;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import com.saveetha.LeaveManagement.repository.LeaveApprovalRepository;
import com.saveetha.LeaveManagement.repository.LeaveRequestRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveApprovalService {

    private final LeaveApprovalRepository leaveApprovalRepository;
    private final EmployeeRepository employeeRepository;
    private final ApprovalFlowLevelRepository approvalFlowLevelRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public void initiateApprovalFlow(Integer requestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("LeaveRequest not found"));

        Employee employee = leaveRequest.getEmployee();
        ApprovalFlow approvalFlow = employee.getApprovalFlow();

        List<ApprovalFlowLevel> flowLevels = approvalFlowLevelRepository
                .findByApprovalFlow_ApprovalFlowIdOrderBySequenceAsc(approvalFlow.getApprovalFlowId());

        for (ApprovalFlowLevel level : flowLevels) {
            LeaveApproval approval = new LeaveApproval();
            approval.setLeaveRequest(leaveRequest);
            approval.setApprovalFlowLevel(level);
            approval.setApprover(level.getApprover());
            approval.setStatus(ApprovalStatus.PENDING);
            leaveApprovalRepository.save(approval);
        }
    }


    @Transactional
    public void processApproval(Integer approvalId, ApprovalStatus status, String reason) {
        LeaveApproval approval = leaveApprovalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval not found"));

        if (approval.getStatus() != ApprovalStatus.PENDING)
            throw new RuntimeException("Already processed");

        approval.setStatus(status);
        approval.setReason(reason);
        approval.setUpdatedAt(LocalDateTime.now());
        leaveApprovalRepository.save(approval);

        LeaveRequest request = approval.getLeaveRequest();

        if (status == ApprovalStatus.REJECTED) {
            request.setStatus(LeaveStatus.REJECTED);
            leaveRequestRepository.save(request);
            return;
        }

        // If approved, check if current approver is final approver
        List<LeaveApproval> approvals = leaveApprovalRepository.findByLeaveRequest_RequestId(request.getRequestId());

        boolean allApproved = approvals.stream()
                .allMatch(a -> a.getStatus() == ApprovalStatus.APPROVED);

        if (allApproved) {
            // 1. Get the last ApprovalFlowLevel (highest sequence)
            int maxSequence = approvals.stream()
                    .mapToInt(a -> a.getApprovalFlowLevel().getSequence())
                    .max().orElse(0);

            LeaveApproval finalApproval = approvals.stream()
                    .filter(a -> a.getApprovalFlowLevel().getSequence() == maxSequence)
                    .findFirst()
                    .orElse(null);

            if (finalApproval != null) {
                Employee expectedFinalApprover = request.getEmployee().getApprovalFlow().getFinalApprover();

                // 2. Check if the current approval is done by final approver
                if (finalApproval.getApprover().getEmpId().equals(expectedFinalApprover.getEmpId())) {
                    request.setStatus(LeaveStatus.APPROVED);
                    leaveRequestRepository.save(request);
                }
            }
        }
    }


}

