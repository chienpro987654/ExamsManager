package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Assignment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Assignment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Assignment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Assignment.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Assignment newInstance(String param1, String param2) {
        Fragment_Assignment fragment = new Fragment_Assignment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    Boolean isRead =false;
    String curr_userID;
    String joined_group;
    String[] groupIDs;
    String joinedGroupName = " ";
    DatabaseReference ref_user = FirebaseDatabase.getInstance().getReference("User");
    DatabaseReference ref_group = FirebaseDatabase.getInstance().getReference("Group");
    DatabaseReference ref_chat = FirebaseDatabase.getInstance().getReference("Chat");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__assignment, container, false);

        //Get id of user
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        SharedPreferences preferences = activity.getSharedPreferences("UserID", Context.MODE_PRIVATE);
        curr_userID = preferences.getString("userID","Unknown");

        ListView listView = view.findViewById(R.id.lv_all_chat_room);
        TextView textView = view.findViewById(R.id.tv_empty_group_chat);

        List<ChatGroupOverviewClass> groupList = new ArrayList<ChatGroupOverviewClass>();
        ShowAllGroupChatAdapter adapter = new ShowAllGroupChatAdapter(getActivity(),R.layout.item_group_chat,groupList);
        listView.setAdapter(adapter);

        ref_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String joined_group = snapshot.child(curr_userID).child("joined_group").getValue().toString();
                if(joined_group.matches("Unknown"))
                {
                    textView.setVisibility(View.VISIBLE);
                    return;
                }
                groupIDs = joined_group.split(",",0);

                for (int i = 0; i < groupIDs.length; i++) {
                    String groupID = groupIDs[i];
                    ref_group.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String groupName = Objects.requireNonNull(snapshot.child(groupID).child("groupName").getValue()).toString();
                            String groupImg = snapshot.child(groupID).child("groupImg").getValue().toString();

                            ref_chat.child(groupID).child("msg").limitToLast(1).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                        for (DataSnapshot ss : snapshot.getChildren()) {
                                            ChatMessageClass chatMessage = ss.getValue(ChatMessageClass.class);
                                            String chatUserID = chatMessage.getUserID();
                                            String chatMsg = chatMessage.getMsg();
                                            String fullTimeS = chatMessage.getDate().toString();
                                            Long fullTimeL = Long.parseLong(fullTimeS);
                                            Log.i("chatUser",chatMessage.getUserID().toString());
                                            Log.i("chatMsg",chatMessage.getMsg().toString());
                                            Log.i("chatDate",chatMessage.getDate().toString());

                                            ref_chat.child(groupID).child("lsSeenerLastMsg").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists())
                                                    {
                                                        String readUser = snapshot.getValue().toString();
                                                        String[] arrReadUser= readUser.split(",");
                                                        if (Arrays.asList(arrReadUser).contains(curr_userID)) {
                                                            isRead=true;
                                                        }
                                                        else
                                                            isRead=false;

                                                    }
                                                    else
                                                        isRead=true;
                                                    ChatGroupOverviewClass existGroup = groupList.stream()
                                                            .filter(item -> groupID.equals(item.getGroupID()))
                                                            .findAny()
                                                            .orElse(null);
                                                    if(existGroup!=null)
                                                    {
                                                        int index = groupList.indexOf(existGroup);
                                                        groupList.get(index).setRead(isRead);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });

                                            ref_user.child(chatUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(snapshot.exists())
                                                    {
                                                        String chatUserName;
                                                        if(chatUserID.matches(curr_userID))
                                                            chatUserName="Bạn";
                                                        else
                                                            chatUserName = snapshot.child("name").getValue().toString();
                                                        String lastMsg = chatUserName+": "+chatMsg;
                                                        ChatGroupOverviewClass existGroup = groupList.stream()
                                                                .filter(item -> groupID.equals(item.getGroupID()))
                                                                .findAny()
                                                                .orElse(null);
                                                        if(existGroup!=null)
                                                        {
                                                            int index = groupList.indexOf(existGroup);
                                                            groupList.get(index).setGroupLastMess(lastMsg);
                                                            groupList.get(index).setGroupLastTime(fullTimeL);
                                                        }
                                                        else
                                                        {
                                                            groupList.add(new ChatGroupOverviewClass(groupID,groupName, groupImg, lastMsg,fullTimeL,isRead));
                                                        }
                                                        groupList.sort(new ComparatorForChatGroupLastTime());
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    }
                                    else
                                    {
                                        groupList.add(new ChatGroupOverviewClass(groupID,groupName, groupImg, " ",0l,isRead));
                                        groupList.sort(new ComparatorForChatGroupLastTime());
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ChatRoomClass.class);
                intent.putExtra("groupID",groupList.get(position).getGroupID());
                intent.putExtra("groupName",groupList.get(position).getGroupName());
                startActivity(intent);
            }
        });
        return view;
    }
}