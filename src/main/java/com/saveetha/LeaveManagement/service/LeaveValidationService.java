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
import com.saveetha.LeaveManagement.utility.MonthRange;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        // Step 2: Academic year start
        LocalDate academicStart = academicMonthCycleUtil.getAcademicYearStart();
        LocalDate today = LocalDate.now();
        // — Step 1: Must be a one-day request
        if (!leaveRequestdto.getStartDate().equals(leaveRequestdto.getEndDate())) {
            throw new RuntimeException("Permission Leave must be for exactly one day.");
        }
        LocalDate day = leaveRequestdto.getStartDate();

        // — Step 2: Load Employee
        Employee emp = employeeRepository.findByEmpId(leaveRequestdto.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // — Step 3: Load Permission LeaveType
        LeaveType permType = leaveTypeRepository.findByTypeNameIgnoreCase("permission")
                .orElseThrow(() -> new RuntimeException("Permission Leave type not found"));

        // — Step 4: Load Leave Balance
        EmployeeLeaveBalance bal = employeeLeaveBalanceRepository
                .findByEmployeeAndLeaveType(emp, permType)
                .orElseThrow(() -> new RuntimeException("Permission Leave balance not found"));

        if (bal.getBalanceLeave() <= 0) {
            throw new RuntimeException("You have no remaining Permission Leave balance.");
        }

        // — Step 5: Check overlapping leaves
        boolean hasOverlap = leaveRequestRepository.existsByEmployeeAndDate(emp.getEmpId(), day);
        if (hasOverlap) {
            throw new RuntimeException("You already have a leave on " + day);
        }

        // — Step 6: Find current academic-month
        List<MonthRange> ranges = buildAcademicMonthRanges(academicStart, LocalDate.now());
        MonthRange currentMonth = ranges.stream()
                .filter(r -> !day.isBefore(r.getStart()) && !day.isAfter(r.getEnd()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Date not in any academic month"));

        // — Step 7: Count used in this academic-month
        int usedThisMonth = leaveRequestRepository.countPermissionLeavesInRange(
                emp.getEmpId(),
                currentMonth.getStart(),
                currentMonth.getEnd()
        );
        if (usedThisMonth >= 2) {
            throw new RuntimeException(
                    String.format("Permission limit for %s–%s reached: already used %d of 2",
                            currentMonth.getStart(),
                            currentMonth.getEnd(),
                            usedThisMonth)
            );
        }

        // If you reach here, the request is valid.
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
        boolean isHalfDay = dto.isHalfDay();

        // Step 1: Get CL leave type ID
        LeaveType clType = leaveTypeRepository.findByTypeNameIgnoreCase("CL")
                .orElseThrow(() -> new RuntimeException("CL leave type not found"));
        Integer clTypeId = clType.getLeaveTypeId();

        // Step 2: Academic year start
        LocalDate academicStart = academicMonthCycleUtil.getAcademicYearStart();
        LocalDate today = LocalDate.now();

        // Step 3: Build academic month ranges
        List<MonthRange> academicMonths = buildAcademicMonthRanges(academicStart, today);

        // Step 4: Get employee joining date
        Employee employee = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        LocalDate joiningDate = employee.getJoiningDate();

        // Step 5: Count completed academic months (excluding joining month)
        int completedMonths = 0;
        for (MonthRange month : academicMonths) {
            if (!month.getStart().isAfter(today)) {
                // Skip joining month
                if (joiningDate != null &&
                        !joiningDate.isBefore(month.getStart()) &&
                        !joiningDate.isAfter(month.getEnd())) {
                    continue;
                }
                completedMonths++;
            }
        }

        // Step 6: Count CLs already used (as decimal values)
        double clUsed = leaveRequestRepository.countTotalCLsUsed(
                empId,
                clTypeId,
                Arrays.asList(LeaveStatus.DRAFT, LeaveStatus.PENDING, LeaveStatus.APPROVED),
                academicStart,
                today
        );

        // Step 7: Calculate requested CL days
        double requestedCLDays =calculateLeaveDays(startDate, endDate, isHalfDay);

        // Step 8: Validate against available CLs
        if (clUsed + requestedCLDays > completedMonths) {
            throw new RuntimeException("CL limit exceeded. You have " + completedMonths + " CL(s) available, used " + clUsed + ", trying to use " + requestedCLDays + ".");
        }

        // Step 9: Prevent overlapping CLs
        List<LeaveRequest> overlappingRequests = leaveRequestRepository.findOverlappingLeaveRequestsByTypeId(
                empId, clTypeId,
                Arrays.asList(LeaveStatus.DRAFT, LeaveStatus.PENDING, LeaveStatus.APPROVED),
                startDate, endDate
        );
        if (!overlappingRequests.isEmpty()) {
            throw new RuntimeException("A CL request already exists for the selected date range.");
        }
    }

    private double calculateLeaveDays(LocalDate startDate, LocalDate endDate, boolean isHalfDay) {
        if (startDate.equals(endDate)) {
            return isHalfDay ? 0.5 : 1.0;  // If it's a single day leave, return 0.5 for half-day and 1.0 for full-day.
        }
        long totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return isHalfDay ? totalDays * 0.5 : totalDays;  // For half-day, return 0.5 of the total days.
    }

    private List<MonthRange> buildAcademicMonthRanges(LocalDate academicStart, LocalDate now) {
        List<MonthRange> ranges = new ArrayList<>();

        // Academic cycle is fixed: May 26 to May 25 of next year
        int year = academicStart.getYear();
        boolean isLeap = Year.of(year + 1).isLeap(); // Feb in next calendar year

        LocalDate[] startDates = new LocalDate[]{
                LocalDate.of(year, 5, 26),
                LocalDate.of(year, 6, 25),
                LocalDate.of(year, 7, 26),
                LocalDate.of(year, 8, 26),
                LocalDate.of(year, 9, 25),
                LocalDate.of(year, 10, 26),
                LocalDate.of(year, 11, 25),
                LocalDate.of(year, 12, 26),
                LocalDate.of(year + 1, 1, 26),
                isLeap ? LocalDate.of(year + 1, 2, 24) : LocalDate.of(year + 1, 2, 23),
                LocalDate.of(year + 1, 3, 26),
                LocalDate.of(year + 1, 4, 25)
        };

        LocalDate[] endDates = new LocalDate[]{
                LocalDate.of(year, 6, 24),
                LocalDate.of(year, 7, 25),
                LocalDate.of(year, 8, 25),
                LocalDate.of(year, 9, 24),
                LocalDate.of(year, 10, 25),
                LocalDate.of(year, 11, 24),
                LocalDate.of(year, 12, 25),
                LocalDate.of(year + 1, 1, 25),
                isLeap ? LocalDate.of(year + 1, 2, 23) : LocalDate.of(year + 1, 2, 22),
                LocalDate.of(year + 1, 3, 25),
                LocalDate.of(year + 1, 4, 24),
                LocalDate.of(year + 1, 5, 25)
        };

        for (int i = 0; i < startDates.length; i++) {
            LocalDate start = startDates[i];
            LocalDate end = endDates[i];

            if (start.isAfter(now)) break;
            if (end.isAfter(now)) end = now;

            ranges.add(new MonthRange(start, end,now));
        }

        return ranges;
    }



    public void validatelop(LeaveRequestDTO leaveRequestDTO){

    }
    public void validatevacation(LeaveRequestDTO leaveRequestDTO){

    }
    public void validatelate(LeaveRequestDTO leaveRequestDTO){

        LocalDate academicStart = academicMonthCycleUtil.getAcademicYearStart();
        LocalDate today = LocalDate.now();

        // — Step 1: Must be a one-day request
        if (!leaveRequestDTO.getStartDate().equals(leaveRequestDTO.getEndDate())) {
            throw new RuntimeException("Late Leave must be for exactly one day.");
        }
        LocalDate day = leaveRequestDTO.getStartDate();

        // — Step 2: Load Employee
        Employee emp = employeeRepository.findByEmpId(leaveRequestDTO.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // — Step 3: Load late LeaveType
        LeaveType permType = leaveTypeRepository.findByTypeNameIgnoreCase("late")
                .orElseThrow(() -> new RuntimeException("Late Leave type not found"));

        // — Step 4: Load Leave Balance
        EmployeeLeaveBalance bal = employeeLeaveBalanceRepository
                .findByEmployeeAndLeaveType(emp, permType)
                .orElseThrow(() -> new RuntimeException("Late Leave balance not found"));

        if (bal.getBalanceLeave() <= 0) {
            throw new RuntimeException("You have no remaining Late Leave balance.");
        }

        // — Step 5: Check overlapping leaves
        boolean hasOverlap = leaveRequestRepository.existsByEmployeeAndDate(emp.getEmpId(), day);
        if (hasOverlap) {
            throw new RuntimeException("You already have a leave on " + day);
        }

        // — Step 6: Find current academic-month
        List<MonthRange> ranges = buildAcademicMonthRanges(academicStart, LocalDate.now());
        MonthRange currentMonth = ranges.stream()
                .filter(r -> !day.isBefore(r.getStart()) && !day.isAfter(r.getEnd()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Date not in any academic month"));

        // — Step 7: Count used in this academic-month
        int usedThisMonth = leaveRequestRepository.countlateInRange(
                emp.getEmpId(),
                currentMonth.getStart(),
                currentMonth.getEnd()
        );
        if (usedThisMonth >= 2) {
            throw new RuntimeException(
                    String.format("late limit for %s–%s reached: already used %d of 2",
                            currentMonth.getStart(),
                            currentMonth.getEnd(),
                            usedThisMonth)
            );
        }

    }
}
