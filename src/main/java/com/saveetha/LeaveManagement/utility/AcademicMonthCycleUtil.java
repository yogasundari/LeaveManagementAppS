package com.saveetha.LeaveManagement.utility;

import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AcademicMonthCycleUtil {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    public static class MonthCycle {
        private String monthName;
        private LocalDate startDate;
        private LocalDate endDate;

        public MonthCycle(String monthName, LocalDate startDate, LocalDate endDate) {
            this.monthName = monthName;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        public String toString() {
            return monthName + " (" + startDate + " to " + endDate + ")";
        }
    }

    // Method to get the academic month for a given date
    public MonthCycle getAcademicMonth(LocalDate date) {
        // Fetch academic year start and end from the LeaveType repository
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        if (leaveTypes.isEmpty()) {
            return null; // No leave types found
        }

        // Assuming there is only one leave type or taking the first one
        LeaveType leaveType = leaveTypes.get(0);
        LocalDate academicStartDate = leaveType.getAcademicYearStart();
        LocalDate academicEndDate = leaveType.getAcademicYearEnd();

        // Check if the provided date is within the academic cycle
        if (date.isBefore(academicStartDate) || date.isAfter(academicEndDate)) {
            return null; // Date is outside the academic year
        }

        // Mapping for academic month cycles using the start and end dates
        if (date.isAfter(academicStartDate) && date.isBefore(academicStartDate.plusMonths(1))) {
            return new MonthCycle("May 26 - June 24", academicStartDate, academicStartDate.plusMonths(1).minusDays(1));
        } else if (date.isAfter(academicStartDate.plusMonths(1)) && date.isBefore(academicStartDate.plusMonths(2))) {
            return new MonthCycle("June 25 - July 25", academicStartDate.plusMonths(1), academicStartDate.plusMonths(2).minusDays(1));
        } else if (date.isAfter(academicStartDate.plusMonths(2)) && date.isBefore(academicStartDate.plusMonths(3))) {
            return new MonthCycle("July 26 - August 25", academicStartDate.plusMonths(2), academicStartDate.plusMonths(3).minusDays(1));
        } else if (date.isAfter(academicStartDate.plusMonths(3)) && date.isBefore(academicStartDate.plusMonths(4))) {
            return new MonthCycle("August 26 - September 24", academicStartDate.plusMonths(3), academicStartDate.plusMonths(4).minusDays(1));
        } else if (date.isAfter(academicStartDate.plusMonths(4)) && date.isBefore(academicStartDate.plusMonths(5))) {
            return new MonthCycle("September 25 - October 25", academicStartDate.plusMonths(4), academicStartDate.plusMonths(5).minusDays(1));
        } else if (date.isAfter(academicStartDate.plusMonths(5)) && date.isBefore(academicStartDate.plusMonths(6))) {
            return new MonthCycle("October 26 - November 24", academicStartDate.plusMonths(5), academicStartDate.plusMonths(6).minusDays(1));
        } else if (date.isAfter(academicStartDate.plusMonths(6)) && date.isBefore(academicStartDate.plusMonths(7))) {
            return new MonthCycle("November 25 - December 25", academicStartDate.plusMonths(6), academicStartDate.plusMonths(7).minusDays(1));
        } else if (date.isAfter(academicStartDate.plusMonths(7)) && date.isBefore(academicStartDate.plusMonths(8))) {
            return new MonthCycle("December 26 - January 25", academicStartDate.plusMonths(7), academicStartDate.plusMonths(8).minusDays(1));
        } else if (date.isAfter(academicStartDate.plusMonths(8)) && date.isBefore(academicStartDate.plusMonths(9))) {
            return new MonthCycle("January 26 - February 22 (Non-Leap)", academicStartDate.plusMonths(8), academicStartDate.plusMonths(9).minusDays(1));
        } else if (date.isAfter(academicStartDate.plusMonths(9)) && date.isBefore(academicStartDate.plusMonths(10))) {
            return new MonthCycle("February 23 - March 25 (Non-Leap)", academicStartDate.plusMonths(9), academicStartDate.plusMonths(10).minusDays(1));
        } else if (date.isAfter(academicStartDate.plusMonths(10)) && date.isBefore(academicStartDate.plusMonths(11))) {
            return new MonthCycle("March 26 - April 24", academicStartDate.plusMonths(10), academicStartDate.plusMonths(11).minusDays(1));
        } else if (date.isAfter(academicStartDate.plusMonths(11)) && date.isBefore(academicStartDate.plusMonths(12))) {
            return new MonthCycle("April 25 - May 25 (Last Month)", academicStartDate.plusMonths(11), academicStartDate.plusMonths(12).minusDays(1));
        }

        return null; // Return null if the date doesn't match any academic month
    }

}
