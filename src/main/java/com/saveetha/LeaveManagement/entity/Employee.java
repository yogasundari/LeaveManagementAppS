package com.saveetha.LeaveManagement.entity;

import com.saveetha.LeaveManagement.enums.Role;
import com.saveetha.LeaveManagement.enums.StaffType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@Table(name = "employee")
public class Employee {

    @Id
    @Column(length = 10, nullable = false)
    private String empId; // Employee ID

    // amazonq-ignore-next-line
    @Column(nullable = true, length = 100)
    private String empName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    private String designation;

    @ManyToOne
    @JoinColumn(name = "dept_id", nullable = true) // Department is nullable during registration
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false , length = 50)
    private Role role = Role.EMPLOYEE; // Default role: EMPLOYEE

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private StaffType staffType;

    private String profilePicture;

    @ManyToOne
    @JoinColumn(name = "approval_flow_id", nullable = true)
    private ApprovalFlow approvalFlow;

    private Timestamp lastLogin;

    @Column(nullable = false)
    private boolean active = true; // Default: Employee is active

    private Timestamp createdAt;
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public StaffType getStaffType() {
		return staffType;
	}

	public void setStaffType(StaffType staffType) {
		this.staffType = staffType;
	}

	public String getProfilePicture() {
		return profilePicture;
	}

	public void setProfilePicture(String profilePicture) {
		this.profilePicture = profilePicture;
	}

	public ApprovalFlow getApprovalFlow() {
		return approvalFlow;
	}

	public void setApprovalFlow(ApprovalFlow approvalFlow) {
		this.approvalFlow = approvalFlow;
	}

	public Timestamp getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Timestamp lastLogin) {
		this.lastLogin = lastLogin;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}
    
    
}
