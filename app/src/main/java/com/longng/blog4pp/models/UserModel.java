package com.longng.blog4pp.models;

public class UserModel {
     private String uid,email,password,avartar,username;

    public UserModel(String uid, String email, String password) {
        this.uid = uid;
        this.email = email;
        this.password = password;
    }

    public UserModel(String uid, String email, String password, String avartar, String username) {
        this.uid = uid;
        this.email = email;
        this.password = password;
        this.avartar = avartar;
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

    public String getAvartar() {
        return avartar;
    }

    public void setAvartar(String avartar) {
        this.avartar = avartar;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
