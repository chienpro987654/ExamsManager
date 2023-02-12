package com.example.androidproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class Answer_Adapter extends ArrayAdapter<Answer_Class> {
    private Activity context;

    public Answer_Adapter(Activity context, int layoutID, List<Answer_Class> objects) {
        super(context, layoutID, objects);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView =
                    LayoutInflater.from(context).inflate(R.layout.assignment_window_item, null,
                            false);
        }

        Answer_Class Answer = getItem(position);

        TextView tvAssignment = (TextView) convertView.findViewById(
                R.id.TextView_Assignment_Window);
        TextView tvDone = (TextView) convertView.findViewById(
                R.id.TextView_Assignment_Window_Item_Done);

        if (Answer.IsAnswered()){
            tvAssignment.setBackgroundResource(R.color.teal_200);
            tvDone.setVisibility(View.VISIBLE);
        }
        else{
            tvAssignment.setBackgroundResource(R.color.gray);
            tvDone.setVisibility(View.GONE);
        }

        return convertView;
    }
}
