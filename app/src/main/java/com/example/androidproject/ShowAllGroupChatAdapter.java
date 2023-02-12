package com.example.androidproject;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
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

public class ShowAllGroupChatAdapter extends ArrayAdapter<ChatGroupOverviewClass> {
    Context context;
    public ShowAllGroupChatAdapter(@NonNull Context context, int resource, @NonNull List<ChatGroupOverviewClass> objects) {
        super(context, resource, objects);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_group_chat, null, false);
        }

        ChatGroupOverviewClass chatGroup = getItem(position);

        ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_all_group_chat_group_img);
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_all_group_chat_group_name);
        TextView tv_lastMsg = (TextView) convertView.findViewById(R.id.tv_all_group_chat_last_msg);
        TextView tv_lastTime = (TextView) convertView.findViewById(R.id.tv_all_group_chat_last_time);

        tv_name.setText(chatGroup.getGroupName());
        tv_lastMsg.setText(chatGroup.getGroupLastMess());
        Long lastTime = chatGroup.getGroupLastTime();
        if(lastTime==0)
        {
            tv_lastTime.setVisibility(View.GONE);
            tv_lastMsg.setVisibility(View.GONE);
            tv_name.setTypeface(Typeface.DEFAULT);
        }
        else
        {
            String time = DateFormat.format("dd/MM/yyyy-HH:mm", lastTime).toString();
            tv_lastTime.setText(time.substring(time.indexOf("-")+1));
            tv_lastTime.setVisibility(View.VISIBLE);
            tv_lastMsg.setVisibility(View.VISIBLE);
        }
        if(!chatGroup.getRead())
        {
            if(lastTime!=0)
            {
                tv_name.setTypeface(Typeface.DEFAULT_BOLD);
                tv_lastMsg.setTypeface(Typeface.DEFAULT_BOLD);
                tv_lastTime.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }
        else
        {
            tv_name.setTypeface(Typeface.DEFAULT);
            tv_lastMsg.setTypeface(Typeface.DEFAULT);
            tv_lastTime.setTypeface(Typeface.DEFAULT);
        }
        String img = chatGroup.getGroupImg();
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
