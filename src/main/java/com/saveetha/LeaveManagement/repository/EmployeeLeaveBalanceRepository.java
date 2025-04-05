package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalance;
import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalanceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeLeaveBalanceRepository extends JpaRepository<EmployeeLeaveBalance, EmployeeLeaveBalanceId> {
}
git add .