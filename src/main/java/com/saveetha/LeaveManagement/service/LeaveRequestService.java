package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.dto.LeaveRequestDTO;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.LeaveRequest;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.enums.LeaveStatus;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import com.saveetha.LeaveManagement.repository.LeaveRequestRepository;
import com.saveetha.LeaveManagement.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;


    @Autowired
    private LeaveAlterationService leaveAlterationService; // ✅ Added missing service

    public LeaveRequest createLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        Employee employee = employeeRepository.findById(leaveRequestDTO.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository.findById(leaveRequestDTO.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave Type not found"));
        if (leaveRequestDTO.getClassPeriod() != null) {
            boolean alterationValid = leaveAlterationService.isAlterationCompleted(
                    leaveRequestDTO.getEmpId(),
                    leaveRequestDTO.getClassDate(), // This must be LocalDate
                    leaveRequestDTO.getClassPeriod()
            );

            if (!alterationValid) {
                throw new RuntimeException("Leave alteration is not completed. Either provide Moodle link or get staff approval.");
            }
        }


        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(leaveRequestDTO.getStartDate());
        leaveRequest.setEndDate(leaveRequestDTO.getEndDate());
        leaveRequest.setStartTime(leaveRequestDTO.getStartTime());
        leaveRequest.setEndTime(leaveRequestDTO.getEndTime());
        leaveRequest.setReason(leaveRequestDTO.getReason());
        leaveRequest.setEarnedDate(leaveRequestDTO.getEarnedDate());
        leaveRequest.setClassPeriod(leaveRequestDTO.getClassPeriod());
        leaveRequest.setClassDate(leaveRequestDTO.getClassDate());
        leaveRequest.setSubjectName(leaveRequestDTO.getSubjectName());
        leaveRequest.setSubjectCode(leaveRequestDTO.getSubjectCode());
        leaveRequest.setFileUpload(leaveRequestDTO.getFileUpload());
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequest.setActive(true);
        leaveRequest.setCreatedAt(LocalDateTime.now());
        leaveRequest.setUpdatedAt(LocalDateTime.now());


        return leaveRequestRepository.save(leaveRequest);
    }
}
