package com.example.healthcare.Model;

public class CommentModelClass {

    String UserUid;
    String nameOfUser;

    public CommentModelClass() {
    }

    String Comment;
    String pushKey;


    public CommentModelClass(String userUid, String nameOfUser, String comment, String pushKey) {
        UserUid = userUid;
        this.nameOfUser = nameOfUser;
        Comment = comment;
        this.pushKey = pushKey;
    }

    public String getUserUid() {
        return UserUid;
    }

    public void setUserUid(String userUid) {
        UserUid = userUid;
    }

    public String getNameOfUser() {
        return nameOfUser;
    }

    public void setNameOfUser(String nameOfUser) {
        this.nameOfUser = nameOfUser;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }
}
