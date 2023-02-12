package com.example.androidproject;

public class Question_Class {
    String quesID;
    String question;
    String answerA;
    String answerB;
    String answerC;
    String answerD;
    String right_answer;

    public Question_Class(){
        quesID = "x";
        question = "no";
        answerA = "x";
        answerB = "x";
        answerC = "x";
        answerD = "x";
        right_answer = "x";
    }

    public Question_Class(String quesID, String question, String answerA, String answerB,
                          String answerC, String answerD, String right_answer) {
        this.quesID = quesID;
        this.question = question;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.right_answer = right_answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public String getRight_answer() {
        return right_answer;
    }

    public void setRight_answer(String right_answer) {
        this.right_answer = right_answer;
    }

    public String getQuesID() {
        return quesID;
    }

    public void setQuesID(String quesID) {
        this.quesID = quesID;
    }
}
