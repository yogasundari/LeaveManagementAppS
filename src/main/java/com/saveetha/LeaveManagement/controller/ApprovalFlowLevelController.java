package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.entity.ApprovalFlowLevel;
import com.saveetha.LeaveManagement.service.ApprovalFlowLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/approval-flow-levels")
public class ApprovalFlowLevelController {

    @Autowired
    private ApprovalFlowLevelService approvalFlowLevelService;

    // Get all approval flow levels
    @GetMapping
    public ResponseEntity<List<ApprovalFlowLevel>> getAllApprovalFlowLevels() {
        return ResponseEntity.ok(approvalFlowLevelService.getAllApprovalFlowLevels());
    }

    // Get approval flow levels by Approval Flow ID
    @GetMapping("/flow/{flowId}")
    public ResponseEntity<List<ApprovalFlowLevel>> getApprovalFlowLevelsByFlowId(@PathVariable Integer flowId) {
        return ResponseEntity.ok(approvalFlowLevelService.getApprovalFlowLevelsByFlowId(flowId));
    }

    // Get a specific approval flow level by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalFlowLevel> getApprovalFlowLevelById(@PathVariable Integer id) {
        Optional<ApprovalFlowLevel> approvalFlowLevel = approvalFlowLevelService.getApprovalFlowLevelById(id);
        return approvalFlowLevel.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new approval flow level
    @PostMapping
    public ResponseEntity<ApprovalFlowLevel> createApprovalFlowLevel(@RequestBody ApprovalFlowLevel approvalFlowLevel) {
        return ResponseEntity.ok(approvalFlowLevelService.saveApprovalFlowLevel(approvalFlowLevel));
    }

    // Delete an approval flow level
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprovalFlowLevel(@PathVariable Integer id) {
        approvalFlowLevelService.deleteApprovalFlowLevel(id);
        return ResponseEntity.noContent().build();
    }
}
