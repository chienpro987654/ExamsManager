package com.example.androidproject;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Chat_View_Adapter extends ArrayAdapter<ChatMessageClass> {
    private Activity context;

    public Chat_View_Adapter(Activity context, int layoutID, List<ChatMessageClass> objects) {
        super(context, layoutID, objects);
        this.context = context;
    }

    DatabaseReference ref_user = FirebaseDatabase.getInstance().getReference("User");
    String userID = getContext().getSharedPreferences("UserID",MODE_PRIVATE).getString("userID","Unknown");
    String lastID = "";

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView =
                    LayoutInflater.from(context).inflate(R.layout.message, null,
                            false);
        }

        ChatMessageClass chatMessage = getItem(position);

        ConstraintLayout left_ll = convertView.findViewById(R.id.left_layout);
        ConstraintLayout right_ll = convertView.findViewById(R.id.right_layout);

        if (chatMessage.getUserID().compareTo(userID) == 0) {
            TextView msg_curruser = convertView.findViewById(R.id.tv_chat_message_current_user);
            TextView date_curruser = convertView.findViewById(R.id.tv_date_current_user);

            if (chatMessage.getUserID().compareTo(lastID) != 0) {
                lastID = chatMessage.getUserID();
            }

            msg_curruser.setText(chatMessage.getMsg());

            date_curruser.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", chatMessage.getDate()));

            left_ll.setVisibility(View.GONE);
            right_ll.setVisibility(View.VISIBLE);
        } else {
            TextView msg = convertView.findViewById(R.id.tv_chat_message);
            TextView user = convertView.findViewById(R.id.tv_user_name);
            TextView date = convertView.findViewById(R.id.tv_date);

            if (chatMessage.getUserID().compareTo(lastID) != 0) {
                lastID = chatMessage.getUserID();
                ref_user.child(chatMessage.getUserID()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        user.setText(snapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                user.setVisibility(View.VISIBLE);
            } else {
                user.setVisibility(View.GONE);
            }

            msg.setText(chatMessage.getMsg());

            date.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", chatMessage.getDate()));

            right_ll.setVisibility(View.GONE);
            left_ll.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
