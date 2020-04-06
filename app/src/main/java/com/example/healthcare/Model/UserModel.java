package com.example.healthcare.Model;

public class UserModel {
    String Name,Email,Uid,ImageUri;

    public UserModel() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public UserModel(String name, String email, String uid, String imageUri) {
        Name = name;
        Email = email;
        Uid = uid;
        ImageUri = imageUri;
    }
}
