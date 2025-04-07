package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.LeaveAlteration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveAlterationRepository extends JpaRepository<LeaveAlteration, Integer> {
    List<LeaveAlteration> findByLeaveRequestRequestId(Integer requestId);
    List<LeaveAlteration> findByEmployeeEmpIdAndLeaveRequestClassDateAndLeaveRequestClassPeriod(
            String empId, LocalDate classDate, String classPeriod
    );

}
