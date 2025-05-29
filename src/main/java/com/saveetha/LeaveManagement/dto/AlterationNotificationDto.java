package com.saveetha.LeaveManagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AlterationNotificationDto {

    @JsonProperty("alterationId")
    private Integer alterationId;

    @JsonProperty("empId")
    private String empId;

    @JsonProperty("requesterName")
    private String requesterName;

    @JsonProperty("subjectName")
    private String subjectName;

    @JsonProperty("classDate")
    private String classDate;

    @JsonProperty("classPeriod")
    private String classPeriod;

    @JsonProperty("message")
    private String message;

    // Default constructor
    public AlterationNotificationDto() {}

    // Constructor with all fields
    public AlterationNotificationDto(Integer alterationId, String empId, String requesterName,
                                     String subjectName, String classDate, String classPeriod, String message) {
        this.alterationId = alterationId;
        this.empId = empId;
        this.requesterName = requesterName;
        this.subjectName = subjectName;
        this.classDate = classDate;
        this.classPeriod = classPeriod;
        this.message = message;
    }

    // Getters and Setters
    public Integer getAlterationId() {
        return alterationId;
    }

    public void setAlterationId(Integer alterationId) {
        this.alterationId = alterationId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getClassDate() {
        return classDate;
    }

    public void setClassDate(String classDate) {
        this.classDate = classDate;
    }

    public String getClassPeriod() {
        return classPeriod;
    }

    public void setClassPeriod(String classPeriod) {
        this.classPeriod = classPeriod;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AlterationNotificationDto{" +
                "alterationId=" + alterationId +
                ", empId='" + empId + '\'' +
                ", requesterName='" + requesterName + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", classDate='" + classDate + '\'' +
                ", classPeriod='" + classPeriod + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}