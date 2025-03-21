package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {
    Optional<LeaveType> findByTypeName(String typeName);
}
