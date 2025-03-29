package com.saveetha.LeaveManagement.security;

import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;

    public CustomUserDetailsService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String empId) throws UsernameNotFoundException {
        Employee employee = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with empId: " + empId));

        return new User(employee.getEmpId(), employee.getPassword(), Collections.emptyList());
    }
}
