package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.dto.EmployeeUpdateDTO;
import com.saveetha.LeaveManagement.entity.Department;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.enums.StaffType;
import com.saveetha.LeaveManagement.repository.DepartmentRepository;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final LeaveBalanceUtilService leaveBalanceUtilService ;


    public EmployeeService(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository,LeaveBalanceUtilService leaveBalanceUtilService) {

        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.leaveBalanceUtilService = leaveBalanceUtilService;
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
                employee.setDepartment(department); // ✅ Correct method
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

                // ✅ If joining date is updated, initialize leave balance
                if (isJoiningDateUpdated) {
                    employee.setJoiningDate(employeeUpdateDTO.getJoiningDate());
                    System.out.println("Initializing leave balance for Employee ID: " + empId);
                    System.out.println("New Joining Date: " + employeeUpdateDTO.getJoiningDate());
                    leaveBalanceUtilService.initializeLeaveBalance(employee);
                    System.out.println("✅ Leave balance initialization function called!");

                }
            }

            // ✅ Save updated employee details
            employeeRepository.save(employee);
            return true;
        }
        return false;
    }
}
