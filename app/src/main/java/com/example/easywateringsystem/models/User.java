package com.example.easywateringsystem.models;

import com.example.easywateringsystem.utils.CallbackDB;

import java.lang.System;

public class User {

    private int userId;
    private String nameFirst;
    private String nameLast;
    private String email;
    private String password;
    private String image;

    public CallbackDB callback;

    public User(String nameFirst, String nameLast, String email, String password, String image) {
        this.userId = userId;
        this.nameFirst = nameFirst;
        this.nameLast = nameLast;
        this.email = email;
        this.password = password;
        this.image = image;
    }
    public User(String image) {
        this.image = image;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNameFirst() {
        return nameFirst;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public String getNameLast() {
        return nameLast;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;

    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public static void test() {
        System.out.print("test");
    }

    public void onCallback() {
        callback.callbackDB();
    }



}
