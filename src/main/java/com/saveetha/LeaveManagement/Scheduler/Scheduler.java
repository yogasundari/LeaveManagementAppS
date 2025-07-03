package com.saveetha.LeaveManagement.Scheduler;

import com.saveetha.LeaveManagement.service.LeaveResetService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class Scheduler {

    private final LeaveResetService leaveResetService;

    @Scheduled(cron = "0 2 5 26 * ?")
    public void autoResetLeaveBalances() {
        String newAcademicYear = getCurrentAcademicYear();
        leaveResetService.resetAllEmployeeLeaveBalances(newAcademicYear);
    }

    private String getCurrentAcademicYear() {
        int currentYear = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue();

        if (month >= 6) {
            return currentYear + "-" + (currentYear + 1);
        } else {
            return (currentYear - 1) + "-" + currentYear;
        }
    }
}
