package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.LeaveRequestDTO;
import com.saveetha.LeaveManagement.entity.LeaveRequest;
import com.saveetha.LeaveManagement.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<String> createLeaveRequest(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        LeaveRequest leaveRequest = leaveRequestService.createLeaveRequest(leaveRequestDTO);
        return ResponseEntity.ok("Leave Request Created Successfully with ID: " + leaveRequest.getRequestId());
    }
}
// leave alteration not yet completed