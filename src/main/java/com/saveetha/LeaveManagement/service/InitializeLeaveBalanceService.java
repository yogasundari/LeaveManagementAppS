package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalance;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.repository.EmployeeLeaveBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

@Service
public class InitializeLeaveBalanceService {

    @Autowired
    private final EmployeeLeaveBalanceRepository balanceRepository;

    public InitializeLeaveBalanceService(EmployeeLeaveBalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public void initializeLeaveBalance(Employee employee, List<LeaveType> leaveTypes, String academicYear) {
        LocalDate joiningDate = employee.getJoiningDate();

        if (joiningDate == null) {
            System.out.println("Joining date is null for employee: " + employee.getEmpId());
            return;
        }

        for (LeaveType leaveType : leaveTypes) {
            LocalDate academicStart = leaveType.getAcademicYearStart(); // May 25 of current year
            LocalDate academicEnd = leaveType.getAcademicYearEnd(); // May 24 of next year

            // Skip if employee joined after academic year ends
            if (joiningDate.isAfter(academicEnd)) {
                continue;
            }

            double balance = calculateLeaveBalance(employee, leaveType, joiningDate, academicStart, academicEnd);

            EmployeeLeaveBalance leaveBalance = balanceRepository
                    .findByEmployeeAndLeaveType(employee, leaveType)
                    .orElseGet(() -> new EmployeeLeaveBalance(employee, leaveType, academicYear, balance));

            leaveBalance.setUsedLeaves(0.0);
            leaveBalance.setCarryForwardLeave(0.0);
            leaveBalance.setCurrentYear(academicYear);
            leaveBalance.setBalanceLeave(balance);

            balanceRepository.save(leaveBalance);

            System.out.println("Initialized " + leaveType.getTypeName() + " for " + employee.getEmpId() + ": " + balance);
        }

        System.out.println("Leave balance initialized for employee: " + employee.getEmpId());
    }

    private double calculateLeaveBalance(Employee employee, LeaveType leaveType, LocalDate joiningDate,
                                         LocalDate academicStart, LocalDate academicEnd) {
        String typeName = leaveType.getTypeName().toUpperCase();

        // Check if employee joined before academic year starts
        if (joiningDate.isBefore(academicStart)) {
            return calculateBalanceForEarlyJoiner(employee, leaveType, joiningDate, academicStart);
        } else {
            // Employee joined after academic year started
            return calculateBalanceForLateJoiner(employee, leaveType, joiningDate, academicStart, academicEnd);
        }
    }

    private double calculateBalanceForEarlyJoiner(Employee employee, LeaveType leaveType,
                                                  LocalDate joiningDate, LocalDate academicStart) {
        String typeName = leaveType.getTypeName().toUpperCase();
        LocalDate today = LocalDate.now();

        // Check if joining year is before academic year
        int joiningYear = joiningDate.getYear();
        int academicYear = academicStart.getYear();

        if (joiningYear < academicYear) {
            // Joined in previous year - full balance
            switch (typeName) {
                case "CL": return 12.0;
                case "PERMISSION": return 24.0;
                case "LATE": return 24.0;
                case "ML": return isMLELEligible(joiningDate, today) ? 6.0 : 0.0;
                case "EL": return isMLELEligible(joiningDate, today) ? 12.0 : 0.0;
                default: return leaveType.getMaxAllowedPerYear();
            }
        } else {
            // Same year but before academic month starts
            switch (typeName) {
                case "CL": return 12.0;
                case "PERMISSION": return 24.0;
                case "LATE": return 24.0;
                case "ML": return isMLELEligible(joiningDate, today) ? 6.0 : 0.0;
                case "EL": return isMLELEligible(joiningDate, today) ? 12.0 : 0.0;
                default: return leaveType.getMaxAllowedPerYear();
            }
        }
    }

    private double calculateBalanceForLateJoiner(Employee employee, LeaveType leaveType,
                                                 LocalDate joiningDate, LocalDate academicStart, LocalDate academicEnd) {
        String typeName = leaveType.getTypeName().toUpperCase();
        LocalDate today = LocalDate.now();

        int academicMonth = getAcademicMonth(joiningDate, academicStart);

        switch (typeName) {
            case "CL":
                return Math.max(0, 12 - academicMonth);
            case "PERMISSION":
                return Math.max(0, 24 - (academicMonth * 2));
            case "LATE":
                return Math.max(0, 24 - (academicMonth * 2));
            case "ML":
                // ML activates after 15 months from joining date
                LocalDate mlActivationDate = getMLELActivationDate(joiningDate, academicStart, academicMonth);
                return (today.isAfter(mlActivationDate) || today.equals(mlActivationDate)) ? 6.0 : 0.0;
            case "EL":
                // EL activates after 15 months from joining date
                LocalDate elActivationDate = getMLELActivationDate(joiningDate, academicStart, academicMonth);
                return (today.isAfter(elActivationDate) || today.equals(elActivationDate)) ? 12.0 : 0.0;
            default:
                return leaveType.getMaxAllowedPerYear();
        }
    }

    private int getAcademicMonth(LocalDate joiningDate, LocalDate academicStart) {
        int year = academicStart.getYear();

        // Define academic months with their date ranges
        LocalDate[][] monthRanges = {
                {LocalDate.of(year, 5, 25), LocalDate.of(year, 6, 24)},      // Month 1
                {LocalDate.of(year, 6, 25), LocalDate.of(year, 7, 25)},      // Month 2
                {LocalDate.of(year, 7, 26), LocalDate.of(year, 8, 25)},      // Month 3
                {LocalDate.of(year, 8, 26), LocalDate.of(year, 9, 24)},      // Month 4
                {LocalDate.of(year, 9, 25), LocalDate.of(year, 10, 25)},     // Month 5
                {LocalDate.of(year, 10, 26), LocalDate.of(year, 11, 24)},    // Month 6
                {LocalDate.of(year, 11, 25), LocalDate.of(year, 12, 25)},    // Month 7
                {LocalDate.of(year, 12, 26), LocalDate.of(year + 1, 1, 25)}, // Month 8
                {LocalDate.of(year + 1, 1, 26), getFebruaryEndDate(year + 1)}, // Month 9
                {getFebruaryStartDate(year + 1), LocalDate.of(year + 1, 3, 25)}, // Month 10
                {LocalDate.of(year + 1, 3, 26), LocalDate.of(year + 1, 4, 24)}, // Month 11
                {LocalDate.of(year + 1, 4, 25), LocalDate.of(year + 1, 5, 24)}  // Month 12
        };

        for (int i = 0; i < monthRanges.length; i++) {
            LocalDate start = monthRanges[i][0];
            LocalDate end = monthRanges[i][1];

            if (!joiningDate.isBefore(start) && !joiningDate.isAfter(end)) {
                return i + 1; // Return 1-based month number
            }
        }

        return 12; // Default to last month if not found
    }

    private LocalDate getMLELActivationDate(LocalDate joiningDate, LocalDate academicStart, int joiningAcademicMonth) {
        int year = academicStart.getYear();

        // ML/EL activation dates based on academic month (15 months after joining)
        LocalDate[][] activationRanges = {
                {LocalDate.of(year + 1, 8, 26), LocalDate.of(year + 1, 9, 24)},   // Month 1 -> Month 3 next year
                {LocalDate.of(year + 1, 9, 25), LocalDate.of(year + 1, 10, 25)},  // Month 2 -> Month 4 next year
                {LocalDate.of(year + 1, 10, 26), LocalDate.of(year + 1, 11, 24)}, // Month 3 -> Month 5 next year
                {LocalDate.of(year + 1, 11, 25), LocalDate.of(year + 1, 12, 25)}, // Month 4 -> Month 6 next year
                {LocalDate.of(year + 1, 12, 26), LocalDate.of(year + 2, 1, 25)},  // Month 5 -> Month 7 next year
                {LocalDate.of(year + 2, 1, 26), getFebruaryEndDate(year + 2)},    // Month 6 -> Month 8 next year
                {getFebruaryStartDate(year + 2), LocalDate.of(year + 2, 3, 25)},   // Month 7 -> Month 9 next year
                {LocalDate.of(year + 2, 3, 26), LocalDate.of(year + 2, 4, 24)},   // Month 8 -> Month 10 next year
                {LocalDate.of(year + 2, 4, 25), LocalDate.of(year + 2, 5, 24)},   // Month 9 -> Month 11 next year
                {LocalDate.of(year + 2, 5, 25), LocalDate.of(year + 2, 6, 24)},   // Month 10 -> Month 12 next year
                {LocalDate.of(year + 2, 6, 25), LocalDate.of(year + 2, 7, 25)},   // Month 11 -> Month 1 next year
                {LocalDate.of(year + 2, 7, 26), LocalDate.of(year + 2, 8, 25)}    // Month 12 -> Month 2 next year
        };

        if (joiningAcademicMonth >= 1 && joiningAcademicMonth <= 12) {
            return activationRanges[joiningAcademicMonth - 1][0];
        }

        // Fallback: 15 months from joining date
        return joiningDate.plusMonths(15);
    }

    private boolean isMLELEligible(LocalDate joiningDate, LocalDate today) {
        return today.isAfter(joiningDate.plusMonths(15)) || today.equals(joiningDate.plusMonths(15));
    }

    private LocalDate getFebruaryEndDate(int year) {
        boolean isLeap = Year.of(year).isLeap();
        return isLeap ? LocalDate.of(year, 2, 23) : LocalDate.of(year, 2, 22);
    }

    private LocalDate getFebruaryStartDate(int year) {
        boolean isLeap = Year.of(year).isLeap();
        return isLeap ? LocalDate.of(year, 2, 24) : LocalDate.of(year, 2, 23);
    }
}