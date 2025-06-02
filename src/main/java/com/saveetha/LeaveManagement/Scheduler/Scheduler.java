package com.saveetha.LeaveManagement.Scheduler;

import com.saveetha.LeaveManagement.service.LeaveResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class Scheduler {

    @Autowired
    private final LeaveResetService leaveResetService;

    @Scheduled(cron = "0 0 1 25 06 *")
    public void autoResetLeaveBalances() {
        String newAcademicYear = getCurrentAcademicYear();
        leaveResetService.resetAllEmployeeLeaveBalances(newAcademicYear);
    }

    private String getCurrentAcademicYear() {
        int currentYear = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        if (month >= 6) {
            return currentYear + "-" + (currentYear + 1);  // e.g., "2024-2025"
        } else {
            return (currentYear - 1) + "-" + currentYear;  // e.g., "2023-2024"
        }
    }
}
