package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.LeaveAlteration;
import com.saveetha.LeaveManagement.enums.NotificationStatus;
import com.saveetha.LeaveManagement.repository.LeaveAlterationRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LeaveAlterationService {

    @Autowired
    private LeaveAlterationRepository leaveAlterationRepository;

    @Transactional
    public LeaveAlteration requestAlteration(LeaveAlteration alteration) {
        // Send Email/Notification Logic (Placeholder)
        System.out.println("Sending email to replacement faculty: " + alteration.getReplacementEmployee().getEmpId());

        alteration.setNotificationStatus(NotificationStatus.PENDING);
        return leaveAlterationRepository.save(alteration);
    }

    @Transactional
    public LeaveAlteration approveAlteration(Integer alterationId) {
        Optional<LeaveAlteration> optionalAlteration = leaveAlterationRepository.findById(alterationId);

        if (optionalAlteration.isEmpty()) {
            throw new IllegalArgumentException("Alteration request not found.");
        }

        LeaveAlteration alteration = optionalAlteration.get();
        alteration.setNotificationStatus(NotificationStatus.APPROVED);

        return leaveAlterationRepository.save(alteration);
    }
}
