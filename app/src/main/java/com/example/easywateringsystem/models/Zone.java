package com.example.easywateringsystem.models;

public class Zone {

    private String mNumber;
    private String mName;
    private String mImage;

    public Zone(String number, String name, String image) {
        this.mNumber = number;
        this.mName = name;
        this.mImage = image;
    }

    public Zone(String image) {
        this.mImage = image;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        this.mNumber = number;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        this.mImage = image;
    }

}
