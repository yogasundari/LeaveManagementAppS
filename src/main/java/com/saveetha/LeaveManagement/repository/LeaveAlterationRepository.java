package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.LeaveAlteration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LeaveAlterationRepository extends JpaRepository<LeaveAlteration, Integer> {


    List<LeaveAlteration> findByLeaveRequest_RequestId(Integer requestId);


    // Get alterations by empId, date, and classPeriod
    List<LeaveAlteration> findByEmployeeEmpIdAndClassDateAndClassPeriod(
            String empId, LocalDate classDate, String classPeriod);

    // Get pending approvals for a replacement employee
    List<LeaveAlteration> findByReplacementEmployeeEmpIdAndNotificationStatus(
            String empId, com.saveetha.LeaveManagement.enums.NotificationStatus status);
}
