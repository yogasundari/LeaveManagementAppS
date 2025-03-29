package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.entity.LeaveAlteration;
import com.saveetha.LeaveManagement.service.LeaveAlterationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alterations")
public class LeaveAlterationController {

    @Autowired
    private LeaveAlterationService leaveAlterationService;

    @PostMapping
    public ResponseEntity<LeaveAlteration> requestAlteration(@RequestBody LeaveAlteration alteration) {
        return ResponseEntity.ok(leaveAlterationService.requestAlteration(alteration));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<LeaveAlteration> approveAlteration(@PathVariable Integer id) {
        return ResponseEntity.ok(leaveAlterationService.approveAlteration(id));
    }
}
