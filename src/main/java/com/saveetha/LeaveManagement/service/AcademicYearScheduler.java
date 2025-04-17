package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
public class AcademicYearScheduler {

    @Autowired
    private final LeaveTypeRepository leaveTypeRepository;

    public AcademicYearScheduler(LeaveTypeRepository leaveTypeRepository) {
        this.leaveTypeRepository = leaveTypeRepository;
    }

    // This will run every day at 1 A

        @Scheduled(cron = "0 0 1 26 5 *") // runs at 1:00 AM on April 17th every year
        public void updateAcademicYear() {
            LocalDate start = LocalDate.now();
            LocalDate end = start.plusYears(1).minusDays(1); // 1-year period

            List<LeaveType> leaveTypes = leaveTypeRepository.findAll();

            for (LeaveType lt : leaveTypes) {
                lt.setAcademicYearStart(start);
                lt.setAcademicYearEnd(end);
                leaveTypeRepository.save(lt);
                System.out.println("Updated " + lt.getTypeName() + ": Start = " + start + ", End = " + end);
            }

            System.out.println(" All leave types updated with new academic year.");
        }
    }


