package com.example.healthcare.Model;

import android.util.Log;

public class NewsFeedModel {
   String Userid;
    String Title;
    String Description;
    String pushKey;
    String ImageUri;
    String Category;
    String Name;




    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public NewsFeedModel() {
    }

    public String getUserid() {
        return Userid;
    }

    public void setUserid(String userid) {
        Userid = userid;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public NewsFeedModel(String userid, String title, String description, String pushKey, String imageUri, String category, String Name) {
        Userid = userid;
        Title = title;
        Description = description;
        this.pushKey = pushKey;
        ImageUri = imageUri;
        Category = category;
        this.Name=Name;
    }
}
