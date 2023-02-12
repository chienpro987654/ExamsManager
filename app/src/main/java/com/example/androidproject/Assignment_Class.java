package com.example.androidproject;

public class Assignment_Class {
    String asName;
    String admin;
    String groupID;
    String asID;
    String setID;
    String transID;
    String date_start;
    String date_end;
    String status;
    String maxAttempt;
    String doingTime;
    String setHash;

    public Assignment_Class(String asName, String admin, String groupID, String asID, String setID, String transID, String date_start, String date_end, String status, String maxAttempt, String doingTime, String setHash) {
        this.asName = asName;
        this.admin = admin;
        this.groupID = groupID;
        this.asID = asID;
        this.setID = setID;
        this.transID = transID;
        this.date_start = date_start;
        this.date_end = date_end;
        this.status = status;
        this.maxAttempt = maxAttempt;
        this.doingTime = doingTime;
        this.setHash = setHash;
    }

    public String getAsName() { return asName; }

    public void setAsName(String asName) { this.asName = asName; }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getAsID() {
        return asID;
    }

    public void setAsID(String asID) {
        this.asID = asID;
    }

    public String getSetID() { return setID; }

    public void setSetID(String setID) { this.setID = setID; }

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMaxAttempt() {
        return maxAttempt;
    }

    public void setMaxAttempt(String maxAttempt) {
        this.maxAttempt = maxAttempt;
    }

    public String getDoingTime() {
        return doingTime;
    }

    public void setDoingTime(String doingTime) {
        this.doingTime = doingTime;
    }

    public String getSetHash() {
        return setHash;
    }

    public void setSetHash(String setHash) {
        this.setHash = setHash;
    }
}
