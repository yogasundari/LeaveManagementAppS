package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.LeaveBalanceDto;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.security.JwtUtil;
import com.saveetha.LeaveManagement.service.LeaveBalanceService;
import com.saveetha.LeaveManagement.service.LeaveResetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LeaveBalanceController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @Autowired
    private LeaveResetService leaveResetService;

    @GetMapping("/leave-balance-all")
    public Object getLeaveBalance(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // remove "Bearer "
        String empId = jwtUtil.extractEmpId(token);
        String role = jwtUtil.extractRole(token);

        if ("ADMIN".equalsIgnoreCase(role)) {
            return leaveBalanceService.getAllEmployeesLeaveBalance();
        } else {
            Employee employee = leaveBalanceService.getEmployeeById(empId);
            return leaveBalanceService.getEmployeeLeaveBalance(employee);
        }
    }
    @GetMapping("/leave-balance/me")
    public Object getMyLeaveBalance(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String empId = jwtUtil.extractEmpId(token);

        Employee employee = leaveBalanceService.getEmployeeById(empId);
        return leaveBalanceService.getEmployeeLeaveBalance(employee);
    }
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping("/leave-balance/{empId}")
    public Map<String, LeaveBalanceDto> getLeaveBalanceByEmpId(@PathVariable String empId, HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // remove "Bearer "
        String role = jwtUtil.extractRole(token);

        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access Denied: Only Admin can access this endpoint");
        }

        Employee employee = leaveBalanceService.getEmployeeById(empId);
        return leaveBalanceService.getEmployeeLeaveBalance(employee);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @PatchMapping("/leave-balance/{empId}")
    public ResponseEntity<Map<String, LeaveBalanceDto>> updateLeaveBalanceByEmpId(
            @PathVariable String empId,
            @RequestBody Map<String, LeaveBalanceDto> leaveBalances,
            HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7); // remove "Bearer "
        String role = jwtUtil.extractRole(token);

        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access Denied: Only Admin can update leave balances.");
        }

        Map<String, LeaveBalanceDto> updated = leaveBalanceService.updateEmployeeLeaveBalances(empId, leaveBalances);
        return ResponseEntity.ok(updated);
    }
    @PostMapping("/reset-leave-balances")
    public ResponseEntity<Map<String, Object>> resetAllLeaveBalances() {
        Map<String, Object> response = new HashMap<>();

        try {
            String currentAcademicYear = getCurrentAcademicYear();
            leaveResetService.resetAllEmployeeLeaveBalances(currentAcademicYear);

            response.put("success", true);
            response.put("message", "Leave balances reset successfully for academic year: " + currentAcademicYear);
            response.put("academicYear", currentAcademicYear);
            response.put("resetDate", LocalDate.now().toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to reset leave balances: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());

            return ResponseEntity.internalServerError().body(response);
        }
    }
    private String getCurrentAcademicYear() {
        int currentYear = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        if (month >= 6) {
            return currentYear + "-" + (currentYear + 1);  // e.g., "2024-2025"
        } else {
            return (currentYear - 1) + "-" + currentYear;  // e.g., "2023-2024"
        }
    }
}