package com.example.androidproject;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.SSLEngineResult;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_MainGroup_Assignment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_MainGroup_Assignment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_MainGroup_Assignment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment Fragment_MainGroup_Member.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_MainGroup_Assignment newInstance(Bundle bundle) {
        Fragment_MainGroup_Assignment fragment = new Fragment_MainGroup_Assignment();
        Bundle args = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    String userID;
    String groupID;
    String groupName;
    Boolean isAdmin;
    boolean canDo = true;

    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
    DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference("Group");
    DatabaseReference assignmentReference = FirebaseDatabase.getInstance().getReference("Assignment");

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
        View view = inflater.inflate(R.layout.fragment__main_group__assignment, container, false);
        ListView lv_showAssign = view.findViewById(R.id.lv_main_group_assignment);
        Button bt_addAssign = view.findViewById(R.id.bt_main_group_add_assignmnet);

        Bundle bundle = this.getArguments();
        userID = bundle.getString("userID");
        groupID = bundle.getString("groupID");
        groupName = bundle.getString("groupName");

        List<AssignmentViewClass> assignmentsList = new ArrayList<>();

        Assignment_View_Adapter adapter = new Assignment_View_Adapter(getContext(),R.layout.item_assignment,assignmentsList);
        lv_showAssign.setAdapter(adapter);

        lv_showAssign.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                groupReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String groupAdmin = Objects.requireNonNull(snapshot.child(groupID).child("groupAdmin").getValue()).toString();
                        if(userID.equals(groupAdmin)) {

                            isAdmin=true;

                            final Dialog adminDialog= new Dialog(getContext());
                            adminDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            adminDialog.setContentView(R.layout.main_group_admin_assignment_click_dialog);

                            Window window  = adminDialog.getWindow();
                            if(window == null)
                                return;

                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            WindowManager.LayoutParams winAttributes = window.getAttributes();
                            winAttributes.gravity = Gravity.CENTER;
                            window.setAttributes(winAttributes);
                            adminDialog.setCancelable(true);

                            Button bt_edit = adminDialog.findViewById(R.id.bt_main_group_assignment_dialog_edit);
                            Button bt_del = adminDialog.findViewById(R.id.bt_main_group_assignment_dialog_remove);
                            Button bt_statistic = adminDialog.findViewById(R.id.bt_main_group_assignment_dialog_statistic);
                            Button bt_do = adminDialog.findViewById(R.id.bt_main_group_assignment_dialog_do_assign);

                            bt_edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(),CreateAssignment.class);
                                    intent.putExtra("userID",userID);
                                    intent.putExtra("assignID",assignmentsList.get(i).asID);
                                    intent.putExtra("assignName",assignmentsList.get(i).asName);
                                    intent.putExtra("datetimeStart",assignmentsList.get(i).asBegin);
                                    intent.putExtra("datetimeEnd",assignmentsList.get(i).asEnd);
                                    intent.putExtra("groupID",groupID);
                                    intent.putExtra("groupName",groupName);
                                    intent.putExtra("doingTime",assignmentsList.get(i).getAsMaxTime());
                                    startActivity(intent);
                                    adminDialog.cancel();
                                    getActivity().finish();
                                }
                            });

                            bt_del.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(
                                            getActivity());
                                    builder.setTitle("Bạn có thật sự muốn xóa assigment này?");

                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int which) {
                                            String asID = assignmentsList.get(i).getAsID();

                                            assignmentReference.child(asID).removeValue();

                                            groupReference.child(groupID).child("allAsID").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    String allAsID = snapshot.getValue().toString();
                                                    if (allAsID.compareTo(asID) == 0)
                                                        allAsID = "Unknown";
                                                    else if (allAsID.contains(asID + ","))
                                                        allAsID = allAsID.replace(asID + ",", "");
                                                    else
                                                        allAsID = allAsID.replace("," + asID, "");

                                                    groupReference.child(groupID).child("allAsID").setValue(allAsID);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });

                                            assignmentsList.remove(i);

                                            adapter.notifyDataSetChanged();
                                            adminDialog.cancel();
                                            Toast.makeText(getContext(), "Đã xóa thành công " + assignmentsList.get(i).getAsName(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });

                                    builder.show();
                                }
                            });

                            bt_statistic.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(),AssignmentPointStatistic.class);
                                    intent.putExtra("userID",userID);
                                    intent.putExtra("assignID",assignmentsList.get(i).asID);
                                    intent.putExtra("assignName",assignmentsList.get(i).asName);
                                    intent.putExtra("groupName",groupName);
                                    startActivity(intent);
                                    adminDialog.cancel();
                                }
                            });


                            //
                            //DO
                            //
                            bt_do.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(),Assignment_Question.class);
                                    intent.putExtra("assignID",assignmentsList.get(i).asID);
                                    startActivity(intent);
                                    adminDialog.cancel();
                                }
                            });

                            adminDialog.show();
                        }
                        else {
                            isAdmin=false;

                            final Dialog memDialog= new Dialog(getContext());
                            memDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            memDialog.setContentView(R.layout.main_group_member_assignment_click_dialog);

                            Window window  = memDialog.getWindow();
                            if(window == null)
                                return;

                            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                            WindowManager.LayoutParams winAttributes = window.getAttributes();
                            winAttributes.gravity = Gravity.CENTER;
                            window.setAttributes(winAttributes);
                            memDialog.setCancelable(true);

                            Button bt_do = memDialog.findViewById(R.id.bt_main_group_assignment_dialog_mem_do_assign);
                            TextView tv_name = memDialog.findViewById(R.id.tv_main_group_assignment_dialog_mem_assign_name);
                            TextView tv_time_start = memDialog.findViewById(R.id.tv_main_group_assignment_dialog_mem_time_start);
                            TextView tv_time_end = memDialog.findViewById(R.id.tv_main_group_assignment_dialog_mem_time_end);
                            TextView tv_max_time = memDialog.findViewById(R.id.tv_main_group_assignment_dialog_mem_countdown_time);
                            TextView tv_max_attempt = memDialog.findViewById(R.id.tv_main_group_assignment_dialog_mem_max_attempt);
                            TextView tv_have_done = memDialog.findViewById(R.id.tv_main_group_assignment_dialog_mem_have_done);
                            TextView tv_point = memDialog.findViewById(R.id.tv_main_group_assignment_dialog_mem_point);

                            tv_name.setText(assignmentsList.get(i).getAsName());
                            tv_max_time.setText(assignmentsList.get(i).getAsMaxTime());
                            tv_time_start.setText(assignmentsList.get(i).getAsBegin());
                            tv_time_end.setText(assignmentsList.get(i).getAsEnd());
                            tv_max_attempt.setText(assignmentsList.get(i).getAsMaxAttempt());

                            assignmentReference.child(assignmentsList.get(i).getAsID()).child("participant").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                        String have_done = snapshot.getValue().toString();
                                        tv_have_done.setText(have_done);
                                        int have_done_int = Integer.parseInt(tv_have_done.getText().toString());
                                        int maxAttempt_int = Integer.parseInt(assignmentsList.get(i).getAsMaxAttempt());

                                        if(have_done_int>=maxAttempt_int || assignmentsList.get(i).getAsCountdown()=="0")
                                        {
                                            bt_do.setVisibility(View.GONE);
                                        }
                                        else
                                            bt_do.setVisibility(View.VISIBLE);
                                    }
                                    else
                                    {
                                        assignmentReference.child(assignmentsList.get(i).getAsID()).child("participant").child(userID).setValue(0);
                                        tv_have_done.setText("0");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            assignmentReference.child(assignmentsList.get(i).getAsID()).child("transcript").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists())
                                    {
                                        String point = snapshot.getValue().toString();
                                        tv_point.setText(point);
                                    }
                                    else
                                    {
                                        assignmentReference.child(assignmentsList.get(i).getAsID()).child("transcript").child(userID).setValue(0);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            bt_do.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(),Assignment_Question.class);
                                    intent.putExtra("assignID",assignmentsList.get(i).asID);
                                    startActivity(intent);
                                    memDialog.cancel();
                                }
                            });
                            memDialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
        });

        groupReference.child(groupID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("allAsID").exists())
                {
                    if(snapshot.child("groupAdmin").getValue().toString().matches(userID))
                    {
                        bt_addAssign.setVisibility(View.VISIBLE);
                        isAdmin=true;
                    }
                    else
                    {
                        bt_addAssign.setVisibility(View.GONE);
                        isAdmin=false;
                    }

                    String[] allAsIDArr = snapshot.child("allAsID").getValue().toString().split(",");
                    for(int i=0;i<allAsIDArr.length;i++)
                    {
                        assignmentReference.child(allAsIDArr[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    String asID = snapshot.child("asID").getValue().toString();
                                    String asName = snapshot.child("asName").getValue().toString();
                                    String asGroup = snapshot.child("groupID").getValue().toString();
                                    String admin =snapshot.child("admin").getValue().toString();
                                    String begin = snapshot.child("date_start").getValue().toString();
                                    String end = snapshot.child("date_end").getValue().toString();

                                    String maxAttempt = snapshot.child("maxAttempt").getValue().toString();
                                    String doingTime = snapshot.child("doingTime").getValue().toString();
                                    String setID = snapshot.child("setID").getValue().toString();
                                    String countdown;
                                    String status;

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm.dd/MM/yyyy");
                                    Date dateBegin, dateEnd;

                                    try
                                    {
                                        dateBegin = dateFormat.parse(begin);
                                        dateEnd = dateFormat.parse(end);
                                    }
                                    catch (Exception exception)
                                    {
                                        Log.i("EX",exception.toString());
                                        return;
                                    }
                                    Log.i("Begin",dateFormat.format(dateBegin));
                                    Log.i("End",dateFormat.format(dateEnd));

                                    Date currentTime = Calendar.getInstance().getTime();
                                    Log.i("Curr",dateFormat.format(currentTime));

                                    Long diff = dateEnd.getTime() - currentTime.getTime();
                                    if(diff>0)
                                    {
                                        int daysDiff = (int) (diff / (1000*60*60*24));
                                        int hoursDiff = (int) ((diff - (1000*60*60*24*daysDiff)) / (1000*60*60));
                                        int minDiff = (int) (diff - (1000*60*60*24*daysDiff) - (1000*60*60*hoursDiff)) / (1000*60);

                                        countdown =String.valueOf(daysDiff)+" ngày " + String.valueOf(hoursDiff) +" giờ "+ String.valueOf(minDiff)+" phút";
                                        Log.i("countdown",countdown);
                                        status ="Đang diễn ra";
                                    }
                                    else
                                    {
                                        countdown ="0";
                                        status = "Đã hết hạn";
                                    }

                                    if(!countdown.matches("0"))
                                    {
                                        assignmentsList.add(new AssignmentViewClass(asID,asName,admin,asGroup,begin,end,countdown,status,maxAttempt,doingTime,setID,diff));
                                    }
                                    else
                                    {
                                        if(isAdmin)
                                        assignmentsList.add(new AssignmentViewClass(asID,asName,admin,asGroup,begin,end,countdown,status,maxAttempt,doingTime,setID,diff));
                                    }

                                    assignmentsList.sort(new ComparatorForAssignmentCountDownTime());
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        bt_addAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateAssignment.class);
                intent.putExtra("userID", userID);
                intent.putExtra("groupID",groupID);
                intent.putExtra("groupName",groupName);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}