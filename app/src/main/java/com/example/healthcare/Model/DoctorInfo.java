package com.example.healthcare.Model;

public class DoctorInfo {
    String doctorName;
    String doctorEmail;
    String doctorSpecification;
    String user_type;
    String reg_id;
    String doctorUid;
    String imageUrl;


    public DoctorInfo() {
    }

    public DoctorInfo(String doctorName, String doctorEmail, String doctorSpecification, String user_type, String reg_id, String doctorUid, String imageUrl) {
        this.doctorName = doctorName;
        this.doctorEmail = doctorEmail;
        this.doctorSpecification = doctorSpecification;
        this.user_type = user_type;
        this.reg_id = reg_id;
        this.doctorUid=doctorUid;
        this.imageUrl=imageUrl;

    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
    }

    public String getDoctorSpecification() {
        return doctorSpecification;
    }

    public void setDoctorSpecification(String doctorSpecification) {
        this.doctorSpecification = doctorSpecification;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getReg_id() {
        return reg_id;
    }

    public void setReg_id(String reg_id) {
        this.reg_id = reg_id;
    }

    public String getDoctorUid() {
        return doctorUid;
    }

    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
