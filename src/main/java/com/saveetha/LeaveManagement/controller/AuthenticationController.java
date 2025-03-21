package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.LoginRequest;
import com.saveetha.LeaveManagement.dto.RegisterRequest;
import com.saveetha.LeaveManagement.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Validate input fields
        if (request.getEmpId() == null || request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest().body("EmpId, Email, and Password are required.");
        }

        try {
            String token = authService.register(request);
            return ResponseEntity.ok("User registered successfully. Please log in.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
            return ResponseEntity.ok(token);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
