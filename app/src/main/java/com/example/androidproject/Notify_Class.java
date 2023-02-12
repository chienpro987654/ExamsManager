package com.example.androidproject;

public class Notify_Class {
    private String id;
    private String grade;
    private String date;
    private String date_happen;
    private String title;
    private String content;
    private String read;

    public Notify_Class() {
        id="x";
        grade="x";
        date="1/1/2000";
        date_happen="2/1/2000";
        title="x";
        content="x";
    }

    public Notify_Class(String id, String grade, String date, String date_happen, String title, String content, String read) {
        this.id = id;
        this.grade = grade;
        this.date = date;
        this.date_happen = date_happen;
        this.title = title;
        this.content = content;
        this.read = read;
    }

    public Notify_Class(Notify_Class notify_class) {
        this.id = notify_class.getId();
        this.grade = notify_class.getGrade();
        this.date = notify_class.getDate();
        this.date_happen = notify_class.getDate_happen();
        this.title = notify_class.getTitle();
        this.content = notify_class.getContent();
        this.read = notify_class.getRead();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate_happen() {
        return date_happen;
    }

    public void setDate_happen(String date_happen) {
        this.date_happen = date_happen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }
}
