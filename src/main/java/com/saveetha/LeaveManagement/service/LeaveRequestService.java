package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.dto.LeaveRequestDTO;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.LeaveAlteration;
import com.saveetha.LeaveManagement.entity.LeaveRequest;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.enums.AlterationType;
import com.saveetha.LeaveManagement.enums.LeaveDuration;
import com.saveetha.LeaveManagement.enums.LeaveStatus;
import com.saveetha.LeaveManagement.enums.NotificationStatus;
import com.saveetha.LeaveManagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalance;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveAlterationRepository leaveAlterationRepository;
    private final LeaveApprovalService leaveApprovalService;

    public LeaveRequest createDraftLeaveRequest(LeaveRequestDTO leaveRequestdto) {
        Employee employee = employeeRepository.findByEmpId(leaveRequestdto.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveType leaveType = leaveTypeRepository.findById(leaveRequestdto.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("LeaveType not found"));

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setLeaveType(leaveType);
        leaveRequest.setStartDate(leaveRequestdto.getStartDate());
        leaveRequest.setEndDate(leaveRequestdto.getEndDate());
        leaveRequest.setStartTime(leaveRequestdto.getStartTime());
        leaveRequest.setEndTime(leaveRequestdto.getEndTime());
        leaveRequest.setReason(leaveRequestdto.getReason());
        leaveRequest.setEarnedDate(leaveRequestdto.getEarnedDate());
        leaveRequest.setFileUpload(leaveRequestdto.getFileUpload());
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
        leaveApprovalService.initiateApprovalFlow(requestId);
        return "Leave request submitted successfully!";
    }


    public String withdrawLeaveRequest(Integer requestId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        if (leaveRequest.getStatus() == LeaveStatus.PENDING || leaveRequest.getStatus() == LeaveStatus.APPROVED) {
            leaveRequest.setStatus(LeaveStatus.WITHDRAWN);
            leaveRequestRepository.save(leaveRequest);
            return "Leave request withdrawn successfully.";
        } else {
            throw new RuntimeException("Only PENDING or APPROVED leave requests can be withdrawn.");
        }
    }

}
