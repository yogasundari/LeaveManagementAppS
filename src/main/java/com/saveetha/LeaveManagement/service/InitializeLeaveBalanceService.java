package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalance;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.repository.EmployeeLeaveBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
        LocalDate today = LocalDate.now();

        for (LeaveType leaveType : leaveTypes) {
            double proratedLeave = 0.0;

            LocalDate academicStart = leaveType.getAcademicYearStart();
            LocalDate academicEnd = leaveType.getAcademicYearEnd();

            // Skip if joined after academic year ends
            if (joiningDate != null && joiningDate.isAfter(academicEnd)) continue;

            String typeName = leaveType.getTypeName().toUpperCase();

            if (joiningDate != null) {
                if (joiningDate.isBefore(academicStart)) {
                    // Joined before academic year: full allocation
                    proratedLeave = leaveType.getMaxAllowedPerYear();
                } else {
                    // Joined after academic year started: prorated
                    int monthsRemaining = monthsBetween(joiningDate, academicEnd);
                    monthsRemaining = Math.min(monthsRemaining, 12); // Cap at 12 months

                    switch (typeName) {
                        case "CL":
                            // Casual Leave: 1 CL per month, prorated based on remaining months in the year
                            proratedLeave = calculateCLMonths(joiningDate, academicStart, academicEnd); // Dynamic CL calculation
                            break;
                        case "PERMISSION":
                        case "LATE":
                            // Permission and Late Leave: 2 per month, prorated
                            proratedLeave = Math.min(monthsRemaining * 2, 24); // Max 24 days allowed for the year
                            break;
                        case "ML":
                        case "EL":
                            // Medical and Earned Leave: Allowed after 1 year 3 months
                            LocalDate eligibleDate = joiningDate.plusMonths(15);
                            proratedLeave = today.isAfter(eligibleDate) ? leaveType.getMaxAllowedPerYear() : 0.0;
                            break;
                        default:
                            // All others: full year allocation
                            proratedLeave = leaveType.getMaxAllowedPerYear();
                            break;
                    }
                }
            }

            final double finalLeave = proratedLeave;

            EmployeeLeaveBalance balance = balanceRepository
                    .findByEmployeeAndLeaveType(employee, leaveType)
                    .orElseGet(() -> new EmployeeLeaveBalance(employee, leaveType, academicYear, finalLeave));

            balance.setUsedLeaves(0.0);
            balance.setCarryForwardLeave(0.0);
            balance.setCurrentYear(academicYear);
            balance.setBalanceLeave(finalLeave);

            balanceRepository.save(balance);

            // Debugging info
            System.out.println("Initialized " + typeName + " for " + employee.getEmpId() + ": " + finalLeave);
        }

        System.out.println("✅ Leave balance initialized for employee: " + employee.getEmpId());
    }

    // Helper method to calculate months between two date
    private int monthsBetween(LocalDate joiningDate, LocalDate academicEnd) {
        if (joiningDate.isAfter(academicEnd)) return 0;

        // CL calculation should start from the next full month
        LocalDate startMonth = joiningDate.plusMonths(1).withDayOfMonth(1); // Start from the next full month
        LocalDate endMonth = academicEnd.withDayOfMonth(1);  // End month (academic year end)

        // If startMonth is after endMonth, no months available
        if (startMonth.isAfter(endMonth)) {
            return 0;
        }

        // Calculate the difference in years and months
        int yearDiff = endMonth.getYear() - startMonth.getYear();
        int monthDiff = endMonth.getMonthValue() - startMonth.getMonthValue();

        // Return total months between the two dates
        return yearDiff * 12 + monthDiff;
    }


    // Custom logic for calculating Casual Leave months based on joining date and academic year
    private int calculateCLMonths(LocalDate joiningDate, LocalDate academicStart, LocalDate academicEnd) {
        // If the employee joins after the academic year ends, no CLs for that year
        if (joiningDate.isAfter(academicEnd)) return 0;

        // If the employee joins before the academic year start (May 26), they get full CLs
        if (joiningDate.isBefore(academicStart)) {
            return 11; // Full year of CL
        }

        // CL calculation starts from the next full month after joining
        LocalDate startMonth = joiningDate.plusMonths(1).withDayOfMonth(1); // Start from next full month
        LocalDate endMonth = academicEnd.withDayOfMonth(1);  // End of academic year month

        // Calculate the months from the next full month
        int monthsRemaining = monthsBetween(startMonth, endMonth);

        // Adjust if they join after May 25, 2024 (they will get 0 CL for this academic year)
        if (joiningDate.getMonthValue() == 5 && joiningDate.getDayOfMonth() > 25) {
            return 0;  // No CL if joined after May 25
        }

        return monthsRemaining;  // Number of months the employee is eligible for CL
    }

}
