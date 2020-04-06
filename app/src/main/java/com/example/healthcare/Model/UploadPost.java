package com.example.healthcare.Model;

public class UploadPost {
    String Title, Description,Category ,ImageURI,pushKey;

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public UploadPost() {
    }

    public UploadPost(String title, String description, String category, String imageURI, String pushKey) {
        Title = title;
        Description = description;
        Category = category;
        ImageURI = imageURI;
        this.pushKey = pushKey;
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

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getImageURI() {
        return ImageURI;
    }

    public void setImageURI(String imageURI) {
        ImageURI = imageURI;
    }
}