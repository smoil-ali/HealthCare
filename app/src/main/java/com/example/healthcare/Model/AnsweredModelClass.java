package com.example.healthcare.Model;

public class AnsweredModelClass {
    String doctorUid;
    String nameOfDoctor;
    String Answer;
    String pushKey;




    public AnsweredModelClass() {
    }

    public String getDoctorUid() {
        return doctorUid;
    }

    public void setDoctorUid(String doctorUid) {
        this.doctorUid = doctorUid;
    }

    public String getNameOfDoctor() {
        return nameOfDoctor;
    }

    public void setNameOfDoctor(String nameOfDoctor) {
        this.nameOfDoctor = nameOfDoctor;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String answer) {
        Answer = answer;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public AnsweredModelClass(String doctorUid, String nameOfDoctor, String answer, String pushKey) {
        this.doctorUid = doctorUid;
        this.nameOfDoctor = nameOfDoctor;
        Answer = answer;
        this.pushKey = pushKey;
    }
}
