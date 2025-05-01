package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.dto.EmployeeUpdateDTO;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    //  Update Employee API
    @PutMapping("/update/{empId}")
    @PreAuthorize("hasAnyAuthority('EMPLOYEE', 'ADMIN')")  // Restrict access
    public ResponseEntity<String> updateEmployee(
            @PathVariable String empId,
            @RequestBody EmployeeUpdateDTO employeeUpdateDTO) {

        boolean isUpdated = employeeService.updateEmployee(empId, employeeUpdateDTO);
        if (isUpdated) {
            System.out.println("Joining Date: " + employeeUpdateDTO.getJoiningDate());
            return ResponseEntity.ok("Employee updated successfully!");
        } else {
            return ResponseEntity.badRequest().body("Failed to update employee.");
        }
    }
    // Get all employees
    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    // Get employee by ID
    @GetMapping("/{empId}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String empId) {
        return employeeService.getEmployeeById(empId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // Soft delete - deactivate employee
    @PatchMapping("/deactivate/{empId}")
    public ResponseEntity<String> deactivateEmployee(@PathVariable String empId) {
        boolean result = employeeService.deactivateEmployee(empId);
        return result ? ResponseEntity.ok("Employee deactivated successfully")
                : ResponseEntity.notFound().build();
    }

    // Reactivate employee
    @PatchMapping("/activate/{empId}")
    public ResponseEntity<String> activateEmployee(@PathVariable String empId) {
        boolean result = employeeService.activateEmployee(empId);
        return result ? ResponseEntity.ok("Employee activated successfully")
                : ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{empId}")
    public ResponseEntity<String> deleteEmployee(@PathVariable String empId) {
        boolean result = employeeService.deleteEmployee(empId);
        return result ? ResponseEntity.ok("Employee deleted successfully")
                : ResponseEntity.notFound().build();
    }
}
