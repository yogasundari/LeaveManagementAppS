package com.saveetha.LeaveManagement.dto;

public class LoginResponse {
    private String token;
    private String empId;
    private String role;
    private String email;

    public LoginResponse(String token, String empId, String role,String email) {
        this.token = token;
        this.empId = empId;
        this.role = role;
        this.email= email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
    }
}