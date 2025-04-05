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
    private LeaveAlterationService leaveAlterationService;

    @PostMapping("/create")
    public ResponseEntity<String> createLeaveAlteration(@RequestBody LeaveAlterationDTO alterationDTO) {
        LeaveAlteration alteration = leaveAlterationService.createLeaveAlteration(alterationDTO);
        return ResponseEntity.ok("Leave Alteration created successfully with ID: " + alteration.getAlterationId());
    }
}
