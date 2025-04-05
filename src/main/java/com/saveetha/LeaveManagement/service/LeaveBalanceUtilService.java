package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalance;
import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalanceId;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.repository.EmployeeLeaveBalanceRepository;
import com.saveetha.LeaveManagement.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveBalanceUtilService {

    private final EmployeeLeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveBalanceUtilService(EmployeeLeaveBalanceRepository leaveBalanceRepository, LeaveTypeRepository leaveTypeRepository) {
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public void initializeLeaveBalance(Employee employee) {
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        int joiningMonth = employee.getJoiningDate().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        for (LeaveType leaveType : leaveTypes) {
            initializeLeaveBalanceForType(employee, leaveType, joiningMonth, currentYear);
        }
    }

    private void initializeLeaveBalanceForType(Employee employee, LeaveType leaveType, int joiningMonth, int currentYear) {
        LocalDate joiningDate = employee.getJoiningDate();
        LocalDate oneYearCompletionDate = joiningDate.plusYears(1);
        LocalDate currentDate = LocalDate.now();

        boolean isFirstYear = currentDate.isBefore(oneYearCompletionDate);
        boolean isFirstMonth = joiningDate.getMonthValue() == currentDate.getMonthValue();

        if (isFirstYear && !(leaveType.getTypeName().equalsIgnoreCase("Casual Leave") ||
                leaveType.getTypeName().equalsIgnoreCase("Permission Leave"))) {
            return;
        }

        if (isFirstMonth && leaveType.getTypeName().equalsIgnoreCase("Casual Leave")) {
            return;
        }

        int maxAllowedPerYear = leaveType.getMaxAllowedPerYear();
        int remainingMonths = 12 - (joiningMonth - 6);
        if (remainingMonths < 0) remainingMonths = 0;

        int proratedLeave = (leaveType.getTypeName().equalsIgnoreCase("Casual Leave") && isFirstMonth)
                ? 0
                : (maxAllowedPerYear * remainingMonths) / 12;

        EmployeeLeaveBalanceId leaveBalanceId = new EmployeeLeaveBalanceId(employee.getEmpId(), leaveType.getLeaveTypeId());
        Optional<EmployeeLeaveBalance> existingBalance = leaveBalanceRepository.findById(leaveBalanceId);

        EmployeeLeaveBalance leaveBalance = existingBalance.orElseGet(() -> new EmployeeLeaveBalance());
        leaveBalance.setId(leaveBalanceId);
        leaveBalance.setEmployee(employee);
        leaveBalance.setLeaveType(leaveType);
        leaveBalance.setBalanceLeave(proratedLeave);
        leaveBalance.setUsedLeaves(0);
        leaveBalance.setCurrentYear(currentYear);

        leaveBalanceRepository.save(leaveBalance);
    }
}
