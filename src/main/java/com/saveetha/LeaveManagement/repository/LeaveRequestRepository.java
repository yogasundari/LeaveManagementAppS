package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.LeaveRequest;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Integer> {

    // Get all leave requests by employee
    List<LeaveRequest> findByEmployeeEmpId(String empId);

    // Optional: Get all active requests
    List<LeaveRequest> findByActiveTrue();


    @Query("SELECT COALESCE(SUM(DATEDIFF(lr.endDate, lr.startDate) + 1), 0) FROM LeaveRequest lr " +
            "WHERE lr.employee.empId = :empId " +
            "AND LOWER(lr.leaveType.typeName) = LOWER(:leaveTypeName) " +
            "AND lr.status IN ('PENDING', 'APPROVED')")
    int sumTotalDaysOfPendingAndApprovedML(
            @Param("empId") String empId,
            @Param("leaveTypeName") String leaveTypeName
    );

    @Query("SELECT lr FROM LeaveRequest lr " +
            "WHERE lr.employee.empId = :empId " +
            "AND LOWER(lr.leaveType.typeName) = LOWER(:leaveType) " +
            "AND lr.status IN :statuses " +
            "AND lr.startDate <= :endDate " +
            "AND lr.endDate >= :startDate")
    List<LeaveRequest> findOverlappingLeaveRequests(
            @Param("empId") String empId,
            @Param("leaveType") String leaveType,
            @Param("statuses") List<LeaveStatus> statuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    // You can add more methods as needed (e.g., find by date range, leave type, etc.)
}
