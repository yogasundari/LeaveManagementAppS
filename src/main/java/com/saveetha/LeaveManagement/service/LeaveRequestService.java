package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.dto.LeaveRequestDto;
import com.saveetha.LeaveManagement.entity.*;
import com.saveetha.LeaveManagement.enums.LeaveStatus;
import com.saveetha.LeaveManagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private EmployeeLeaveBalanceRepository employeeLeaveBalanceRepository;

    public String applyForLeave(LeaveRequestDto leaveRequestDto) {
        // 1️⃣ Find Employee & Leave Type
        Employee employee = employeeRepository.findById(leaveRequestDto.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        LeaveType leaveType = leaveTypeRepository.findById(leaveRequestDto.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        // 2️⃣ Fetch Leave Balance
        Optional<EmployeeLeaveBalance> leaveBalanceOpt = employeeLeaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType);
        if (leaveBalanceOpt.isEmpty()) {
            return "No leave balance record found!";
        }
        EmployeeLeaveBalance leaveBalance = leaveBalanceOpt.get();

        // 3️⃣ Calculate Requested Days
        int requestedDays = (int) ChronoUnit.DAYS.between(leaveRequestDto.getStartDate(), leaveRequestDto.getEndDate()) + 1;

        // 4️⃣ Special Leave Type Conditions
        if (leaveType.getTypeName().equalsIgnoreCase("Permission")) {
            if (leaveRequestDto.getStartTime() == null || leaveRequestDto.getEndTime() == null) {
                return "Start time and end time are required for Permission leave.";
            }
        }

        if (leaveType.getTypeName().equalsIgnoreCase("Medical Leave")) {
            if (leaveRequestDto.getFileUpload() == null || leaveRequestDto.getFileUpload().isEmpty()) {
                return "Medical leave requires a file upload.";
            }
        }

        if (leaveType.getTypeName().equalsIgnoreCase("Comp Off")) {
            if (leaveRequestDto.getEarnedDate() == null) {
                return "Comp Off leave requires an earned date.";
            }
        }

        // 5️⃣ Check Leave Balance
        if (leaveBalance.getBalanceLeave() < requestedDays) {
            return "Insufficient leave balance!";
        }

        // 6️⃣ Deduct Leave Balance
        leaveBalance.setBalanceLeave(leaveBalance.getBalanceLeave() - requestedDays);
        leaveBalance.setUsedLeaves(leaveBalance.getUsedLeaves() + requestedDays);
        employeeLeaveBalanceRepository.save(leaveBalance);

        // 7️⃣ Save Leave Request
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(leaveRequestDto.getStartDate());
        leaveRequest.setEndDate(leaveRequestDto.getEndDate());
        leaveRequest.setStartTime(leaveRequestDto.getStartTime());
        leaveRequest.setEndTime(leaveRequestDto.getEndTime());
        leaveRequest.setReason(leaveRequestDto.getReason());
        leaveRequest.setEarnedDate(leaveRequestDto.getEarnedDate());
        leaveRequest.setClassPeriod(leaveRequestDto.getClassPeriod());
        leaveRequest.setClassDate(leaveRequestDto.getClassDate());
        leaveRequest.setSubjectName(leaveRequestDto.getSubjectName());
        leaveRequest.setSubjectCode(leaveRequestDto.getSubjectCode());
        leaveRequest.setFileUpload(leaveRequestDto.getFileUpload());
        leaveRequest.setStatus(LeaveStatus.PENDING);

        leaveRequestRepository.save(leaveRequest);

        return "Leave request submitted successfully!";
    }
}
