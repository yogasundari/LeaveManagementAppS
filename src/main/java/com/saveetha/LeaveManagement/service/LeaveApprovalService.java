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

    public void initiateApprovalFlow(Integer leaveRequestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new RuntimeException("LeaveRequest not found"));

        Employee employee = leaveRequest.getEmployee();
        Integer approvalFlowId = employee.getApprovalFlow().getApprovalFlowId();

        // 👇 Get the sequence of approvers for this flow
        List<ApprovalFlowLevel> flowLevels = approvalFlowLevelRepository
                .findByApprovalFlow_ApprovalFlowIdOrderBySequenceAsc(approvalFlowId);
        System.out.println("Initiating approval flow for Leave Request ID: " + leaveRequestId);
        System.out.println("Approval Flow ID: " + approvalFlowId);
        System.out.println("Approvers in flow:");

        for (ApprovalFlowLevel level : flowLevels) {
            System.out.println(" - Approver: " + level.getApprover().getEmpId() +
                    ", Sequence: " + level.getSequence());
            LeaveApproval approval = new LeaveApproval();
            approval.setLeaveRequest(leaveRequest);
            approval.setApprovalFlowLevel(level);   // Which level of approval
            approval.setApprover(level.getApprover());  // Who is the approver
            approval.setStatus(ApprovalStatus.PENDING); // Initially pending
            leaveApprovalRepository.save(approval); //  This creates one row per level
        }
        System.out.println("Approval flow entries created for leave request.");
    }


    @Transactional
    public String processApproval(Integer approvalId, ApprovalStatus status, String reason) {
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
            return "Leave request rejected by " + approval.getApprover().getEmpId();
        }

        // If approved, check if current approver is final approver
        List<LeaveApproval> approvals = leaveApprovalRepository.findByLeaveRequest_RequestId(request.getRequestId());

        boolean allApproved = approvals.stream()
                .allMatch(a -> a.getStatus() == ApprovalStatus.APPROVED);

        if (allApproved) {
            int maxSequence = approvals.stream()
                    .mapToInt(a -> a.getApprovalFlowLevel().getSequence())
                    .max().orElse(0);

            LeaveApproval finalApproval = approvals.stream()
                    .filter(a -> a.getApprovalFlowLevel().getSequence() == maxSequence)
                    .findFirst()
                    .orElse(null);

            if (finalApproval != null) {
                Employee expectedFinalApprover = request.getEmployee().getApprovalFlow().getFinalApprover();

                if (finalApproval.getApprover().getEmpId().equals(expectedFinalApprover.getEmpId())) {
                    request.setStatus(LeaveStatus.APPROVED);
                    leaveRequestRepository.save(request);
                    return "Leave request approved by final approver";
                }
            }
        }

        return "Leave approved at current level and pending further approval";
    }


}

