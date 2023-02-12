package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.CountDownLatch;

public class ChatRoomClass extends AppCompatActivity {
    String groupID, groupName, userID, GroupMemID, GroupMemName, lsSeenerName, lsSeenerLastMsg;
    String[] MemArray;
    boolean initialized;
    TextView tvSeener, tvDate, tvMsg;
    Hashtable<String, String> htID_Name;
    int lastSelected = -1;

    DatabaseReference ref_chat = FirebaseDatabase.getInstance().getReference("Chat");
    DatabaseReference ref_group = FirebaseDatabase.getInstance().getReference("Group");
    DatabaseReference ref_user = FirebaseDatabase.getInstance().getReference("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_class);
        getSupportActionBar().hide();
        initialized = false;
        htID_Name = new Hashtable<>();

        TextView tvRoomName = findViewById(R.id.tv_room_name);
        ListView lvChat = findViewById(R.id.lv_chat_room);
        tvSeener = findViewById(R.id.tv_seener);
        EditText etChat = findViewById(R.id.et_chat);
        Button btSend = findViewById(R.id.bt_send_message);

        SharedPreferences preferences = getSharedPreferences("UserID",MODE_PRIVATE);
        userID = preferences.getString("userID","Unknown");

        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupID");
        groupName = intent.getStringExtra("groupName");

        tvRoomName.setText(groupName);
        lvChat.setDivider(null);

        ArrayList<ChatMessageClass> chatMessages = new ArrayList<>();

        Chat_View_Adapter adapter = new Chat_View_Adapter(this, android.R.layout.simple_list_item_1,
                chatMessages);
        lvChat.setAdapter(adapter);

        etChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().compareTo("") != 0) {
                    if (!btSend.isEnabled())
                        btSend.setEnabled(true);
                } else
                    btSend.setEnabled(false);
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatMessageClass Msg = new ChatMessageClass(etChat.getText().toString(), userID);
                ref_chat.child(groupID).child("msg").push().setValue(Msg);
                ref_chat.child(groupID).child("lsSeenerLastMsg").setValue(userID);
                etChat.setText("");
            }
        });

        try {
            ref_group.child(groupID).child("groupMem").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    GroupMemID = snapshot.getValue().toString();
                    String[] ArrayID = GroupMemID.split(",");
                    CountDownLatch done = new CountDownLatch(ArrayID.length);
                    for (String id : ArrayID) {
                        ref_user.child(id).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                htID_Name.put(id, snapshot.getValue().toString());
                                done.countDown();
                                if (done.getCount() == 0) {
                                    try {
                                        ref_chat.child(groupID).child("msg").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    for (DataSnapshot ss : snapshot.getChildren()) {
                                                        ChatMessageClass chatMessage = ss.getValue(ChatMessageClass.class);
                                                        chatMessages.add(chatMessage);
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                    lvChat.smoothScrollToPosition(chatMessages.size());
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });

                                        ref_chat.child(groupID).child("lsSeenerLastMsg").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    lsSeenerLastMsg = snapshot.getValue().toString();
                                                    String[] Arr_seenerID = lsSeenerLastMsg.split(",");
                                                    boolean CurrUserSeen = false;
                                                    for (String seenerID : Arr_seenerID) {
                                                        if (seenerID.compareTo(userID) == 0) {
                                                            CurrUserSeen = true;
                                                            break;
                                                        }
                                                    }
                                                    if (!CurrUserSeen) {
                                                        lsSeenerLastMsg += "," + userID;

                                                        ref_chat.child(groupID).child("lsSeenerLastMsg").setValue(lsSeenerLastMsg);
                                                    }
                                                }
                                                addSeenerListener();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    } catch (Exception e) { }


                                    try {
                                        ref_chat.child(groupID).child("msg").limitToLast(1).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (!initialized) {
                                                    initialized = true;
                                                } else {
                                                    for (DataSnapshot ss : snapshot.getChildren()) {
                                                        ChatMessageClass chatMessage = ss.getValue(ChatMessageClass.class);
                                                        chatMessages.add(chatMessage);
                                                    }
                                                    adapter.notifyDataSetChanged();
                                                    lvChat.smoothScrollToPosition(chatMessages.size());

                                                    ref_chat.child(groupID).child("lsSeenerLastMsg").addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            lsSeenerLastMsg = "";
                                                            if (snapshot.exists())
                                                                lsSeenerLastMsg = snapshot.getValue().toString();

                                                            String[] Arr_seenerID = lsSeenerLastMsg.split(",");
                                                            boolean CurrUserSeen = false;
                                                            for (String seenerID : Arr_seenerID) {
                                                                if (seenerID.compareTo(userID) == 0) {
                                                                    CurrUserSeen = true;
                                                                    break;
                                                                }
                                                            }
                                                            if (!CurrUserSeen) {
                                                                lsSeenerLastMsg += "," + userID;

                                                                ref_chat.child(groupID).child("lsSeenerLastMsg").setValue(lsSeenerLastMsg);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {
                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    } catch (Exception e) {
                                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {}

        lvChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i != lastSelected) {
//                    if (lastSelected != -1)
//                        tvDate.setVisibility(View.GONE);
//                    lastSelected = i;

                    ConstraintLayout layout = view.findViewById(R.id.left_layout);
                      //---------
                    if (layout.getVisibility() == View.VISIBLE) {
                        tvDate = view.findViewById(R.id.tv_date);
                        tvMsg = view.findViewById(R.id.tv_chat_message); //---------
                    } else {
                        tvDate = view.findViewById(R.id.tv_date_current_user);
                        tvMsg = view.findViewById(R.id.tv_chat_message_current_user); //-------
                    }
               //     tvDate.setVisibility(View.VISIBLE);

//                } else {
                    if (tvDate.getVisibility() == View.GONE)
                        tvDate.setVisibility(View.VISIBLE);
                    else
                        tvDate.setVisibility(view.GONE);
//                }
//                tvMsg.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                    @Override
//                    public void onLayoutChange(View view, int i, int i1, int i2, int Bottom, int i4, int i5, int i6, int oldBottom) {
//                        if (Bottom > oldBottom) {
//                            Toast.makeText(getApplicationContext(), String.valueOf(lvChat.getScrollY()), Toast.LENGTH_SHORT).show();
//                            lvChat.scrollBy(0, Bottom - oldBottom);
//               //             Toast.makeText(getApplicationContext(), "later: " + String.valueOf(lvChat.getScrollY()), Toast.LENGTH_SHORT).show();
//                        } else {
//                            //          lvChat.setScrollY(0);
//                            lvChat.scrollBy(0, Bottom - oldBottom);
//                        }
//                        tvMsg.removeOnLayoutChangeListener(this);
//                    }
//                });
            }
        });
    }

    private void addSeenerListener () {
        try {
            ref_chat.child(groupID).child("lsSeenerLastMsg").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        lsSeenerLastMsg = snapshot.getValue().toString();

                        if (GroupMemID.length() == lsSeenerLastMsg.length())
                            tvSeener.setText("Mọi người đã xem");
                        else {
                            lsSeenerName = "";
                            MemArray = lsSeenerLastMsg.split(",");
                            for (String mem : MemArray)
                                lsSeenerName += (htID_Name.get(mem) + ", ");
                            lsSeenerName += "*";
                            tvSeener.setText(lsSeenerName.replace(", *", " đã xem"));
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}