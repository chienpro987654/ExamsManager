package com.example.androidproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.xmlbeans.impl.xb.substwsdl.TImport;

import java.util.List;

public class Member_View_Adapter extends ArrayAdapter {
    Context context;
    public Member_View_Adapter(@NonNull Context context, int resource, @NonNull List<GroupMember> objects) {
        super(context, resource, objects);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.maingroup_member_item, null, false);
        }

        // Get item
        GroupMember member = (GroupMember) getItem(position);
        // Get view
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_member_item_name);
        TextView tvEmail = (TextView) convertView.findViewById(R.id.tv_member_item_email);
        ImageView ivRole = (ImageView) convertView.findViewById(R.id.iv_member_item_role);
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.ll_member_item_parent);

        tvName.setText(member.getName());
        tvEmail.setText(member.getEmail());

        if(member.isAd())
            ivRole.setImageResource(R.drawable.leadership);
        else
            ivRole.setImageResource(R.drawable.user);

        if(position%2!=0)
            linearLayout.setBackgroundResource(R.color.blue_10percent);
        else
            linearLayout.setBackgroundResource(R.color.white);
        return convertView;
    }

}
