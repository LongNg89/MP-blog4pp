package com.longng.blog4pp.models;

public class UserModel {
    private String uid,email,password,avatar,username;

    public UserModel(String uid, String email, String password, String avatar, String username) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.username = username;
    }

    public UserModel() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avartar) {
        this.avatar = avartar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
