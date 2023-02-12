package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainGroupActivity extends AppCompatActivity {

    String userID;
    String groupID;
    String groupHash;
    Boolean isAdmin;
    DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference("Group");
    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_group);
        getSupportActionBar().hide();

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tl_main_group);
        TextView textView = (TextView) findViewById(R.id.tv_main_group_name);
        ViewPager viewPager = (ViewPager) findViewById(R.id.vp_tabview);
        ExtendedFloatingActionButton bt_mainGroup = (ExtendedFloatingActionButton) findViewById(R.id.efab_mainGroup);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getString("userID");
            groupID = extras.getString("groupID");
            groupHash=extras.getString("groupHash");

            Fragment_MainGroup_TabAdapter adapter = new Fragment_MainGroup_TabAdapter(getSupportFragmentManager(), extras);
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);

            groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String groupAdmin = Objects.requireNonNull(snapshot.child(groupID).child("groupAdmin").getValue()).toString();
                    String groupName = Objects.requireNonNull(snapshot.child(groupID).child("groupName").getValue()).toString();
                    textView.setText(groupName);
                    if(userID.equals(groupAdmin)) {
                        isAdmin=true;
                    }
                    else {
                        isAdmin=false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

        }

        bt_mainGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog groupInfDialog= new Dialog(MainGroupActivity.this);
                groupInfDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                groupInfDialog.setContentView(R.layout.group_inf_dialog);

                Window window  = groupInfDialog.getWindow();
                if(window == null)
                    return;

                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams winAttributes = window.getAttributes();
                winAttributes.gravity = Gravity.CENTER;
                window.setAttributes(winAttributes);

                groupInfDialog.setCancelable(true);

                EditText editText = groupInfDialog.findViewById(R.id.et_join_group_code_in_grInf);
                ListView listView =groupInfDialog.findViewById(R.id.lv_group_inf_group_mem);
                Button button = groupInfDialog.findViewById(R.id.bt_leave_grInf_dialog);

                TextView tv_copy = groupInfDialog.findViewById(R.id.tv_copy_joincode_group_inf);

                tv_copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String hash = editText.getText().toString();
                        ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Copy text",hash);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getApplicationContext(), "Đã sao chép vào clipboard, Mã: "+hash, Toast.LENGTH_SHORT).show();
                    }
                });

                if(isAdmin)
                    button.setVisibility(View.GONE);
                else
                    button.setVisibility(View.VISIBLE);

                List<GroupMember> groupMembers = new ArrayList<GroupMember>();
                Member_View_Adapter adapter = new Member_View_Adapter(getApplicationContext(),R.layout.maingroup_member_item,groupMembers);
                listView.setAdapter(adapter);

                groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String groupAdmin = Objects.requireNonNull(snapshot.child(groupID).child("groupAdmin").getValue()).toString();
                        String groupMember = Objects.requireNonNull(snapshot.child(groupID).child("groupMem").getValue()).toString();
                        String groupHash = Objects.requireNonNull(snapshot.child(groupID).child("groupHash").getValue()).toString();
                        editText.setText(groupHash);
                        String[] allMemID = groupMember.split(",",0);

                        for(int i=0;i<allMemID.length;i++)
                        {
                            String memID = allMemID[i];
                            userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String name = Objects.requireNonNull(snapshot.child(memID).child("name").getValue()).toString();
                                    String email = Objects.requireNonNull(snapshot.child(memID).child("email").getValue()).toString();

                                    if(memID.equals(groupAdmin))
                                    {
                                        groupMembers.add(new GroupMember(name,email,true));
                                    }
                                    else
                                        groupMembers.add(new GroupMember(name,email,false));

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
                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String groupMem = Objects.requireNonNull(snapshot.child(groupID).child("groupMem").getValue()).toString();
                                String[] memArr = groupMem.split(",",0);
                                String newGroupMem="";
                                for(int i =0;i<memArr.length;i++)
                                {
                                    if(!memArr[i].equals(userID))
                                        newGroupMem += memArr[i]+",";
                                }
                                newGroupMem=newGroupMem.substring(0,newGroupMem.length()-1);
                                groupReference.child(groupID).child("groupMem").setValue(newGroupMem);
                                Log.i("Test",newGroupMem);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String joined_group = Objects.requireNonNull(snapshot.child(userID).child("joined_group").getValue()).toString();
                                String[] groupArr = joined_group.split(",",0);
                                String newJoined_Group="";

                                for(int i =0;i<groupArr.length;i++)
                                {
                                    if(!groupArr[i].equals(groupID))
                                        newJoined_Group += groupArr[i]+",";
                                }
                                if(newJoined_Group.matches(""))
                                    newJoined_Group="Unknown";
                                else
                                    newJoined_Group=newJoined_Group.substring(0,newJoined_Group.length()-1);

                                userReference.child(userID).child("joined_group").setValue(newJoined_Group).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        intent.putExtra("userID",userID);
                                        intent.putExtra("itemFrag",2);
                                        startActivity(intent);
                                        groupInfDialog.cancel();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        //
//                        Toast.makeText(getApplicationContext(), "Đã rời nhóm :((", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                        intent.putExtra("userID",userID);
//                        intent.putExtra("itemFrag",2);
//                        startActivity(intent);
//                        groupInfDialog.dismiss();
//                        finish();
                    }
                });

                groupInfDialog.show();
            }
        });

    }
}