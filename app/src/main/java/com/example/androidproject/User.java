package com.example.androidproject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class User {
    String name;
    String email;
    String password;
    String birth;
    String grade;
    String school;
    String login_session;
    String joined_group;
    String avatar;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");

    public User() {
        name ="x";
        email="x";
        password="x";
        birth="1/1/2000";
        grade="Unknown";
        school="Unknown";
        joined_group="";
        avatar="default";
        login_session= simpleDateFormat.format(new Date());
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        birth="1/1/2000";
        grade="Unknown";
        school="Unknown";
        joined_group="Unknown";
        avatar="default";
        login_session= simpleDateFormat.format(new Date());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getLogin_session() {
        return login_session;
    }

    public void setLogin_session(String login_session) {
        this.login_session = login_session;
    }

    public String getJoined_group() {
        return joined_group;
    }

    public void setJoined_group(String joined_group) {
        this.joined_group = joined_group;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
