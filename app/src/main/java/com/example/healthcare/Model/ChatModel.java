package com.example.healthcare.Model;

public class ChatModel {
    String sender;
    String receiver;
    String message;

    public String getSendedImageUri() {
        return sendedImageUri;
    }

    public void setSendedImageUri(String sendedImageUri) {
        this.sendedImageUri = sendedImageUri;
    }

    String sendedImageUri;
    boolean isseen;

    public ChatModel() {
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ChatModel(String receiver, String sender, String message,boolean isseen,String SendedImageUri) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.isseen = isseen;
        this.sendedImageUri=SendedImageUri;
    }
}
