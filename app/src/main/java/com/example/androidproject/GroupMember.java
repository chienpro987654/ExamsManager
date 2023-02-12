package com.example.androidproject;

public class GroupMember {
    String name;
    String email;
    boolean isAd;

    public GroupMember(String name, String email, boolean isAd) {
        this.name = name;
        this.email = email;
        this.isAd = isAd;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAd() {
        return isAd;
    }
}
