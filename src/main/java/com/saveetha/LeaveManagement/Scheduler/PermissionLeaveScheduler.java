
package com.saveetha.LeaveManagement.Scheduler;

import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalance;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.repository.EmployeeLeaveBalanceRepository;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import com.saveetha.LeaveManagement.repository.LeaveRequestRepository;
import com.saveetha.LeaveManagement.repository.LeaveTypeRepository;
import com.saveetha.LeaveManagement.utility.MonthRange;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PermissionLeaveScheduler {

    private final EmployeeRepository employeeRepo;
    private final EmployeeLeaveBalanceRepository balanceRepo;
    private final LeaveTypeRepository leaveTypeRepo;
    private final LeaveRequestRepository lrRepo;

    public PermissionLeaveScheduler(EmployeeRepository employeeRepo,
                                    EmployeeLeaveBalanceRepository balanceRepo,
                                    LeaveTypeRepository leaveTypeRepo,
                                    LeaveRequestRepository lrRepo) {
        this.employeeRepo  = employeeRepo;
        this.balanceRepo   = balanceRepo;
        this.leaveTypeRepo = leaveTypeRepo;
        this.lrRepo        = lrRepo;
    }

    /**
     * Fires at midnight on the 22nd, 23rd, 24th, and 25th of every month.
     * But only actually runs the deduction if today matches an academic-month end.
     */
    @Scheduled(cron = "0 0 0 22,23,24,25 * *")
    public void adjustMonthlyPermissionBalances() {
        LocalDate today = LocalDate.now();

        // 1) Compute this cycle’s academicStart (May 26 of current or previous year)
        LocalDate academicStart = LocalDate.of(
                today.getMonthValue() >= 5 ? today.getYear() : today.getYear() - 1,
                5,
                26
        );

        // 2) Build all academic-month ranges up to today
        List<MonthRange> ranges = buildAcademicMonthRanges(academicStart, today);

        // 3) Is today one of their endDates?
        Optional<MonthRange> optBoundary = ranges.stream()
                .filter(r -> r.getEnd().equals(today))
                .findFirst();
        if (optBoundary.isEmpty()) {
            return; // not a real academic boundary
        }
        MonthRange boundaryMonth = optBoundary.get();

        // 4) Load the Permission leave type
        LeaveType permType = leaveTypeRepo.findByTypeNameIgnoreCase("permission")
                .orElseThrow(() -> new IllegalStateException("Permission LeaveType not found"));

        // 5) For each employee, count used and deduct unused
        for (Employee emp : employeeRepo.findAll()) {
            int used = lrRepo.countPermissionLeavesInRange(
                    emp.getEmpId(),
                    boundaryMonth.getStart(),
                    boundaryMonth.getEnd()
            );

            int unused = Math.max(0, 2 - used);
            if (unused == 0) {
                continue; // they used all 2 (or more) → nothing to deduct
            }

            EmployeeLeaveBalance bal = balanceRepo
                    .findByEmployeeAndLeaveType(emp, permType)
                    .orElseThrow(() -> new IllegalStateException(
                            "Balance not found for emp " + emp.getEmpId()));

            bal.setBalanceLeave(Math.max(0, bal.getBalanceLeave() - unused));
            balanceRepo.save(bal);
        }
    }

    private List<MonthRange> buildAcademicMonthRanges(LocalDate academicStart, LocalDate now) {
        List<MonthRange> ranges = new ArrayList<>();

        // Academic cycle is fixed: May 26 to May 25 of next year
        int year = academicStart.getYear();
        boolean isLeap = Year.of(year + 1).isLeap(); // Feb in next calendar year

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

            ranges.add(new MonthRange(start, end,now));
        }

        return ranges;
    }
}

