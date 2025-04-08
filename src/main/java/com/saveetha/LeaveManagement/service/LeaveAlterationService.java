package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.dto.LeaveAlterationDto;
import com.saveetha.LeaveManagement.entity.*;
import com.saveetha.LeaveManagement.enums.*;
import com.saveetha.LeaveManagement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveAlterationService {

    @Autowired
    private LeaveAlterationRepository leaveAlterationRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public String assignAlteration(LeaveAlterationDto dto) {
        LeaveAlteration alteration = new LeaveAlteration();

        // Set LeaveRequest
        LeaveRequest leaveRequest = leaveRequestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("LeaveRequest not found"));
        alteration.setLeaveRequest(leaveRequest);

        // Set Employee who is applying
        Employee employee = employeeRepository.findById(dto.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        alteration.setEmployee(employee);

        // Set Type and Details
        alteration.setAlterationType(dto.getAlterationType());

        // Handle Moodle Link
        if (dto.getAlterationType() == AlterationType.MOODLE_LINK) {
            alteration.setMoodleActivityLink(dto.getMoodleActivityLink());
            alteration.setNotificationStatus(null); // No approval needed
        }

        // Handle Staff Alteration
        if (dto.getAlterationType() == AlterationType.STAFF_ALTERATION) {
            Employee replacement = employeeRepository.findById(dto.getReplacementEmpId())
                    .orElseThrow(() -> new RuntimeException("Replacement Employee not found"));

            alteration.setReplacementEmployee(replacement);
            alteration.setNotificationStatus(NotificationStatus.PENDING); // Approval needed
        }

        alteration.setClassDate(dto.getClassDate());
        alteration.setClassPeriod(dto.getClassPeriod());
        alteration.setSubjectCode(dto.getSubjectCode());
        alteration.setSubjectName(dto.getSubjectName());
        System.out.println("Notification sent to replacement faculty (Emp ID: " + dto.getReplacementEmpId() + ")");
        leaveAlterationRepository.save(alteration); // ✅ Now this is valid!

        return "Alteration created successfully!";
    }
    public void approveAlteration(Integer alterationId) {
        LeaveAlteration alteration = leaveAlterationRepository.findById(alterationId)
                .orElseThrow(() -> new RuntimeException("Alteration not found"));

        // Only allow approval if it's PENDING
        if (alteration.getNotificationStatus() != NotificationStatus.PENDING) {
            throw new IllegalStateException("Alteration already processed.");
        }

        alteration.setNotificationStatus(NotificationStatus.APPROVED);
        leaveAlterationRepository.save(alteration);

        System.out.println("Alteration approved by replacement faculty (Emp ID: " +
                alteration.getReplacementEmployee().getEmpId() + ")");
    }

}


