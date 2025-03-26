package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {
    List<LeaveRequest> findByEmployeeEmpId(Integer empId); // Find leave requests by employee ID
}
