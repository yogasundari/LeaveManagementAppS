package com.saveetha.LeaveManagement.repository;

import com.saveetha.LeaveManagement.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Integer> {


    @Query("SELECT lt FROM LeaveType lt WHERE LOWER(lt.typeName) = LOWER(:typeName) AND lt.active = true")
    Optional<LeaveType> findByTypeNameIgnoreCase(@Param("typeName") String typeName);

    List<LeaveType> findByActiveTrue();

}
