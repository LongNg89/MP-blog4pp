package com.longng.blog4pp.models;

import java.util.Date;

public class CommentModel {

    private String Message, user_id;
    private Date timeStamp;

    public CommentModel() {

    }

    public CommentModel(String message, String user_id, Date timeStamp) {
        this.Message = message;
        this.user_id = user_id;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}