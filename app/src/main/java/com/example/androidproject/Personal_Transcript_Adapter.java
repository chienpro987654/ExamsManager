package com.example.androidproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class Personal_Transcript_Adapter extends ArrayAdapter {
    Context context;

    public Personal_Transcript_Adapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.personal_transcript_item, null, false);
        }

        // Get item
        ScoreClass score = (ScoreClass) getItem(position);
        // Get view
        TextView tvName = (TextView) convertView.findViewById(R.id.tv_per_trans_assign_name);
        TextView tvEmail = (TextView) convertView.findViewById(R.id.tv_per_trans_assign_score);

        tvName.setText(score.getDescription());
        tvEmail.setText(score.getPoint());

        return convertView;
    }
}
