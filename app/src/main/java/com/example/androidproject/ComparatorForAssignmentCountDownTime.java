package com.example.androidproject;

import java.util.Comparator;

public class ComparatorForAssignmentCountDownTime implements Comparator<AssignmentViewClass> {
    @Override
    public int compare(AssignmentViewClass assign1, AssignmentViewClass assign2) {
        return  assign1.getAsCountDownBySecond().compareTo(assign2.getAsCountDownBySecond());
    }
}
