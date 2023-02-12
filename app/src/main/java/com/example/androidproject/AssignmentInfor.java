package com.example.androidproject;

public class AssignmentInfor {
    private String name;
    private String group;
    private String open;
    private String close;
    private String countdown;
    private String status;

    public AssignmentInfor(String name, String group, String open, String close, String countdown, String status) {
        this.name = name;
        this.group = group;
        this.open = open;
        this.close = close;
        this.countdown = countdown;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getOpen() {
        return open;
    }

    public String getClose() {
        return close;
    }

    public String getCountdown() {
        return countdown;
    }

    public String getStatus() {
        return status;
    }
}
