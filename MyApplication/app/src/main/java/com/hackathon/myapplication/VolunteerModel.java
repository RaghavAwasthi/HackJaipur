package com.hackathon.myapplication;

import java.io.Serializable;

public class VolunteerModel implements Serializable {
    String username;
    String address;
    String city;
    String state;
    String email;
    long mobileNumber;
    int age;
    int AccountStatus;
    int contributionCount;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getContributionCount() {
        return contributionCount;
    }

    public void setContributionCount(int contributionCount) {
        this.contributionCount = contributionCount;
    }

    public int getAccountStatus() {
        return AccountStatus;
    }

    public void setAccountStatus(int accountStatus) {
        AccountStatus = accountStatus;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(long mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
