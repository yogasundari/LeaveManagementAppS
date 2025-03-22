package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.entity.ApprovalFlowLevel;
import com.saveetha.LeaveManagement.service.ApprovalFlowLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/approval-flow-levels")
public class ApprovalFlowLevelController {

    @Autowired
    private ApprovalFlowLevelService approvalFlowLevelService;

    @GetMapping
    public List<ApprovalFlowLevel> getAllApprovalFlowLevels() {
        return approvalFlowLevelService.getAllApprovalFlowLevels();
    }

    @GetMapping("/{id}")
    public Optional<ApprovalFlowLevel> getApprovalFlowLevelById(@PathVariable Integer id) {
        return approvalFlowLevelService.getApprovalFlowLevelById(id);
    }

    @GetMapping("/by-flow/{flowId}")
    public List<ApprovalFlowLevel> getApprovalFlowLevelsByFlowId(@PathVariable Integer flowId) {
        return approvalFlowLevelService.getApprovalFlowLevelsByFlowId(flowId);
    }

    @PostMapping("/create")
    public ApprovalFlowLevel createApprovalFlowLevel(@RequestBody ApprovalFlowLevel request) {
        return approvalFlowLevelService.createApprovalFlowLevel(
                request.getApprovalFlow().getApprovalFlowId(),
                request.getApprover().getEmpId(),
                request.getSequence()
        );
    }
    \



    @DeleteMapping("/{id}")
    public void deleteApprovalFlowLevel(@PathVariable Integer id) {
        approvalFlowLevelService.deleteApprovalFlowLevel(id);
    }
}
