package com.example.androidproject;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Group#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Group extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Group() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Group.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Group newInstance(String param1, String param2) {
        Fragment_Group fragment = new Fragment_Group();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    String curr_userID;
    String joined_group;
    String[] groupIDs;
    String joinedGroupName = " ";
    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
    DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference("Group");
    DatabaseReference hashReference = FirebaseDatabase.getInstance().getReference("Hash");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__group, container, false);
        GridView gridView = view.findViewById(R.id.gv_group);
        ExtendedFloatingActionButton efab= view.findViewById(R.id.efab_group);
        TextView tv_empty = view.findViewById(R.id.tv_empty_group);

        List<Group> groupList = new ArrayList<Group>();

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        SharedPreferences preferences = activity.getSharedPreferences("UserID", Context.MODE_PRIVATE);
        curr_userID = preferences.getString("userID","Unknown");

        Query checkUser = userReference.orderByChild("name").equalTo(curr_userID);
        Fragment_Group_GridViewAdapter adapter = new Fragment_Group_GridViewAdapter(getActivity(),R.layout.group_item,groupList);
        gridView.setAdapter(adapter);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                joined_group = snapshot.child(curr_userID).child("joined_group").getValue().toString();
                if(joined_group.matches("Unknown"))
                {
                        tv_empty.setVisibility(View.VISIBLE);
                    return;
                }
                groupIDs = joined_group.split(",",0);

                for (int i = 0; i < groupIDs.length; i++) {
                    String groupID = groupIDs[i];
                    groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String groupName = Objects.requireNonNull(snapshot.child(groupID).child("groupName").getValue()).toString();
                            String groupImg = snapshot.child(groupID).child("groupImg").getValue().toString();
                            String groupHash = snapshot.child(groupID).child("groupHash").getValue().toString();
                            groupList.add(new Group(groupID,groupName, groupImg, groupHash));
                            groupList.sort(new ComparatorForGroupName());
                            adapter.notifyDataSetChanged();
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

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), MainGroupActivity.class);
                intent.putExtra("userID", curr_userID);
                intent.putExtra("groupID",groupList.get(i).getGroupID());
                intent.putExtra("groupHash",groupList.get(i).getGroupHash());
                intent.putExtra("groupName",groupList.get(i).getGroupName());
                startActivity(intent);
            }
        });

        efab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog joinDialog= new Dialog(getContext());
                joinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                joinDialog.setContentView(R.layout.join_group_dialog);

                Window window  = joinDialog.getWindow();
                if(window == null)
                    return;

                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams winAttributes = window.getAttributes();
                winAttributes.gravity = Gravity.CENTER;
                window.setAttributes(winAttributes);

                joinDialog.setCancelable(true);

                EditText et_join_code = joinDialog.findViewById(R.id.et_join_group_code);
                Button bt_join = joinDialog.findViewById(R.id.bt_join_dialog);
                TextView tv_create = joinDialog.findViewById(R.id.tv_join_group_create);

                bt_join.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(et_join_code.getText().toString().matches(""))
                        {
                            Toast.makeText(getContext(),"Bạn phải điền mã nhóm vào!",Toast.LENGTH_LONG).show();
                            return;
                        };

                        Query hashQuerry = hashReference.child(et_join_code.getText().toString());
                        hashQuerry.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(!snapshot.exists())
                                {
                                    Log.i("Err code","Err");
                                    Toast.makeText(getContext(),"Lỗi: Mã nhóm không tồn tại",Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    String groupJoinID = snapshot.getValue().toString();
                                    Log.i("Group found",groupJoinID);

                                    groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String groupMem = Objects.requireNonNull(snapshot.child(groupJoinID).child("groupMem").getValue()).toString();
                                            joinedGroupName = Objects.requireNonNull(snapshot.child(groupJoinID).child("groupName").getValue()).toString();
                                            Log.i("Old group mem",groupMem);

                                            String[] groupMemArr = groupMem.split(",",0);
                                            if(Arrays.asList(groupMemArr).contains(curr_userID)){
                                                Log.i("Have joined","Have joined"+groupJoinID);
                                                return;
                                            }

                                            String newGroupMem = groupMem+","+ curr_userID;
                                            groupReference.child(groupJoinID).child("groupMem").setValue(newGroupMem);
                                            Log.i("New group mem",newGroupMem);
                                            adapter.notifyDataSetChanged();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String joinedGroup = Objects.requireNonNull(snapshot.child(curr_userID).child("joined_group").getValue()).toString();

                                            if(joinedGroup.contains(groupJoinID))
                                                return;

                                            String newJoinedGroup = "";
                                            if(joinedGroup.matches("Unknown"))
                                                newJoinedGroup = groupJoinID;
                                            else
                                                newJoinedGroup = joinedGroup +","+groupJoinID;

                                            userReference.child(curr_userID).child("joined_group").setValue(newJoinedGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getContext(),"Đã tham gia nhóm "+joinedGroupName,Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getContext(),MainActivity.class);
                                                    intent.putExtra("userID",curr_userID);
                                                    intent.putExtra("itemFrag",2);
                                                    startActivity(intent);
                                                    joinDialog.cancel();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                };
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                });

                tv_create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog createDialog= new Dialog(getContext());
                        createDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        createDialog.setContentView(R.layout.create_group_dialog);

                        Window window  = createDialog.getWindow();
                        if(window == null)
                            return;

                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        WindowManager.LayoutParams winAttributes = window.getAttributes();
                        winAttributes.gravity = Gravity.CENTER;
                        window.setAttributes(winAttributes);

                        createDialog.setCancelable(true);

                        EditText et_create_group_name = createDialog.findViewById(R.id.et_create_group_name);
                        Button bt_create = createDialog.findViewById(R.id.bt_create_dialog);
                        EditText et_shared_code = createDialog.findViewById(R.id.et_create_group_join_code);
                        TextView tv_copy = createDialog.findViewById(R.id.tv_copy_join_code_create_dialog);

                        tv_copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String hash = et_shared_code.getText().toString();
                                if(!hash.matches(""))
                                {
                                    ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("Copy text",hash);
                                    clipboard.setPrimaryClip(clip);
                                    Toast.makeText(getContext(), "Đã sao chép vào clipboard, Mã: "+hash, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        bt_create.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(et_create_group_name.getText().toString().matches(""))
                                {
                                    Toast.makeText(getContext(),"Bạn phải điền  nhóm vào!",Toast.LENGTH_LONG).show();
                                    return;
                                };

                                groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        long countGroup = snapshot.getChildrenCount();
                                        Log.i("Group Number",String.valueOf(countGroup));
                                        String newGroupID = "group"+String.valueOf(countGroup+1);
                                        String newGroupName = et_create_group_name.getText().toString();
                                        String newGroupAdmin = curr_userID;
                                        String newGroupMem = curr_userID;

                                        RandomClass randomClass = new RandomClass();
                                        String newGroupImg = String.valueOf(randomClass.getRandomInteger(9));

                                        String uniq = newGroupID.substring(5,newGroupID.length());
                                        String newGroupHash = randomClass.getRandomString(10-uniq.length())+uniq;


                                        //public Group(String groupId, String groupName, String groupImg, String groupAdmin, String groupMember)
                                        Group newGroup = new Group(newGroupID,newGroupName,newGroupImg,newGroupAdmin,newGroupMem,newGroupHash);
                                        groupReference.child(newGroupID).setValue(newGroup);


                                        hashReference.child(newGroupHash).setValue(newGroupID);
                                        et_shared_code.setText(newGroupHash);

                                        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String joinedGroup = Objects.requireNonNull(snapshot.child(curr_userID).child("joined_group").getValue()).toString();

                                                String newJoinedGroup = "";
                                                if(joinedGroup.matches("Unknown"))
                                                    newJoinedGroup = newGroupID;
                                                else
                                                    newJoinedGroup = joinedGroup +","+newGroupID;
                                                userReference.child(curr_userID).child("joined_group").setValue(newJoinedGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        final Timer timer2 = new Timer();
                                                        timer2.schedule(new TimerTask() {
                                                            public void run() {
                                                                Toast.makeText(getContext(),"Đã tạo nhóm "+newGroupName,Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(getContext(),MainActivity.class);
                                                                intent.putExtra("userID",curr_userID);
                                                                intent.putExtra("itemFrag",2);
                                                                startActivity(intent);
                                                                createDialog.cancel();
                                                                timer2.cancel(); //this will cancel the timer of the system
                                                            }
                                                        }, 5000);


                                                    }
                                                });
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

//                                Intent intent = new Intent(getContext(),MainActivity.class);
//                                intent.putExtra("userID",curr_userID);
//                                intent.putExtra("itemFrag",2);
//                                startActivity(intent);
                            }
                        });

                        joinDialog.cancel();
                        createDialog.show();
                    }
                });

                joinDialog.show();

            }
        });
        return view;
    }
}