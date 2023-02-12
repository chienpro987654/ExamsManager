package com.example.androidproject;

import java.util.Comparator;

public class ComparatorForChatGroupLastTime implements Comparator<ChatGroupOverviewClass> {
    @Override
    public int compare(ChatGroupOverviewClass o1, ChatGroupOverviewClass o2) {
        return o2.getGroupLastTime().compareTo(o1.getGroupLastTime());
    }
}
