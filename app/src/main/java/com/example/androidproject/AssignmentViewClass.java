package com.example.androidproject;

public class AssignmentViewClass {
    String asID;
    String asName;
    String asAdmin;
    String asGroup;
    String asBegin;
    String asEnd;
    String asCountdown;
    String asStatus;
    String asMaxAttempt;
    String asMaxTime;
    String setID;
    Long asCountDownBySecond;

    public Long getAsCountDownBySecond() {
        return asCountDownBySecond;
    }

    public AssignmentViewClass(String asID, String asName, String asAdmin, String asGroup, String asBegin, String asEnd, String asCountdown, String asStatus, String asMaxAttempt, String asMaxTime, String setID, Long asCountDownBySecond) {
        this.asID = asID;
        this.asName = asName;
        this.asAdmin = asAdmin;
        this.asGroup = asGroup;
        this.asBegin = asBegin;
        this.asEnd = asEnd;
        this.asCountdown = asCountdown;
        this.asStatus = asStatus;
        this.asMaxAttempt = asMaxAttempt;
        this.asMaxTime = asMaxTime;
        this.setID = setID;
        this.asCountDownBySecond = asCountDownBySecond;
    }

    public AssignmentViewClass(String asID, String asName, String asAdmin, String asGroup, String asBegin, String asEnd, String asCountdown, String asStatus, String asMaxAttempt, String asMaxTime, String setID) {
        this.asID = asID;
        this.asName = asName;
        this.asAdmin = asAdmin;
        this.asGroup = asGroup;
        this.asBegin = asBegin;
        this.asEnd = asEnd;
        this.asCountdown = asCountdown;
        this.asStatus = asStatus;
        this.asMaxAttempt = asMaxAttempt;
        this.asMaxTime = asMaxTime;
        this.setID = setID;
    }

    public String getSetID() {
        return setID;
    }

    public String getAsGroup() {
        return asGroup;
    }

    public AssignmentViewClass(String asID, String asName, String asAdmin, String asGroup, String asBegin, String asEnd, String asCountdown, String asStatus, String asMaxAttempt, String asMaxTime) {
        this.asID = asID;
        this.asName = asName;
        this.asAdmin = asAdmin;
        this.asGroup = asGroup;
        this.asBegin = asBegin;
        this.asEnd = asEnd;
        this.asCountdown = asCountdown;
        this.asStatus = asStatus;
        this.asMaxAttempt = asMaxAttempt;
        this.asMaxTime = asMaxTime;
    }

    public String getAsMaxAttempt() {
        return asMaxAttempt;
    }

    public String getAsMaxTime() {
        return asMaxTime;
    }

    public AssignmentViewClass(String asID, String asName, String asAdmin, String asGroup, String asBegin, String asEnd, String asCountdown, String asStatus) {
        this.asID = asID;
        this.asName = asName;
        this.asAdmin = asAdmin;
        this.asGroup = asGroup;
        this.asBegin = asBegin;
        this.asEnd = asEnd;
        this.asCountdown = asCountdown;
        this.asStatus = asStatus;
    }

    public AssignmentViewClass(String asID, String asName, String asAdmin, String asBegin, String asEnd, String asCountdown, String asStatus) {
        this.asID = asID;
        this.asName = asName;
        this.asAdmin = asAdmin;
        this.asBegin = asBegin;
        this.asEnd = asEnd;
        this.asCountdown = asCountdown;
        this.asStatus = asStatus;
    }

    public String getAsID() {
        return asID;
    }

    public String getAsName() {
        return asName;
    }

    public String getAsAdmin() {
        return asAdmin;
    }

    public String getAsBegin() {
        return asBegin;
    }

    public String getAsEnd() {
        return asEnd;
    }

    public String getAsCountdown() {
        return asCountdown;
    }

    public String getAsStatus() {
        return asStatus;
    }
}
