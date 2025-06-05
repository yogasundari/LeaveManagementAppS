package com.saveetha.LeaveManagement.controller;

import com.saveetha.LeaveManagement.entity.Department;
import com.saveetha.LeaveManagement.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    // Available to both ADMIN and EMPLOYEE roles
    // Employees typically see only active departments
    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllActiveDepartments());
    }

    // Available only to ADMIN - shows ALL departments including inactive ones
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Department>> getAllDepartmentsAdmin() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    // Available to both ADMIN and EMPLOYEE roles
    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    // Restricted to ADMIN role only
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Department> createDepartment(@Valid @RequestBody Department department) {
        return new ResponseEntity<>(departmentService.createDepartment(department), HttpStatus.CREATED);
    }

    // Restricted to ADMIN role only
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @Valid @RequestBody Department department) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, department));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<Department> toggleDepartmentStatus(@PathVariable Long id, @RequestParam boolean active) {
        Department updatedDept = departmentService.toggleDepartmentStatus(id, active);
        return ResponseEntity.ok(updatedDept);
    }

}
