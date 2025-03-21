package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.dto.LoginRequest;
import com.saveetha.LeaveManagement.dto.RegisterRequest;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.enums.Role;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import com.saveetha.LeaveManagement.security.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class AuthenticationService {
    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthenticationService(EmployeeRepository employeeRepository, JwtUtil jwtUtil) {
        this.employeeRepository = employeeRepository;
        this.jwtUtil = jwtUtil;
    }

    public String register(RegisterRequest request) {
        // Check if email already exists
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Create Employee entity
        Employee employee = new Employee();
        employee.setEmpId(request.getEmpId());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        String role = request.getRole() != null ? request.getRole().toString().toUpperCase() : "EMPLOYEE";
//        if (!role.startsWith("ROLE_")) {
//            role = "ROLE_" + role;
//        }
        employee.setRole(Role.fromString(role));

        employee.setActive(true);

        // Save to DB
        employeeRepository.save(employee);

        // Return JWT token instead of a plain message
        return jwtUtil.generateToken(employee.getEmpId(), employee.getRole().toString());
    }

    public String login(LoginRequest request) {
        Employee employee = employeeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            throw new RuntimeException("Invalid credentials!");
        }

        // Convert role to uppercase before mapping
        Role role = Role.fromString(employee.getRole().toString());

        // Update lastLogin timestamp
        employee.setLastLogin(new Timestamp(System.currentTimeMillis()));
        employeeRepository.save(employee);

        // Generate token using EmpID
        return jwtUtil.generateToken(employee.getEmpId(), employee.getRole().toString());
    }

}
