package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalance;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.utility.MonthRange;
import com.saveetha.LeaveManagement.repository.EmployeeLeaveBalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
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
            double balance = 0.0;
            String typeName = leaveType.getTypeName().toUpperCase();
            LocalDate academicStart = leaveType.getAcademicYearStart();
            LocalDate academicEnd = leaveType.getAcademicYearEnd();

            if (joiningDate == null || joiningDate.isAfter(academicEnd)) continue;

            List<MonthRange> academicMonths = buildAcademicMonthRanges(academicStart, today);

            if (joiningDate.isBefore(academicStart)) {
                // Joined before academic year
                switch (typeName) {
                    case "CL": balance = 12; break;
                    case "PERMISSION":
                    case "LATE": balance = 24; break;
                    case "ML": balance = today.isAfter(joiningDate.plusMonths(15)) ? 6 : 0; break;
                    case "EL": balance = today.isAfter(joiningDate.plusMonths(15)) ? 12 : 0; break;
                    default: balance = leaveType.getMaxAllowedPerYear(); break;
                }
            } else {
                int completedAcademicMonths = getCompletedAcademicMonths(joiningDate, academicMonths);

                switch (typeName) {
                    case "CL":
                        balance = Math.max(12 - (completedAcademicMonths - 1), 0);
                        break;
                    case "PERMISSION":
                    case "LATE":
                        balance = Math.max(24 - (completedAcademicMonths * 2), 0);
                        break;
                    case "ML":
                        balance = today.isAfter(joiningDate.plusMonths(15)) ? 6 : 0;
                        break;
                    case "EL":
                        balance = today.isAfter(joiningDate.plusMonths(15)) ? 12 : 0;
                        break;
                    default:
                        balance = leaveType.getMaxAllowedPerYear();
                        break;
                }
            }

            final double finalBalance = balance;
            EmployeeLeaveBalance leaveBalance = balanceRepository
                    .findByEmployeeAndLeaveType(employee, leaveType)
                    .orElseGet(() -> new EmployeeLeaveBalance(employee, leaveType, academicYear, finalBalance));

            leaveBalance.setUsedLeaves(0.0);
            leaveBalance.setCarryForwardLeave(0.0);
            leaveBalance.setCurrentYear(academicYear);
            leaveBalance.setBalanceLeave(finalBalance);

            balanceRepository.save(leaveBalance);

            System.out.println("Initialized " + typeName + " for " + employee.getEmpId() + ": " + finalBalance);
        }

        System.out.println("✅Leave balance initialized for employee: " + employee.getEmpId());
    }

    private int getCompletedAcademicMonths(LocalDate joiningDate, List<MonthRange> academicMonths) {
        int completed = 0;
        for (MonthRange range : academicMonths) {
            if (!joiningDate.isAfter(range.getEnd())) {
                completed++;
            }
        }
        return completed;
    }

    private List<MonthRange> buildAcademicMonthRanges(LocalDate academicStart, LocalDate now) {
        List<MonthRange> ranges = new ArrayList<>();
        int year = academicStart.getYear();
        boolean isLeap = Year.of(year + 1).isLeap();

        LocalDate[] startDates = new LocalDate[]{
                LocalDate.of(year, 5, 26),
                LocalDate.of(year, 6, 25),
                LocalDate.of(year, 7, 26),
                LocalDate.of(year, 8, 26),
                LocalDate.of(year, 9, 25),
                LocalDate.of(year, 10, 26),
                LocalDate.of(year, 11, 25),
                LocalDate.of(year, 12, 26),
                LocalDate.of(year + 1, 1, 26),
                isLeap ? LocalDate.of(year + 1, 2, 24) : LocalDate.of(year + 1, 2, 23),
                LocalDate.of(year + 1, 3, 26),
                LocalDate.of(year + 1, 4, 25)
        };

        LocalDate[] endDates = new LocalDate[]{
                LocalDate.of(year, 6, 24),
                LocalDate.of(year, 7, 25),
                LocalDate.of(year, 8, 25),
                LocalDate.of(year, 9, 24),
                LocalDate.of(year, 10, 25),
                LocalDate.of(year, 11, 24),
                LocalDate.of(year, 12, 25),
                LocalDate.of(year + 1, 1, 25),
                isLeap ? LocalDate.of(year + 1, 2, 23) : LocalDate.of(year + 1, 2, 22),
                LocalDate.of(year + 1, 3, 25),
                LocalDate.of(year + 1, 4, 24),
                LocalDate.of(year + 1, 5, 25)
        };

        for (int i = 0; i < startDates.length; i++) {
            LocalDate start = startDates[i];
            LocalDate end = endDates[i];

            if (start.isAfter(now)) break;
            if (end.isAfter(now)) end = now;

            ranges.add(new MonthRange(start, end, now));
        }

        return ranges;
    }
}
