package com.saveetha.LeaveManagement.dto;

public class LoginResponse {
    private String token;
    private String empId;
    private String role;

    public LoginResponse(String token, String empId, String role) {
        this.token = token;
        this.empId = empId;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getEmpId() {
        return empId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public void setToken(String token) {
        this.token = token;
    }
}