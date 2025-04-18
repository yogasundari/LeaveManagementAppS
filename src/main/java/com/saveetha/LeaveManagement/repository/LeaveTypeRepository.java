package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {
    Optional<LeaveType> findByTypeName(String typeName);
    @Query("SELECT lt FROM LeaveType lt WHERE lt.active = true")
    List<LeaveType> findAllActive();
    Optional<LeaveType> findByTypeNameIgnoreCase(String typeName);
    LeaveType findByAcademicYearStartAndAcademicYearEnd(LocalDate start, LocalDate end);
}
