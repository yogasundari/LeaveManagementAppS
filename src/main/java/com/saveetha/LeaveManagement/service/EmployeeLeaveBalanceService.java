package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalance;
import com.saveetha.LeaveManagement.entity.LeaveRequest;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.enums.LeaveStatus;
import com.saveetha.LeaveManagement.repository.EmployeeLeaveBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;

@Service
public class EmployeeLeaveBalanceService {
    @Autowired
    private EmployeeLeaveBalanceRepository employeeLeaveBalanceRepository;

    public void deductLeaveBalanceOnApproval(LeaveRequest leaveRequest) {
        if (!leaveRequest.getStatus().equals(LeaveStatus.APPROVED)) {
            return; // Only proceed if the leave is approved
        }

        Employee employee = leaveRequest.getEmployee();
        LeaveType leaveType = leaveRequest.getLeaveType();

        EmployeeLeaveBalance balance = employeeLeaveBalanceRepository
                .findByEmployeeAndLeaveType(employee, leaveType)
                .orElseThrow(() -> new RuntimeException("Leave balance not found."));

        long totalDays = ChronoUnit.DAYS.between(leaveRequest.getStartDate(), leaveRequest.getEndDate()) + 1;

        if (balance.getBalanceLeave() < totalDays) {
            throw new RuntimeException("Insufficient leave balance. Required: " + totalDays +
                    ", Available: " + balance.getBalanceLeave());
        }

        balance.setBalanceLeave(balance.getBalanceLeave() - (int) totalDays);
        balance.setUsedLeaves(balance.getUsedLeaves() + (int) totalDays);

        employeeLeaveBalanceRepository.save(balance);
    }
}
