package com.example.androidproject;

public class Group {
    String groupID;
    String groupName;
    String groupImg;
    String groupAdmin;
    String groupMem;
    String allAsID;
    String groupHash;

    public Group(String groupID, String groupName, String groupImg) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupImg = groupImg;
    }

    public Group(String groupID, String groupName, String groupImg, String groupAdmin, String groupMem, String groupHash) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupImg = groupImg;
        this.groupAdmin = groupAdmin;
        this.groupMem = groupMem;
        this.groupHash = groupHash;
        this.allAsID="Unknown";
    }

    public String getAllAsID() {
        return allAsID;
    }

    public String getGroupHash() {
        return groupHash;
    }

    public Group(String groupID, String groupName, String groupImg, String groupHash) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupImg = groupImg;
        this.groupHash = groupHash;
        this.allAsID="Unknown";
    }

    public Group(String groupID, String groupName, String groupImg, String groupAdmin, String groupMem) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupImg = groupImg;
        this.groupAdmin = groupAdmin;
        this.groupMem = groupMem;
        this.allAsID="Unknown";
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupAdmin() {
        return groupAdmin;
    }

    public String getGroupMem() {
        return groupMem;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupImg() {
        return groupImg;
    }
}


