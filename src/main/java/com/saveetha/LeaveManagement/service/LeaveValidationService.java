package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.dto.LeaveRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class LeaveValidationService {

    public void validatePermissionLeave(LeaveRequestDTO leaveRequestdto) {
        if (leaveRequestdto.getStartTime() == null || leaveRequestdto.getEndTime() == null) {
            throw new RuntimeException("Start and End Time are required for Permission Leave.");
        }
        if (!leaveRequestdto.getStartDate().equals(leaveRequestdto.getEndDate())) {
            throw new RuntimeException("Permission Leave must be for a single day.");
        }
        long duration = java.time.Duration.between(
                leaveRequestdto.getStartTime(), leaveRequestdto.getEndTime()).toMinutes();
        if (duration > 60) {
            throw new RuntimeException("Permission Leave cannot exceed 1 hour.");
        }
        // TODO: Check if the employee has used 2 permissions in the current month.
    }

    public void validateMedicalLeave(LeaveRequestDTO leaveRequestdto) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(
                leaveRequestdto.getStartDate(), leaveRequestdto.getEndDate()) + 1;
        if (days < 3) {
            throw new RuntimeException("Medical Leave must be at least 3 consecutive days.");
        }

        if (leaveRequestdto.getFileUpload() == null || leaveRequestdto.getFileUpload().isEmpty()) {
            throw new RuntimeException("Medical Leave requires a file upload (supporting document).");
        }
    }

    public void validateEarnedLeave(LeaveRequestDTO leaveRequestdto) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(
                leaveRequestdto.getStartDate(), leaveRequestdto.getEndDate()) + 1;
        if (days < 3) {
            throw new RuntimeException("Earned Leave must be at least 3 consecutive days.");
        }
    }

    public void validateCompOffLeave(LeaveRequestDTO leaveRequestdto) {
        if (leaveRequestdto.getEarnedDate() == null) {
            throw new RuntimeException("Comp Off Leave requires an Earned Date.");
        }
    }

    public void validateCasualLeave(LeaveRequestDTO leaveRequestDTO){

    }
}
