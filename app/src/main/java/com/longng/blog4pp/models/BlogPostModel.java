package com.longng.blog4pp.models;

import java.util.Date;

public class BlogPostModel {

    public String image_url, image_thumb, description, userId;
    public Date timeStamp;

    public BlogPostModel() {

    }

    public BlogPostModel(String image_url, String image_thumb, String desc, String user_id, Date timeStamp) {
        this.image_url = image_url;
        this.image_thumb = image_thumb;
        this.description = desc;
        this.userId = user_id;
        this.timeStamp = timeStamp;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public String getDesc() {
        return description;
    }

    public void setDesc(String desc) {
        this.description = desc;
    }

    public String getUser_id() {
        return userId;
    }

    public void setUser_id(String user_id) {
        this.userId = user_id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

}