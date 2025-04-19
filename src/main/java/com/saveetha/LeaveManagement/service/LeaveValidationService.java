package com.saveetha.LeaveManagement.service;

import com.saveetha.LeaveManagement.dto.LeaveRequestDTO;
import com.saveetha.LeaveManagement.entity.Employee;
import com.saveetha.LeaveManagement.entity.EmployeeLeaveBalance;
import com.saveetha.LeaveManagement.entity.LeaveRequest;
import com.saveetha.LeaveManagement.entity.LeaveType;
import com.saveetha.LeaveManagement.enums.LeaveStatus;
import com.saveetha.LeaveManagement.repository.EmployeeLeaveBalanceRepository;
import com.saveetha.LeaveManagement.repository.EmployeeRepository;
import com.saveetha.LeaveManagement.repository.LeaveRequestRepository;
import com.saveetha.LeaveManagement.repository.LeaveTypeRepository;
import com.saveetha.LeaveManagement.utility.AcademicMonthCycleUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class LeaveValidationService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeLeaveBalanceRepository employeeLeaveBalanceRepository;
    private final AcademicMonthCycleUtil academicMonthCycleUtil ;

    public LeaveValidationService(LeaveRequestRepository leaveRequestRepository,AcademicMonthCycleUtil academicMonthCycleUtil, EmployeeRepository employeeRepository, LeaveTypeRepository leaveTypeRepository, EmployeeLeaveBalanceRepository employeeLeaveBalanceRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.academicMonthCycleUtil = academicMonthCycleUtil;
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.employeeLeaveBalanceRepository = employeeLeaveBalanceRepository;
    }

    public void validatePermissionLeave(LeaveRequestDTO leaveRequestdto) {

    }

    public void validateMedicalLeave(LeaveRequestDTO dto) {
        // Step 1: Ensure at least 3 consecutive days
        long requestedDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        if (requestedDays < 3) {
            throw new RuntimeException("Medical leave must be taken for 3 or more consecutive days.");
        }

        // Step 2: Ensure a medical certificate is uploaded
        if (dto.getFileUpload() == null || dto.getFileUpload().isEmpty()) {
            throw new RuntimeException("Medical leave requires a medical certificate to be uploaded.");
        }

        // Step 3: Retrieve Employee and LeaveType
        Employee employee = employeeRepository.findByEmpId(dto.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveType mlLeaveType = leaveTypeRepository.findByTypeNameIgnoreCase("ml")
                .orElseThrow(() -> new RuntimeException("Medical Leave type not found"));

        // Step 4: Get current leave balance
        EmployeeLeaveBalance balance = employeeLeaveBalanceRepository
                .findByEmployeeAndLeaveType(employee, mlLeaveType)
                .orElseThrow(() -> new RuntimeException("Medical Leave balance not found"));

        // Step 5: Prevent drafting if balance is zero
        if (balance.getBalanceLeave() <= 0) {
            throw new RuntimeException("You have no remaining Medical Leave balance.");
        }

        // Step 6: Check for overlapping approved/pending ML requests
        List<LeaveRequest> overlappingRequests = leaveRequestRepository.findOverlappingLeaveRequests(
                dto.getEmpId(),
                "ml",
                Arrays.asList(LeaveStatus.PENDING, LeaveStatus.APPROVED),
                dto.getStartDate(),
                dto.getEndDate()
        );
        if (!overlappingRequests.isEmpty()) {
            throw new RuntimeException("Overlapping Medical Leave request exists for the selected dates.");
        }

        // Step 7: Total used + pending leave must not exceed 6
        int totalUsedOrPendingML = leaveRequestRepository.sumTotalDaysOfPendingAndApprovedML(dto.getEmpId(), "ml");
        int futureTotal = totalUsedOrPendingML + (int) requestedDays;

        if (futureTotal > 6) {
            throw new RuntimeException("This request exceeds the allowed Medical Leave limit (6 days). " +
                    "Currently used/pending: " + totalUsedOrPendingML + ", Requested: " + requestedDays);
        }
    }


    public void validateEarnedLeave(LeaveRequestDTO dto) {
        // Step 1: Ensure at least 3 consecutive days
        long requestedDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        if (requestedDays < 3) {
            throw new RuntimeException("Earned Leave must be taken for 3 or more consecutive days.");
        }

        // Step 2: Retrieve Employee and LeaveType
        Employee employee = employeeRepository.findByEmpId(dto.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LeaveType elLeaveType = leaveTypeRepository.findByTypeNameIgnoreCase("el")
                .orElseThrow(() -> new RuntimeException("Earned Leave type not found"));

        // Step 3: Get current leave balance
        EmployeeLeaveBalance balance = employeeLeaveBalanceRepository
                .findByEmployeeAndLeaveType(employee, elLeaveType)
                .orElseThrow(() -> new RuntimeException("Earned Leave balance not found"));

        if (balance.getBalanceLeave() <= 0) {
            throw new RuntimeException("You have no remaining Earned Leave balance.");
        }

        // Step 4: Check for overlapping EL requests
        List<LeaveRequest> overlappingRequests = leaveRequestRepository.findOverlappingLeaveRequests(
                dto.getEmpId(),
                "el",
                Arrays.asList(LeaveStatus.PENDING, LeaveStatus.APPROVED),
                dto.getStartDate(),
                dto.getEndDate()
        );
        if (!overlappingRequests.isEmpty()) {
            throw new RuntimeException("Overlapping Earned Leave request exists for the selected dates.");
        }

        // Step 5: Total used + pending must not exceed 12
        int totalUsedOrPendingEL = leaveRequestRepository.sumTotalDaysOfPendingAndApprovedML(dto.getEmpId(), "el");
        int futureTotal = totalUsedOrPendingEL + (int) requestedDays;

        if (futureTotal > 12) {
            throw new RuntimeException("This request exceeds the allowed Earned Leave limit (12 days). " +
                    "Currently used/pending: " + totalUsedOrPendingEL + ", Requested: " + requestedDays);
        }
    }
    public void validateCompOff(LeaveRequestDTO dto) {
        // Step 1: Ensure earned date is provided
        if (dto.getEarnedDate() == null) {
            throw new RuntimeException("Comp Off leave requires an earned date.");
        }

        // Step 2: Earned date must be before the leave start date
        if (!dto.getEarnedDate().isBefore(dto.getStartDate())) {
            throw new RuntimeException("Earned Date must be before the leave Start Date.");
        }

        // Step 3: Retrieve the leave type ID for "comp off" from the database
        LeaveType compOffType = leaveTypeRepository.findByTypeNameIgnoreCase("comp off")
                .orElseThrow(() -> new RuntimeException("Comp Off leave type not found."));

        Integer compOffTypeId = compOffType.getLeaveTypeId();  // Get leaveTypeId (Integer)

        // Step 4: Check if the earned date is already used for another Comp Off request (Pending, Approved, or Draft)
        List<LeaveRequest> existingEarnedDateRequests = leaveRequestRepository.findCompOffRequestsByEarnedDate(
                dto.getEmpId(),
                compOffTypeId,  // Use the correct leaveTypeId for 'Comp Off'
                Arrays.asList(LeaveStatus.PENDING, LeaveStatus.APPROVED, LeaveStatus.DRAFT), // Checking for PENDING, APPROVED, or DRAFT status
                dto.getEarnedDate()
        );

        // If any requests exist, throw an exception to prevent using the same earned date again
        if (!existingEarnedDateRequests.isEmpty()) {
            throw new RuntimeException("The earned date you have already used");
        }

        // Step 5: Check for overlapping Comp Off leave requests (same leave type, start date, end date)
        List<LeaveRequest> overlappingRequests = leaveRequestRepository.findOverlappingLeaveRequestsByTypeId(
                dto.getEmpId(),
                compOffTypeId,  // Pass the Integer leaveTypeId here
                Arrays.asList(LeaveStatus.PENDING, LeaveStatus.APPROVED, LeaveStatus.DRAFT), // Checking for PENDING, APPROVED, or DRAFT status
                dto.getStartDate(),
                dto.getEndDate()
        );

        // If any overlapping requests are found, throw an exception
        if (!overlappingRequests.isEmpty()) {
            throw new RuntimeException("Overlapping Comp Off leave request exists for the selected dates.");
        }
    }
    public void validateCasualLeave(LeaveRequestDTO dto) {
        String empId = dto.getEmpId();
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();

        // Step 1: Get Casual Leave Type
        LeaveType clType = leaveTypeRepository.findByTypeNameIgnoreCase("CL")
                .orElseThrow(() -> new RuntimeException("Casual Leave type not found"));
        Integer clTypeId = clType.getLeaveTypeId();

        // Step 2: Get academic cycle start date (e.g., May 26th to next May 25yh)
        LocalDate academicYearStart = academicMonthCycleUtil.getAcademicYearStart();

        // Step 3: Calculate how many CLs earned until now in the academic year
        long monthsSinceStart = ChronoUnit.MONTHS.between(
                academicYearStart.withDayOfMonth(25),
                LocalDate.now().withDayOfMonth(25)
        ) + 1;

        int totalEarnedCL = (int) monthsSinceStart;

        // Step 4: Get number of used CLs (DRAFT, PENDING, APPROVED) in current academic year
        List<LeaveRequest> usedCLs = leaveRequestRepository.findUsedCLsForEmployeeInAcademicYear(
                empId,
                clTypeId,
                Arrays.asList(LeaveStatus.DRAFT, LeaveStatus.PENDING, LeaveStatus.APPROVED),
                academicYearStart,
                LocalDate.now()
        );

        if (usedCLs.size() >= totalEarnedCL) {
            throw new RuntimeException("You have already used all your earned CLs for this academic year.");
        }

        // Step 5: Prevent overlapping CL requests
        List<LeaveRequest> overlappingRequests = leaveRequestRepository.findOverlappingLeaveRequestsByTypeId(
                empId,
                clTypeId,
                Arrays.asList(LeaveStatus.DRAFT, LeaveStatus.PENDING, LeaveStatus.APPROVED),
                startDate,
                endDate
        );

        if (!overlappingRequests.isEmpty()) {
            throw new RuntimeException("A CL request already exists for the selected date range.");
        }
    }

    public void validatelop(LeaveRequestDTO leaveRequestDTO){

    }
    public void validatevacation(LeaveRequestDTO leaveRequestDTO){

    }
    public void validatelate(LeaveRequestDTO leaveRequestDTO){

    }
}
