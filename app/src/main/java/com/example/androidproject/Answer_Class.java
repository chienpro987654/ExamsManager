package com.example.androidproject;

public class Answer_Class {
    String QuesNumber;
    String Answer;
    boolean IsAnswered;

    public Answer_Class(String QuesNumber, String Answer){
        this.QuesNumber = QuesNumber;
        this.Answer = Answer;
        IsAnswered = true;
    }

    String getQuesNumber(){ return QuesNumber; }
    String getAnswer(){ return Answer; }
    boolean IsAnswered(){ return IsAnswered; }
}
