package com.example.androidproject;

import java.util.Comparator;

public class ComparatorForGroupName implements Comparator<Group> {
    @Override
    public int compare(Group o1, Group o2) {
        return o1.getGroupName().compareTo(o2.getGroupName());
    }
}
