package com.example.healthcare.Model;

public class PatientInfo {
    private String patientName;
    private String patientEmail;
    private String patientGender;
    private String userType;
    private String patientUid;
    private String imageUrl;

    public PatientInfo() {
    }

    public PatientInfo(String patientName, String patientEmail, String patientGender, String userType ,String patientUid ,String imageUrl) {
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientGender = patientGender;
        this.userType = userType;
        this.patientUid=patientUid;
        this.imageUrl=imageUrl;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPatientUid() {
        return patientUid;
    }

    public void setPatientUid(String patientUid) {
        this.patientUid = patientUid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
