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
    private final EmployeeLeaveBalanceRepository leaveBalanceRepository;
    private final LeaveResetService leaveResetService;

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
        // Fetch employee's leave balance for the requested leave type
        Employee employee = leaveRequest.getEmployee();
        LeaveType leaveType = leaveRequest.getLeaveType();
        EmployeeLeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType)
                .orElseThrow(() -> new RuntimeException("Leave balance not found"));

        // Perform leave type specific validation
     // switch (leaveType.getTypeName().toUpperCase()) {
             // case "CL": // Casual Leave (1 per month)
                 // validateCasualLeave(leaveBalance, leaveRequest);
               //    break;
         //   case "ML": // Medical Leave (6 per year, 3+ days)
             //   validateMedicalLeave(leaveBalance, leaveRequest);
             //   break;
       //     case "EL": // Earned Leave (12 per year, 3+ days)
             //   validateEarnedLeave(leaveBalance, leaveRequest);
              //  break;
         //   case "PERMISSION": // Permission Leave (2 per month, 1 hour each)
             //   validatePermissionLeave(leaveBalance, leaveRequest);
             //   break;
         //   case "LATE PERMISSION": // Same as Permission Leave but 10 minutes each
            //    validateLatePermissionLeave(leaveBalance, leaveRequest);
              //  break;
         //   case "VACATION": // Vacation (Approval-based)
                // No balance validation, just approval
             //   break;
           // case "COMPOFF": // Comp Off (Approval-based)
              //  validateCompOffLeave(leaveBalance, leaveRequest);
              //  break;
         //   case "RELIGION HOLIDAY": // Religion Holiday (1 per year)
                //validateReligionHoliday(leaveBalance, leaveRequest);
          //      break;
         //   case "LOP": // LOP (Approval-based)
       //     case "OD": // OD (Approval-based)
         //   case "SPECIAL OD": // Special OD (Approval-based)
                // No balance validation, just approval
       //         break;
        //    default:
        //        throw new RuntimeException("Unknown leave type.");
     //   }

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
