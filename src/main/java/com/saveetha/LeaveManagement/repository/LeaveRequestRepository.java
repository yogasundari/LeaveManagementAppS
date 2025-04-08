package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.LeaveRequest;
import com.saveetha.LeaveManagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {

    // Get all leave requests by employee
    List<LeaveRequest> findByEmployeeEmpId(String empId);

    // Optional: Get all active requests
    List<LeaveRequest> findByActiveTrue();

    // You can add more methods as needed (e.g., find by date range, leave type, etc.)
}
