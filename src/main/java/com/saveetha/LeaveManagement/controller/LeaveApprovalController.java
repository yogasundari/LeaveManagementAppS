package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.ApprovalRequestDTO;
import com.saveetha.LeaveManagement.enums.ApprovalStatus;
import com.saveetha.LeaveManagement.service.LeaveApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-approval")
@RequiredArgsConstructor
public class LeaveApprovalController {

    private final LeaveApprovalService leaveApprovalService;

    // Call this after submitting the leave request
    @PostMapping("/initiate/{leaveRequestId}")
    public ResponseEntity<String> initiateApprovalFlow(@PathVariable Integer leaveRequestId) {
        leaveApprovalService.initiateApprovalFlow(leaveRequestId);
        return ResponseEntity.ok("Approval flow initiated successfully!");
    }

    // Call this when an approver approves or rejects
    @PatchMapping("/process/{approvalId}")
    public ResponseEntity<String> processApproval(
            @PathVariable Integer approvalId,
            @RequestBody ApprovalRequestDTO approvalRequestDTOdto
    ) {
        String loggedInEmpId = SecurityContextHolder.getContext().getAuthentication().getName();

        String result = leaveApprovalService.processApproval(
                approvalId,  // The ID of the approval record
                approvalRequestDTOdto.getStatus(),  // The approval status (APPROVED, REJECTED)
                approvalRequestDTOdto.getReason(),  // The reason for the approval or rejection
                loggedInEmpId  // The employee ID of the logged-in approver
        );


        return ResponseEntity.ok(result);
    }
}
