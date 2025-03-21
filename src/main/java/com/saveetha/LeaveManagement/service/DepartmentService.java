package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.entity.Department;
import com.saveetha.LeaveManagement.exception.ResourceAlreadyExistsException;
import com.saveetha.LeaveManagement.exception.ResourceNotFoundException;
import com.saveetha.LeaveManagement.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public List<Department> getAllActiveDepartments() {
        return departmentRepository.findByActive(true);
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    @Transactional
    public Department createDepartment(Department department) {
        // Check if department with same name already exists
        if (departmentRepository.existsByDeptName(department.getDeptName())) {
            throw new ResourceAlreadyExistsException("Department already exists with name: " + department.getDeptName());
        }

        // Set default values
        department.setActive(true);

        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Long id, Department departmentDetails) {
        Department existingDepartment = getDepartmentById(id);

        // Check if another department with the same name exists
        if (!existingDepartment.getDeptName().equals(departmentDetails.getDeptName()) &&
                departmentRepository.existsByDeptName(departmentDetails.getDeptName())) {
            throw new ResourceAlreadyExistsException("Department already exists with name: " + departmentDetails.getDeptName());
        }

        existingDepartment.setDeptName(departmentDetails.getDeptName());
        existingDepartment.setDeptType(departmentDetails.getDeptType());

        return departmentRepository.save(existingDepartment);
    }

    @Transactional
    public Department deactivateDepartment(Long id) {
        Department department = getDepartmentById(id);
        department.setActive(false);
        return departmentRepository.save(department);
    }

    @Transactional
    public Department activateDepartment(Long id) {
        Department department = getDepartmentById(id);
        department.setActive(true);
        return departmentRepository.save(department);
    }
}