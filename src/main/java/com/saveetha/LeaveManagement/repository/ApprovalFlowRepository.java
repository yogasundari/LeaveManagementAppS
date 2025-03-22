package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.ApprovalFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalFlowRepository extends JpaRepository<ApprovalFlow, Integer> {

    // Custom query to find active approval flows
    List<ApprovalFlow> findByActiveTrue();

    // Check if an approval flow with a given name exists
    boolean existsByName(String name);
}

