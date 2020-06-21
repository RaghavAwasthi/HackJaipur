package com.hackathon.myapplication;

import java.io.Serializable;

public class ContributionFoodModel implements Serializable {
    String foodname;
    String foodimage_url;
    String location;
    long date;
    String id;
    String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    long number;

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public long getDate() {
        return date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDate(long date) {
        this.date = date;
    }

    int type;

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    public String getFoodimage_url() {
        return foodimage_url;
    }

    public void setFoodimage_url(String foodimage_url) {
        this.foodimage_url = foodimage_url;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
