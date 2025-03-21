package com.saveetha.LeaveManagement.entity;

import com.saveetha.LeaveManagement.enums.LeaveStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "LeaveRequest")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id", nullable = false, unique = true)
    private Integer requestId; // Primary Key

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee; // Foreign Key reference to Employee

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType; // Foreign Key reference to LeaveType

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private LocalTime startTime;
    private LocalTime endTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    private LocalDate earnedDate;
    private String classPeriod;
    private LocalDate classDate;
    private String subjectName;
    private String subjectCode;
    private String fileUpload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LeaveStatus status = LeaveStatus.PENDING; // Default status: Pending

    @Column(nullable = false)
    private Boolean active = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
