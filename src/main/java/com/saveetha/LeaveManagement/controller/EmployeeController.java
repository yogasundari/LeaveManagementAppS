package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.EmployeeUpdateDTO;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    // ✅ Get all employees
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    // ✅ Get employee by ID
    @GetMapping("/{empId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String empId) {
        return employeeService.getEmployeeById(empId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
