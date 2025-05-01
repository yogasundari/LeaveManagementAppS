package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<String>> getMyNotifications() {
        String empId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<String> notifications = notificationService.getNotifications(empId);
        return ResponseEntity.ok(notifications);
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearMyNotifications() {
        String empId = SecurityContextHolder.getContext().getAuthentication().getName();
        notificationService.clearNotifications(empId);
        return ResponseEntity.ok("Notifications cleared.");
    }
}

