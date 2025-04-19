package com.saveetha.LeaveManagement.utility;

import com.saveetha.LeaveManagement.utility.AcademicMonthCycleUtil.MonthCycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AcademicMonthTest implements CommandLineRunner {

    @Autowired
    private AcademicMonthCycleUtil academicMonthCycleUtil;

    @Override
    public void run(String... args) {
        LocalDate today = LocalDate.of(2024, 8, 1);  // Get today's date
        MonthCycle month = academicMonthCycleUtil.getAcademicMonth(today);

        if (month != null) {
            System.out.println(" *_______________________Current Academic Month: ___________________*" + month);
        } else {
            System.out.println("Today's date is not in the academic year cycle.");
        }
    }
}

