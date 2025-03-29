package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.EmployeeUpdateDTO;
import com.saveetha.LeaveManagement.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ✅ Update Employee API
    @PutMapping("/update/{empId}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'ADMIN')")  // Restrict access
    public ResponseEntity<String> updateEmployee(
            @PathVariable String empId,
            @RequestBody EmployeeUpdateDTO employeeUpdateDTO) {

        boolean isUpdated = employeeService.updateEmployee(empId, employeeUpdateDTO);
        if (isUpdated) {
            return ResponseEntity.ok("Employee updated successfully!");
        } else {
            return ResponseEntity.badRequest().body("Failed to update employee.");
        }
    }
}
