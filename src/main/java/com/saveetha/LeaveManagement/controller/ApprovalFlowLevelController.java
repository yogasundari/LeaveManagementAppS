package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.ApprovalFlowLevelDTO;
import com.saveetha.LeaveManagement.entity.ApprovalFlowLevel;
import com.saveetha.LeaveManagement.service.ApprovalFlowLevelService;
import com.saveetha.LeaveManagement.utility.ApprovalFlowLevelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import com.saveetha.LeaveManagement.dto.ApprovalFlowLevelDTO;
@RestController
@RequestMapping("/api/approval-flow-levels")
public class ApprovalFlowLevelController {

    @Autowired
    private ApprovalFlowLevelService approvalFlowLevelService;

    // Get all approval flow levels
    @GetMapping("/active")
    public ResponseEntity<List<ApprovalFlowLevelDTO>> getAllActiveApprovalFlowLevels() {
        return ResponseEntity.ok(approvalFlowLevelService.getAllActiveApprovalFlowLevels());
    }

    // Get approval flow levels by Approval Flow ID
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/flow/{flowId}")
    public ResponseEntity<List<ApprovalFlowLevel>> getApprovalFlowLevelsByFlowId(@PathVariable Integer flowId) {
        return ResponseEntity.ok(approvalFlowLevelService.getApprovalFlowLevelsByFlowId(flowId));
    }

    // Get a specific approval flow level by ID
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApprovalFlowLevelDTO> getApprovalFlowLevelById(@PathVariable Integer id) {
        Optional<ApprovalFlowLevel> approvalFlowLevel = approvalFlowLevelService.getApprovalFlowLevelById(id);

        return approvalFlowLevel
                .map(level -> ResponseEntity.ok(ApprovalFlowLevelMapper.toDTO(level)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // Create a new approval flow level
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public ResponseEntity<ApprovalFlowLevel> createApprovalFlowLevel(@RequestBody ApprovalFlowLevel approvalFlowLevel) {
        return ResponseEntity.ok(approvalFlowLevelService.saveApprovalFlowLevel(approvalFlowLevel));
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApprovalFlowLevelDTO> updateApprovalFlowLevel(@PathVariable Integer id,
                                                                        @RequestBody ApprovalFlowLevelDTO dto) {
        ApprovalFlowLevelDTO updatedDto = approvalFlowLevelService.updateApprovalFlowLevel(id, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/activate/{id}")
    public ResponseEntity<ApprovalFlowLevel> activateApprovalFlowLevel(@PathVariable Integer id) {
        ApprovalFlowLevel updatedLevel = approvalFlowLevelService.setActiveStatus(id, true);
        return ResponseEntity.ok(updatedLevel);
    }
    @PreAuthorize("hasAuthority('ADMIN')")
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<ApprovalFlowLevel> deactivateApprovalFlowLevel(@PathVariable Integer id) {
        ApprovalFlowLevel updatedLevel = approvalFlowLevelService.setActiveStatus(id, false);
        return ResponseEntity.ok(updatedLevel);
    }

}

