package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.LeaveApproval;
import com.saveetha.LeaveManagement.entity.LeaveRequest;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveApprovalRepository extends JpaRepository<LeaveApproval, Integer> {

    List<LeaveApproval> findByLeaveRequest(LeaveRequest leaveRequest);

    List<LeaveApproval> findByLeaveRequestAndStatus(LeaveRequest leaveRequest, ApprovalStatus status);

    Optional<LeaveApproval> findByLeaveRequestAndApprover(LeaveRequest leaveRequest, Employee approver);

    List<LeaveApproval> findByApprover(Employee approver);
    List<LeaveApproval> findByLeaveRequest_RequestIdOrderByApprovalFlowLevel_SequenceAsc(Integer leaveRequestId);



    List<LeaveApproval> findByLeaveRequest_RequestId(Integer requestId);

    boolean existsByLeaveRequestAndApproverAndStatus(LeaveRequest leaveRequest, Employee approver, ApprovalStatus status);
    @Query("SELECT a FROM LeaveApproval a WHERE a.approver.empId = :empId AND a.status = 'PENDING'")
    List<LeaveApproval> findPendingApprovalsForApprover(@Param("empId") String empId);


}
