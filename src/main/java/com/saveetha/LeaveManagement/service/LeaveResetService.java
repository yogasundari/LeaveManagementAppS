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
        List<Employee> allEmployees = employeeRepository.findAll();
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

        for (Employee employee : allEmployees) {
            for (LeaveType leaveType : leaveTypes) {

                // Determine prorated leave logic
                double proratedLeave;
                LocalDate today = LocalDate.now();

                if (leaveType.getTypeName().equalsIgnoreCase("CL")) {
                    // If it's June, start with 1 CL, else full year allocation
                    proratedLeave = (today.getMonthValue() == 6) ? 1.0 : leaveType.getMaxAllowedPerYear();
                } else {
                    proratedLeave = leaveType.getMaxAllowedPerYear();
                }

                EmployeeLeaveBalance balance = balanceRepository
                        .findByEmployeeAndLeaveType(employee, leaveType)
                        .orElseGet(() -> new EmployeeLeaveBalance(employee, leaveType, newAcademicYear, proratedLeave));

                // If it already exists, update values
                balance.setUsedLeaves(0.0);
                balance.setCarryForwardLeave(0.0);
                balance.setCurrentYear(newAcademicYear);
                balance.setBalanceLeave(proratedLeave); // Reset balance

                balanceRepository.save(balance);
            }
        }

        System.out.println("Leave balances have been reset for the new academic year: " + newAcademicYear);
    }
    public void initializeLeaveBalance(Employee employee, List<LeaveType> leaveTypes, String academicYear) {
        LocalDate joiningDate = employee.getJoiningDate();

        for (LeaveType leaveType : leaveTypes) {
            double proratedLeave;

            LocalDate academicStart = leaveType.getAcademicYearStart();
            LocalDate academicEnd = leaveType.getAcademicYearEnd();

            if (joiningDate != null && joiningDate.isAfter(academicEnd)) {
                continue; // Joined after academic year ends, skip
            }

            if (joiningDate != null && joiningDate.isAfter(academicStart)) {
                // Mid-year joiner
                int monthsRemaining = 12 - joiningDate.getMonthValue() + 1;

                if (isCLOrPermission(leaveType)) {
                    int perMonth = leaveType.getMaxAllowedPerMonth() != null ? leaveType.getMaxAllowedPerMonth() : 0;
                    proratedLeave = monthsRemaining * perMonth;
                } else {
                    proratedLeave = 0.0;
                }
            } else {
                // Joined before or on academic year start => full leave
                proratedLeave = leaveType.getMaxAllowedPerYear();
            }
            // Debugging to ensure CL is calculated correctly
            if ("CL".equalsIgnoreCase(leaveType.getTypeName())) {
                System.out.println("Prorated CL leave for " + employee.getEmpName() + ": " + proratedLeave);
            }

            EmployeeLeaveBalance balance = balanceRepository
                    .findByEmployeeAndLeaveType(employee, leaveType)
                    .orElseGet(() -> new EmployeeLeaveBalance(employee, leaveType, academicYear, proratedLeave));

            balance.setUsedLeaves(0.0);
            balance.setCarryForwardLeave(0.0);
            balance.setCurrentYear(academicYear);
            balance.setBalanceLeave(proratedLeave);

            balanceRepository.save(balance);
        }

        System.out.println("✅ Leave balance initialized for Employee ID: " + employee.getEmpId());
    }
// initialization is not working for CL and working for permission-----------

    private boolean isCLOrPermission(LeaveType leaveType) {
        String type = leaveType.getTypeName().toUpperCase();
        return type.equals("CL") || type.equals("PERMISSION") || type.equals("LATE");
    }


}
