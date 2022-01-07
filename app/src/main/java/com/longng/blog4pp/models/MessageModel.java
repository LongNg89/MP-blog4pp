package com.longng.blog4pp.models;

import java.util.Date;

public class MessageModel {
    private String messageId;
    private String message;
    private Date timeForMessage;
    private String senderID;
    private String receiverId;

    public MessageModel(String messageId, String message, Date timeForMessage, String senderID, String receiverId) {
        this.messageId = messageId;
        this.message = message;
        this.timeForMessage = timeForMessage;
        this.senderID = senderID;
        this.receiverId = receiverId;
    }

    public MessageModel() {
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimeForMessage() {
        return timeForMessage;
    }

    public void setTimeForMessage(Date timeForMessage) {
        this.timeForMessage = timeForMessage;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
}
