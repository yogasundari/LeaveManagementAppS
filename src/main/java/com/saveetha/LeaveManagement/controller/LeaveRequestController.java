package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.LeaveRequestDto;
import com.saveetha.LeaveManagement.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping("/apply")
    public ResponseEntity<String> applyForLeave(@RequestBody LeaveRequestDto leaveRequestDto) {
        String response = leaveRequestService.applyForLeave(leaveRequestDto);
        return ResponseEntity.ok(response);
    }
}
