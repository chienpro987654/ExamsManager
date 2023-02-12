package com.example.androidproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class Fragment_Group_GridViewAdapter extends ArrayAdapter<Group> {
    private Activity context;

    public Fragment_Group_GridViewAdapter(@NonNull Activity context, int resource, @NonNull List<Group> objects) {
        super(context, resource, objects);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.group_item, null, false);
        }

        Group group = getItem(position);

        ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_group_item_img);
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name_group_item);
        LinearLayout ll_parent = (LinearLayout) convertView.findViewById(R.id.ll_group_item_parent);

        tv_name.setText(group.getGroupName());
        String img = group.getGroupImg();
        switch (img)
        {
            case "0":
                iv_img.setImageResource(R.drawable.ic_group_img_0_white);
                break;
            case "1":
                iv_img.setImageResource(R.drawable.ic_group_img_1_red);
                break;
            case "2":
                iv_img.setImageResource(R.drawable.ic_group_img_2_orange);
                break;
            case "3":
                iv_img.setImageResource(R.drawable.ic_group_img_3_yellow);
                break;
            case "4":
                iv_img.setImageResource(R.drawable.ic_group_img_4_green);
                break;
            case "5":
                iv_img.setImageResource(R.drawable.ic_group_img_5_blue);
                break;
            case "6":
                iv_img.setImageResource(R.drawable.ic_group_img_6_indigo);
                break;
            case "7":
                iv_img.setImageResource(R.drawable.ic_group_img_7_violet);
                break;
            case "8":
                iv_img.setImageResource(R.drawable.ic_group_img_8_black);
                break;
            default:
                iv_img.setImageResource(R.drawable.ic_group_img_0_white);
                break;
        }
        return convertView;
    }

    
}
