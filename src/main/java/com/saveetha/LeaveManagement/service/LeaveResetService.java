package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalance;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.repository.EmployeeLeaveBalanceRepository;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import com.saveetha.LeaveManagement.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class LeaveResetService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private EmployeeLeaveBalanceRepository balanceRepository;

    public void resetAllEmployeeLeaveBalances(String newAcademicYear) {
        List<Employee> activeEmployees = employeeRepository.findByactiveTrue(); // Only active employees
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

        for (Employee employee : activeEmployees) {
            for (LeaveType leaveType : leaveTypes) {

                double maxLeave = leaveType.getMaxAllowedPerYear();

                // Either update existing balance or create new
                EmployeeLeaveBalance balance = balanceRepository
                        .findByEmployeeAndLeaveType(employee, leaveType)
                        .orElseGet(() -> new EmployeeLeaveBalance(
                                employee,
                                leaveType,
                                newAcademicYear,
                                maxLeave
                        ));

                // Reset fields
                balance.setUsedLeaves(0.0);
                balance.setCarryForwardLeave(0.0);
                balance.setCurrentYear(newAcademicYear);
                balance.setBalanceLeave(maxLeave); // Set balance = full allowed leave

                balanceRepository.save(balance);
            }
        }

        System.out.println("----------************---------Leave balances reset for academic year:-------------------******** " + newAcademicYear);
    }



}
