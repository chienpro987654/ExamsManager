package com.example.androidproject;

public class ChatGroupOverviewClass {
    String groupID;
    String groupName;
    String groupImg;
    String groupLastMess;
    Long groupLastTime;
    Boolean isRead;

    public ChatGroupOverviewClass(String groupID, String groupName, String groupImg, String groupLastMess, Long groupLastTime, Boolean isRead) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupImg = groupImg;
        this.groupLastMess = groupLastMess;
        this.groupLastTime = groupLastTime;
        this.isRead = isRead;
    }

    public ChatGroupOverviewClass(String groupID, String groupName, String groupImg, String groupLastMess, Long groupLastTime) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.groupImg = groupImg;
        this.groupLastMess = groupLastMess;
        this.groupLastTime = groupLastTime;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupImg() {
        return groupImg;
    }

    public String getGroupLastMess() {
        return groupLastMess;
    }

    public Long getGroupLastTime() {
        return groupLastTime;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupImg(String groupImg) {
        this.groupImg = groupImg;
    }

    public void setGroupLastMess(String groupLastMess) {
        this.groupLastMess = groupLastMess;
    }

    public void setGroupLastTime(Long groupLastTime) {
        this.groupLastTime = groupLastTime;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
