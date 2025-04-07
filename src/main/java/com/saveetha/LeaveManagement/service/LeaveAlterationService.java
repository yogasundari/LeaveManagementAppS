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

import java.time.LocalDate;
import java.util.List;

@Service
public class LeaveAlterationService {

    @Autowired
    private LeaveAlterationRepository alterationRepository;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public LeaveAlteration createAlteration(LeaveAlterationDTO leaveAlterationDTO) {
        LeaveRequest request = leaveRequestRepository.findById(leaveAlterationDTO.getRequestId())
                .orElseThrow(() -> new RuntimeException("Leave request not found"));

        Employee employee = employeeRepository.findById(leaveAlterationDTO.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveAlteration alteration = new LeaveAlteration();
        alteration.setLeaveRequest(request);
        alteration.setEmployee(employee);
        alteration.setAlterationType(leaveAlterationDTO.getAlterationType());

        if (leaveAlterationDTO.getAlterationType().name().equals("MOODLE_LINK")) {
            alteration.setMoodleActivityLink(leaveAlterationDTO.getMoodleActivityLink());
        } else if (leaveAlterationDTO.getAlterationType().name().equals("STAFF_ALTERATION")) {
            Employee replacement = employeeRepository.findById(leaveAlterationDTO.getReplacementEmpId())
                    .orElseThrow(() -> new RuntimeException("Replacement employee not found"));
            alteration.setReplacementEmployee(replacement);
            alteration.setNotificationStatus(NotificationStatus.PENDING);
        }

        return alterationRepository.save(alteration);
    }

    public boolean isAlterationCompleted(String empId, LocalDate classDate, String classPeriod) {
        List<LeaveAlteration> alterations = alterationRepository
                .findByEmployeeEmpIdAndLeaveRequestClassDateAndLeaveRequestClassPeriod(empId, classDate, classPeriod);

        if (alterations.isEmpty()) return false; // ✅ Don't allow leave if no alteration created

        return alterations.stream().allMatch(alteration ->
                (alteration.getAlterationType() == AlterationType.MOODLE_LINK && alteration.getMoodleActivityLink() != null)
                        || (alteration.getAlterationType() == AlterationType.STAFF_ALTERATION
                        && alteration.getNotificationStatus() == NotificationStatus.APPROVED));
    }



    public void approveAlteration(Integer alterationId) {
        LeaveAlteration alteration = alterationRepository.findById(alterationId)
                .orElseThrow(() -> new RuntimeException("Alteration not found"));
        alteration.setNotificationStatus(NotificationStatus.APPROVED);
        alterationRepository.save(alteration);
    }
}
