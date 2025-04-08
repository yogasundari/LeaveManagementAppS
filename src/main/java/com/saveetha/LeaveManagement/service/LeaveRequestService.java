package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.dto.LeaveRequestDTO;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.LeaveAlteration;
import com.saveetha.LeaveManagement.entity.LeaveRequest;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.enums.AlterationType;
import com.saveetha.LeaveManagement.enums.LeaveStatus;
import com.saveetha.LeaveManagement.enums.NotificationStatus;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import com.saveetha.LeaveManagement.repository.LeaveAlterationRepository;
import com.saveetha.LeaveManagement.repository.LeaveRequestRepository;
import com.saveetha.LeaveManagement.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveAlterationRepository leaveAlterationRepository;

    public LeaveRequest createDraftLeaveRequest(LeaveRequestDTO dto) {
        Employee employee = employeeRepository.findByEmpId(dto.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("LeaveType not found"));

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(dto.getStartDate());
        leaveRequest.setEndDate(dto.getEndDate());
        leaveRequest.setStartTime(dto.getStartTime());
        leaveRequest.setEndTime(dto.getEndTime());
        leaveRequest.setReason(dto.getReason());
        leaveRequest.setEarnedDate(dto.getEarnedDate());
        leaveRequest.setFileUpload(dto.getFileUpload());
        leaveRequest.setStatus(LeaveStatus.DRAFT); // <-- Important for draft

        return leaveRequestRepository.save(leaveRequest);
    }
    public String submitLeaveRequest(Integer requestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave Request not found"));

        if (!leaveRequest.getStatus().equals(LeaveStatus.DRAFT)) {
            throw new RuntimeException("Only DRAFT leave requests can be submitted");
        }

        // Get alterations linked to this leave request
        List<LeaveAlteration> alterations = leaveAlterationRepository.findByLeaveRequest_RequestId(requestId);

        boolean hasAlteration = !alterations.isEmpty();

        if (hasAlteration) {
            for (LeaveAlteration alt : alterations) {
                if (alt.getAlterationType() == AlterationType.STAFF_ALTERATION) {
                    if (alt.getNotificationStatus() != NotificationStatus.APPROVED) {
                        throw new RuntimeException("All staff alterations must be approved before submission.");
                    }
                }
            }
        }

        // If no alteration or all alterations are valid, proceed
        leaveRequest.setStatus(LeaveStatus.PENDING);
        leaveRequestRepository.save(leaveRequest);
        return "Leave request submitted successfully!";
    }

}
