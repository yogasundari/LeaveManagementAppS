package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.LeaveRequestDTO;
import com.saveetha.LeaveManagement.entity.LeaveRequest;
import com.saveetha.LeaveManagement.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave-request")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping("/create-draft")
    public ResponseEntity<?> createDraft(@RequestBody LeaveRequestDTO dto) {
        LeaveRequest saved = leaveRequestService.createDraftLeaveRequest(dto);
        return ResponseEntity.ok("Draft Leave Request created with ID: " + saved.getRequestId());
    }
    @PostMapping("/submit/{id}")
    public ResponseEntity<String> submitLeaveRequest(@PathVariable("id") Integer requestId) {
        String response = leaveRequestService.submitLeaveRequest(requestId);
        return ResponseEntity.ok(response);
    }

}
