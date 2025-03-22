package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.entity.ApprovalFlow;
import com.saveetha.LeaveManagement.service.ApprovalFlowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/approval-flows")
public class ApprovalFlowController {

    @Autowired
    private ApprovalFlowService approvalFlowService;

    // Create a new approval flow
    @PostMapping("/create")
    public ResponseEntity<ApprovalFlow> createApprovalFlow(@Valid @RequestBody ApprovalFlow approvalFlow) {
        return ResponseEntity.ok(approvalFlowService.createApprovalFlow(approvalFlow));
    }

    // Get all approval flows
    @GetMapping
    public ResponseEntity<List<ApprovalFlow>> getAllApprovalFlows() {
        return ResponseEntity.ok(approvalFlowService.getAllApprovalFlows());
    }

    // Get active approval flows
    @GetMapping("/active")
    public ResponseEntity<List<ApprovalFlow>> getActiveApprovalFlows() {
        return ResponseEntity.ok(approvalFlowService.getActiveApprovalFlows());
    }

    // Get approval flow by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalFlow> getApprovalFlowById(@PathVariable Integer id) {
        Optional<ApprovalFlow> approvalFlow = approvalFlowService.getApprovalFlowById(id);
        return approvalFlow.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update approval flow
    @PutMapping("/{id}")
    public ResponseEntity<ApprovalFlow> updateApprovalFlow(@PathVariable Integer id, @Valid @RequestBody ApprovalFlow approvalFlow) {
        return ResponseEntity.ok(approvalFlowService.updateApprovalFlow(id, approvalFlow));
    }

    // Delete approval flow
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprovalFlow(@PathVariable Integer id) {
        approvalFlowService.deleteApprovalFlow(id);
        return ResponseEntity.noContent().build();
    }
}
