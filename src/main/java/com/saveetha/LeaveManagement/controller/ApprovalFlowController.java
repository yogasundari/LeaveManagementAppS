package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.ApprovalFlowUpdateDTO;
import com.saveetha.LeaveManagement.entity.ApprovalFlow;
import com.saveetha.LeaveManagement.service.ApprovalFlowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/approval-flows")
public class ApprovalFlowController {

    @Autowired
    private ApprovalFlowService approvalFlowService;

    // Create a new approval flow
    @PreAuthorize("hasAuthority('ADMIN')")
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
    @PreAuthorize("hasAuthority('ADMIN')")
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

    //  Update approval flow using DTO
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApprovalFlow> updateApprovalFlow(@PathVariable Integer id,
                                                           @Valid @RequestBody ApprovalFlowUpdateDTO dto) {
        return ResponseEntity.ok(approvalFlowService.updateApprovalFlow(id, dto));
    }

    // Delete approval flow
    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApprovalFlow(@PathVariable Integer id) {
        approvalFlowService.deleteApprovalFlow(id);
        return ResponseEntity.noContent().build();
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/activate/{id}")
    public ResponseEntity<ApprovalFlow> activateApprovalFlow(@PathVariable Integer id) {
        ApprovalFlowUpdateDTO dto = new ApprovalFlowUpdateDTO();
        dto.setActive(true);
        return ResponseEntity.ok(approvalFlowService.updateApprovalFlow(id, dto));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<ApprovalFlow> deactivateApprovalFlow(@PathVariable Integer id) {
        ApprovalFlowUpdateDTO dto = new ApprovalFlowUpdateDTO();
        dto.setActive(false);
        return ResponseEntity.ok(approvalFlowService.updateApprovalFlow(id, dto));
    }




}
