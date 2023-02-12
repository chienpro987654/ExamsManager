package com.example.androidproject;

public class QuestionSet_Class {
    String setID;
    String name;
    String userID;
    Question_Class[] questions;
    String set_creator;
    String date_create;
    String grade;
    String quantity; //number of questions
    String note;

    public QuestionSet_Class(String setID, String name, String userID, String set_creator, String grade) {
        this.setID = setID;
        this.name = name;
        this.userID = userID;
        this.set_creator = set_creator;
        this.grade = grade;
        this.note="Không";
    }

    public QuestionSet_Class(String setID, String name, String userID,
                             String set_creator, String date_create, String grade) {
        this.setID = setID;
        this.name = name;
        this.userID = userID;
        this.set_creator = set_creator;
        this.date_create = date_create;
        this.grade = grade;
        this.note="Không";
    }

    public String getSetID() {
        return setID;
    }

    public void setSetID(String setID) {
        this.setID = setID;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Question_Class[] getQuestions() {
        return questions;
    }

    public void setQuestions(Question_Class[] questions) {
        this.questions = questions;
    }

    public String getSet_creator() {
        return set_creator;
    }

    public void setSet_creator(String set_creator) {
        this.set_creator = set_creator;
    }

    public String getDate_create() {
        return date_create;
    }

    public void setDate_create(String date_create) {
        this.date_create = date_create;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
