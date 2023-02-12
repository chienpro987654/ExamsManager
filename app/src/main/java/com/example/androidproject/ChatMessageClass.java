package com.example.androidproject;

import java.util.Calendar;

public class ChatMessageClass {
    private String Msg;
    private Long date;
    private String userID;

    public ChatMessageClass(){
    }

    public ChatMessageClass(String Msg, String userID){
        this.Msg = Msg;
        this.date = Calendar.getInstance().getTime().getTime();
        this.userID = userID;
    }

    public void setMsg(String Msg){ this.Msg = Msg; }
    public String getMsg(){ return Msg; }

    public void setDate(Long date){ this.date = date; }
    public Long getDate(){ return date; }

    public void setUsrName(String userID){ this.userID = userID; }
    public String getUserID(){ return userID; }
}
