package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.LeaveAlterationDTO;
import com.saveetha.LeaveManagement.entity.LeaveAlteration;
import com.saveetha.LeaveManagement.service.LeaveAlterationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-alteration")
public class LeaveAlterationController {

    @Autowired
    private LeaveAlterationService alterationService;

    @PostMapping("/assign")
    public ResponseEntity<LeaveAlteration> assignAlteration(@RequestBody LeaveAlterationDTO dto) {
        return ResponseEntity.ok(alterationService.createAlteration(dto));
    }

    @PatchMapping("/approve/{id}")
    public ResponseEntity<String> approve(@PathVariable Integer id) {
        alterationService.approveAlteration(id);
        return ResponseEntity.ok("Alteration Approved");
    }
}
