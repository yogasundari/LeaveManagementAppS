package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.ApprovalFlowLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApprovalFlowLevelRepository extends JpaRepository<ApprovalFlowLevel, Integer> {
    List<ApprovalFlowLevel> findByApprovalFlowApprovalFlowId(Integer approvalFlowId);
    List<ApprovalFlowLevel> findByApprovalFlow_ApprovalFlowIdOrderBySequenceAsc(Integer approvalFlowId);
    List<ApprovalFlowLevel> findByActiveTrue();

}
