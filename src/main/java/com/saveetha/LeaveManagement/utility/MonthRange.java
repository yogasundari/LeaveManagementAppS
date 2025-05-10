package com.saveetha.LeaveManagement.utility;

import java.time.LocalDate;

public class MonthRange {
    private LocalDate start;
    private LocalDate end;
    private LocalDate currentDate;

    // Default constructor (not necessary, but useful if needed)
    public MonthRange() {
    }

    // Constructor with parameters
    public MonthRange(LocalDate start, LocalDate end, LocalDate currentDate) {
        this.start = start;
        this.end = end;
        this.currentDate = currentDate;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public LocalDate getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(LocalDate currentDate) {
        this.currentDate = currentDate;
    }

    @Override
    public String toString() {
        return "MonthRange{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
