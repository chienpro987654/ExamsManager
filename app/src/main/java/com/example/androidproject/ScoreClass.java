package com.example.androidproject;

public class ScoreClass {
    String description;
    String point;

    public ScoreClass(String description, String point) {
        this.description = description;
        this.point = point;
    }

    public ScoreClass() {
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getDescription() {
        return description;
    }

    public String getPoint() {
        return point;
    }
}
