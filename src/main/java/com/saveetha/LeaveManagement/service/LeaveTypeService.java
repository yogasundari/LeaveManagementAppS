package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.exception.LeaveTypeNotFoundException;
import com.saveetha.LeaveManagement.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveTypeService {

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    public LeaveType createLeaveType(LeaveType leaveType) {
        return leaveTypeRepository.save(leaveType);
    }

    public List<LeaveType> getAllLeaveTypes() {
        return leaveTypeRepository.findAll();
    }

    public Optional<LeaveType> getLeaveTypeById(Integer id) {
        return leaveTypeRepository.findById(id);
    }

    public LeaveType updateLeaveType(Integer id, LeaveType updatedLeaveType) {
        return leaveTypeRepository.findById(id).map(leaveType -> {
            leaveType.setTypeName(updatedLeaveType.getTypeName());
            leaveType.setMaxAllowedPerYear(updatedLeaveType.getMaxAllowedPerYear());
            leaveType.setMaxAllowedPerMonth(updatedLeaveType.getMaxAllowedPerMonth());
            leaveType.setMinAllowedDays(updatedLeaveType.getMinAllowedDays());
            leaveType.setAcademicYearStart(updatedLeaveType.getAcademicYearStart());
            leaveType.setAcademicYearEnd(updatedLeaveType.getAcademicYearEnd());
            leaveType.setCanBeCarriedForward(updatedLeaveType.getCanBeCarriedForward());
            leaveType.setMaxCarryForward(updatedLeaveType.getMaxCarryForward());
            leaveType.setActive(updatedLeaveType.getActive());
            return leaveTypeRepository.save(leaveType);
        }).orElseThrow(() -> new LeaveTypeNotFoundException("Leave Type not found with ID: " + id));
    }

    public void deleteLeaveType(Integer id) {
        if (!leaveTypeRepository.existsById(id)) {
            throw new LeaveTypeNotFoundException("Leave Type not found with ID: " + id);
        }
        leaveTypeRepository.deleteById(id);
    }

    public LeaveType setLeaveTypeStatus(Integer id, boolean status) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LeaveType not found with id: " + id));
        leaveType.setActive(status);
        return leaveTypeRepository.save(leaveType);

    }
}