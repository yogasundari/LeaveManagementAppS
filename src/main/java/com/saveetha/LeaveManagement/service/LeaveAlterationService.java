package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.dto.LeaveAlterationDTO;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.LeaveAlteration;
import com.saveetha.LeaveManagement.entity.LeaveRequest;
import com.saveetha.LeaveManagement.enums.AlterationType;
import com.saveetha.LeaveManagement.enums.NotificationStatus;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import com.saveetha.LeaveManagement.repository.LeaveAlterationRepository;
import com.saveetha.LeaveManagement.repository.LeaveRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LeaveAlterationService {

    @Autowired
    private LeaveAlterationRepository leaveAlterationRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    public boolean isAlterationCompleted(LeaveRequest leaveRequest) {
        return leaveAlterationRepository.findByLeaveRequest(leaveRequest)
                .map(alteration -> alteration.getNotificationStatus() == NotificationStatus.APPROVED)
                .orElse(false);
    }

    public LeaveAlteration createLeaveAlteration(LeaveAlterationDTO alterationDTO) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(alterationDTO.getRequestId())
                .orElseThrow(() -> new RuntimeException("Leave Request not found"));

        Employee employee = employeeRepository.findById(alterationDTO.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveAlteration alteration = new LeaveAlteration();
        alteration.setLeaveRequest(leaveRequest);
        alteration.setEmployee(employee);
        alteration.setAlterationType(alterationDTO.getAlterationType());

        if (alterationDTO.getAlterationType() == AlterationType.MOODLE_LINK) {
            alteration.setMoodleActivityLink(alterationDTO.getMoodleActivityLink());
        } else if (alterationDTO.getAlterationType() == AlterationType.STAFF_ALTERATION) {
            Employee replacementEmployee = employeeRepository.findById(alterationDTO.getReplacementEmpId())
                    .orElseThrow(() -> new RuntimeException("Replacement Employee not found"));
            alteration.setReplacementEmployee(replacementEmployee);
        }

        alteration.setNotificationStatus(NotificationStatus.PENDING);
        alteration.setActive(true);
        alteration.setCreatedAt(LocalDateTime.now());
        alteration.setUpdatedAt(LocalDateTime.now());

        return leaveAlterationRepository.save(alteration);
    }
}
