package com.example.androidproject;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Assignment_View_Adapter extends ArrayAdapter {
    Context context;
    public Assignment_View_Adapter(@NonNull Context context, int resource, @NonNull List<AssignmentViewClass> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_assignment, null, false);
        }

        // Get item
        AssignmentViewClass assignmentInfor = (AssignmentViewClass) getItem(position);
        // Get view
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_assign_name);
        TextView tvGroup = (TextView) convertView.findViewById(R.id.tv_assign_group);
        TextView tvAdmin = (TextView) convertView.findViewById(R.id.tv_assign_admin);
        TextView tvOpen = (TextView) convertView.findViewById(R.id.tv_assign_open);
        TextView tvClose = (TextView) convertView.findViewById(R.id.tv_assign_close);
        TextView tvCountdown = (TextView) convertView.findViewById(R.id.tv_assign_countdown);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.tv_assign_status);

        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
        DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference("Group");

        userReference.child(assignmentInfor.getAsAdmin()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    tvAdmin.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        groupReference.child(assignmentInfor.getAsGroup()).child("groupName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                    tvGroup.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tvName.setText(assignmentInfor.getAsName());
        tvOpen.setText(assignmentInfor.getAsBegin());
        tvClose.setText(assignmentInfor.getAsEnd());
        tvCountdown.setText(assignmentInfor.getAsCountdown());
        tvStatus.setText(assignmentInfor.getAsStatus());
        if(assignmentInfor.getAsCountdown().matches("0"))
        {
            tvCountdown.setTextColor(Color.parseColor("#FF0000"));
            tvStatus.setTextColor(Color.parseColor("#FF0000"));
        }
        else
        {
            tvCountdown.setTextColor(Color.parseColor("#000000"));
            tvStatus.setTextColor(Color.parseColor("#000000"));
        }
        return convertView;
    }
}
