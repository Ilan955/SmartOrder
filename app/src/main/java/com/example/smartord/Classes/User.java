package com.example.smartord.Classes;

import java.util.HashMap;

public class User {
    private static User single_instance = null;
    public String fullName;
    public String emailAddress;
    public String phoneNumber;
    public HashMap<String,String> reservations;

    public static User getInstance()
    {
        if (single_instance == null)
            single_instance = new User(null,null,null,new HashMap<>());

        return single_instance;
    }

    public User(String fullName, String emailAddress, String phoneNumber,HashMap<String,String> hash) {
        this.fullName = fullName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.reservations = hash;
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public HashMap<String, String> getReservations() { return reservations; }

    public void setReservations(HashMap<String, String> reservations) { this.reservations = reservations; }
}


