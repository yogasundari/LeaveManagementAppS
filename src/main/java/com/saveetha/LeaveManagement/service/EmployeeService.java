package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.dto.EmployeeUpdateDTO;
import com.saveetha.LeaveManagement.entity.ApprovalFlow;
import com.saveetha.LeaveManagement.entity.Department;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.enums.StaffType;
import com.saveetha.LeaveManagement.repository.ApprovalFlowRepository;
import com.saveetha.LeaveManagement.repository.DepartmentRepository;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveBalanceUtilService leaveBalanceUtilService;
    private final ApprovalFlowRepository approvalFlowRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           LeaveBalanceUtilService leaveBalanceUtilService,
                           ApprovalFlowRepository approvalFlowRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.leaveBalanceUtilService = leaveBalanceUtilService;
        this.approvalFlowRepository = approvalFlowRepository;
    }

    public boolean updateEmployee(String empId, EmployeeUpdateDTO employeeUpdateDTO) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmpId(empId);

        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            boolean isJoiningDateUpdated = employeeUpdateDTO.getJoiningDate() != null && employee.getJoiningDate() == null;

            // ✅ Update fields only if they are not null
            if (employeeUpdateDTO.getEmpName() != null) {
                employee.setEmpName(employeeUpdateDTO.getEmpName());
            }
            if (employeeUpdateDTO.getDesignation() != null) {
                employee.setDesignation(employeeUpdateDTO.getDesignation());
            }
            if (employeeUpdateDTO.getDepartmentId() != null) {
                Department department = departmentRepository.findById(employeeUpdateDTO.getDepartmentId())
                        .orElseThrow(() -> new RuntimeException("Department not found"));
                employee.setDepartment(department);
            }

            if (employeeUpdateDTO.getApprovalFlowId() != null) {
                ApprovalFlow flow = approvalFlowRepository.findById(employeeUpdateDTO.getApprovalFlowId())
                        .orElseThrow(() -> new RuntimeException("Approval Flow not found"));
                employee.setApprovalFlow(flow);
            }

            if (employeeUpdateDTO.getProfilePicture() != null) {
                employee.setProfilePicture(employeeUpdateDTO.getProfilePicture());
            }

            if (employeeUpdateDTO.getStaffType() != null) {
                try {
                    StaffType staffType = StaffType.valueOf(employeeUpdateDTO.getStaffType().toUpperCase());
                    employee.setStaffType(staffType);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid staff type provided");
                }
            }

            if (isJoiningDateUpdated) {
                employee.setJoiningDate(employeeUpdateDTO.getJoiningDate());
                System.out.println("Initializing leave balance for Employee ID: " + empId);
                System.out.println("New Joining Date: " + employeeUpdateDTO.getJoiningDate());
                leaveBalanceUtilService.initializeLeaveBalance(employee);
                System.out.println("✅ Leave balance initialization function called!");
            }

            employeeRepository.save(employee);
            return true;
        }

        return false;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(String empId) {
        return employeeRepository.findByEmpId(empId);
    }

    // ✅ Soft delete (set active = false)
    public boolean deactivateEmployee(String empId) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmpId(empId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setActive(false);
            employeeRepository.save(employee);
            return true;
        }
        return false;
    }

    // ✅ Reactivate (set active = true)
    public boolean activateEmployee(String empId) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmpId(empId);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            employee.setActive(true);
            employeeRepository.save(employee);
            return true;
        }
        return false;
    }
    // ✅ Optional: Hard delete (not recommended)
    public boolean deleteEmployee(String empId) {
        Optional<Employee> optionalEmployee = employeeRepository.findByEmpId(empId);
        if (optionalEmployee.isPresent()) {
            employeeRepository.delete(optionalEmployee.get());
            return true;
        }
        return false;
    }
}
