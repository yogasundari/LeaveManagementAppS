package com.saveetha.LeaveManagement.repository;


import com.saveetha.LeaveManagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByDeptName(String deptName);
    List<Department> findByActive(boolean active);
}