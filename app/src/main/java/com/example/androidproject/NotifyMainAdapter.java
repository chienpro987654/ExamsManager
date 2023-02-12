package com.example.androidproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class NotifyMainAdapter extends BaseExpandableListAdapter {
    ArrayList<String> listGroup = new ArrayList<>();
    HashMap<String, Notify_Class> listChild = new HashMap<>();

    public NotifyMainAdapter(ArrayList<String> listGroup, HashMap<String, Notify_Class> listChild) {
        this.listGroup = listGroup;
        this.listChild = listChild;
    }

    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChild.get(listGroup.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_group_notify,parent,false);

        TextView tv_title = convertView.findViewById(R.id.tv_title_notify);
        TextView tv_grade = convertView.findViewById(R.id.tv_grade_notify);
        TextView tv_date = convertView.findViewById(R.id.tv_date_notify);
        ImageView img_red_dot = convertView.findViewById(R.id.img_red_dot);

        String title = Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).getTitle().toString();
        String grade = Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).getGrade().toString();
        String date = Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).getDate().toString();

        tv_title.setText(title);
        tv_grade.setText(grade);
        tv_date.setText(date);

        String read = Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).getRead().toString();

        if (read.equals("true"))
        {
            img_red_dot.setVisibility(View.GONE);
        }
        else
        {
            img_red_dot.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_child_notify,parent,false);

        TextView textView = convertView.findViewById(R.id.tv_child_content_notify);

        String title = Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).getTitle().toString();
        String content = Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).getContent().toString();
        String grade = Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).getGrade().toString();
        String date_happen = Objects.requireNonNull(listChild.get(listGroup.get(groupPosition))).getDate_happen().toString();

        String msg = "Thông báo: "+title+"\nNội dung: "+content+"\nLớp: "+grade+"\nNgày diễn ra: "+date_happen;

        textView.setText(msg);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
