package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class CreateAssignment extends AppCompatActivity {
    DatabaseReference firebase;
    Intent intent;

    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference("User");
    DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference("Group");
    DatabaseReference assignmentReference = FirebaseDatabase.getInstance().getReference("Assignment");

    DatePickerDialog.OnDateSetListener dateStartSetListener;
    DatePickerDialog.OnDateSetListener dateEndSetListener;
    TimePickerDialog.OnTimeSetListener timeStartSetListener;
    TimePickerDialog.OnTimeSetListener timeEndSetListener;
    TimePickerDialog.OnTimeSetListener doingTimeSetListener;
    EditText etAssignName;
    TextView tvChoseDateStart;
    TextView tvSettingMode;
    TextView tvViewContent;
    TextView tvChoseDateEnd;
    TextView tvChoseTimeStart;
    TextView tvChoseTimeEnd;
    TextView tvChoseDoingTime;
    EditText etQuestionSetCode;
    EditText etMaxAttempt;
    Button btCreateAssign;
    CheckBox cbAddContent;

    String AssignName = "";
    String TimeStart = "";
    String DateStart = "";
    String TimeEnd = "";
    String DateEnd = "";
    String DateTimeStart;
    String DateTimeEnd;
    String DoingTime = "";
    String QuestionSetCode = "";

    int StartHour, StartMinute, StartDay, StartMonth, StartYear;
    int EndHour, EndMinute, EndDay, EndMonth, EndYear;
    int maxAttempt;

    String userID;
    String groupID;
    String assignID;
    String quesSetID;
    String groupName;

    boolean isNew;
    boolean defaultNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assignment);

        etAssignName = (EditText) findViewById(R.id.et_assignment_name);
        tvChoseTimeStart = (TextView) findViewById(R.id.tv_chose_time_start);
        tvChoseDateStart = (TextView) findViewById(R.id.tv_chose_date_start);
        tvChoseTimeEnd = (TextView) findViewById(R.id.tv_chose_time_end);
        tvChoseDateEnd = (TextView) findViewById(R.id.tv_chose_date_end);
        tvChoseDoingTime = (TextView) findViewById(R.id.tv_chose_doingTime);
        etQuestionSetCode = (EditText) findViewById(R.id.et_questionset_code);
        btCreateAssign = (Button) findViewById(R.id.bt_create_assignment);
        tvViewContent = (TextView) findViewById(R.id.tv_view_notify_content);
        cbAddContent = (CheckBox) findViewById(R.id.cb_add_notify_content);
        tvSettingMode = (TextView) findViewById(R.id.tv_assign_setting_mode);
        etMaxAttempt = (EditText) findViewById(R.id.et_choose_max_attempt);

        intent = getIntent();

        groupID = intent.getStringExtra("groupID");

        if (intent.hasExtra("assignID"))
        {
            assignID = intent.getStringExtra("assignID");
            isNew=false;
            assignmentReference.child(assignID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        if(snapshot.child("maxAttempt").exists())
                            etMaxAttempt.setText(snapshot.child("maxAttempt").getValue().toString());
                        if(snapshot.child("setHash").exists())
                            etQuestionSetCode.setText(snapshot.child("setHash").getValue().toString());
                        if(snapshot.child("doingTime").exists())
                            tvChoseDoingTime.setText(snapshot.child("doingTime").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else
            isNew = true;


        if (intent.hasExtra("assignName"))
        {
            etAssignName.setText(intent.getStringExtra("assignName"));
        }

        if (intent.hasExtra("datetimeStart")){
            String datetime = intent.getStringExtra("datetimeStart");
            String[] array = datetime.split("\\.");
            TimeStart = array[0];
            DateStart = array[1];
            tvChoseTimeStart.setText(TimeStart);
            tvChoseDateStart.setText(DateStart);

            array = TimeStart.split(":");
            StartHour = Integer.parseInt(array[0]);
            StartMinute = Integer.parseInt(array[1]);

            array = DateStart.split("/");
            StartDay = Integer.parseInt(array[0]);
            StartMonth = Integer.parseInt(array[1]);
            StartYear = Integer.parseInt(array[2]);
        }

        if (intent.hasExtra("datetimeEnd")){
            String datetime = intent.getStringExtra("datetimeEnd");
            String[] array = datetime.split("\\.");
            TimeEnd = array[0];
            DateEnd = array[1];
            tvChoseTimeEnd.setText(TimeEnd);
            tvChoseDateEnd.setText(DateEnd);

            array = TimeEnd.split(":");
            EndHour = Integer.parseInt(array[0]);
            EndMinute = Integer.parseInt(array[1]);

            array = DateEnd.split("/");
            EndDay = Integer.parseInt(array[0]);
            EndMonth = Integer.parseInt(array[1]);
            EndYear = Integer.parseInt(array[2]);
        }

        if (intent.hasExtra("setHash")){
            etQuestionSetCode.setText(intent.getStringExtra("setHash"));
        }

        if (intent.hasExtra("maxAttempt")){
            etMaxAttempt.setText(intent.getStringExtra("maxAttempt"));
        }

        if (intent.hasExtra("groupName"))
        {
            groupName = intent.getStringExtra("groupName");
        }

        if(isNew)
        {
            tvSettingMode.setText("Bạn đang tạo một bài làm mới!");
            tvSettingMode.setTextColor(getResources().getColor(R.color.black));
        }
        else
        {
            tvSettingMode.setText("Bạn đang chỉnh sửa bài làm " + etAssignName.getText().toString()+"! ");
            tvSettingMode.setTextColor(getResources().getColor(R.color.red_basic));
        }

        if (intent.hasExtra("doingTime"))
        {
            DoingTime = intent.getStringExtra("doingTime");
        }

        cbAddContent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (cbAddContent.isChecked())
                {
                    ShowNotifyContentDialog();
                    tvViewContent.setVisibility(View.VISIBLE);
                    defaultNotification=false;
                }
                else
                {
                    tvViewContent.setVisibility(View.GONE);
                    defaultNotification=true;
                }
            }
        });

        tvChoseTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour =  cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        CreateAssignment.this, R.style.Theme_AppCompat_Light_Dialog,
                        timeStartSetListener, hour, minute, true);
                dialog.show();
            }
        });

        timeStartSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                TimeStart = String.valueOf(i) + ":" + String.valueOf(i1);
                tvChoseTimeStart.setText(TimeStart);
                StartHour = i;
                StartMinute = i1;
            }
        };

        tvChoseDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateAssignment.this, R.style.Theme_AppCompat_Light_Dialog,
                        dateStartSetListener, year, month, day);
                dialog.show();
            }
        });

        dateStartSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                DateStart = String.valueOf(i2) + "/" +
                        String.valueOf(i1 + 1) + "/" + String.valueOf(i);
                tvChoseDateStart.setText(DateStart);
                StartDay = i2;
                StartMonth = i1 + 1;
                StartYear = i;
            }
        };

        tvChoseTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour =  cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        CreateAssignment.this, R.style.Theme_AppCompat_Light_Dialog,
                        timeEndSetListener, hour, minute, true);
                dialog.show();
            }
        });

        timeEndSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                TimeEnd = String.valueOf(i) + ":" + String.valueOf(i1);
                tvChoseTimeEnd.setText(TimeEnd);
                EndHour = i;
                EndMinute = i1;
            }
        };

        tvChoseDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        CreateAssignment.this, R.style.Theme_AppCompat_Light_Dialog,
                        dateEndSetListener, year, month, day);
                dialog.show();
            }
        });

        dateEndSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                DateEnd = String.valueOf(i2) + "/" +
                        String.valueOf(i1 + 1) + "/" + String.valueOf(i);
                tvChoseDateEnd.setText(DateEnd);
                EndDay = i2;
                EndMonth = i1 + 1;
                EndYear = i;
            }
        };

        tvChoseDoingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int hour =  cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog dialog = new TimePickerDialog(
                        CreateAssignment.this, R.style.Theme_AppCompat_Light_Dialog,
                        doingTimeSetListener, hour, minute, true);
                dialog.show();
            }
        });

        doingTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                DoingTime = i + ":" + i1;
                tvChoseDoingTime.setText(i + "h : " + i1 + "m");
            }
        };

        btCreateAssign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AssignName = etAssignName.getText().toString();
                if (!IsValidInformation())
                    return;
                maxAttempt = Integer.parseInt(etMaxAttempt.getText().toString());
                QuestionSetCode = etQuestionSetCode.getText().toString();

                DateTimeStart = TimeStart + "." + DateStart;
                DateTimeEnd = TimeEnd + "." + DateEnd;
                SharedPreferences preferences = getSharedPreferences("UserID", MODE_PRIVATE);
                userID = preferences.getString("userID", "Unknown");

                DatabaseReference ref_assign =
                        FirebaseDatabase.getInstance().getReference("Assignment");

                if(etQuestionSetCode.getText().toString().matches(""))
                {
                    Toast.makeText(getApplicationContext(),"Bạn chưa điền mã số của bộ câu hỏi!",Toast.LENGTH_LONG).show();
                    return;
                };

                DatabaseReference ref_hash =
                        FirebaseDatabase.getInstance().getReference("Hash");

                Query hashQuerry = ref_hash.child(etQuestionSetCode.getText().toString());
                hashQuerry.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.exists())
                        {
                            Toast.makeText(getApplicationContext(), "Lỗi: Mã bộ câu hỏi không tồn tại", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else
                        {
                            quesSetID = snapshot.getValue().toString();
                            Log.i("quesSetID",quesSetID);

                            ArrayList<String> listID = new ArrayList<>();
                            ref_assign.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(isNew)
                                    {
                                        for (DataSnapshot ss : snapshot.getChildren()) {
                                            String id = String.valueOf(ss.child("asID").getValue());
                                            listID.add(id);
                                        }

                                        boolean isCreated = false;
                                        long counter = 1;
                                        while (!isCreated) {
                                            String id = "assign" + counter;
                                            if (listID.contains(id))
                                                counter++;
                                            else isCreated = true;
                                        }
                                        assignID = "assign" + counter;

                                        Assignment_Class assign = new Assignment_Class(
                                                AssignName, userID, groupID, assignID, quesSetID,
                                                "trans" + assignID, DateTimeStart,
                                                DateTimeEnd, "On going", String.valueOf(maxAttempt),
                                                DoingTime, QuestionSetCode);

                                        ref_assign.child(assignID).setValue(assign);

                                        DatabaseReference ref_group =
                                                FirebaseDatabase.getInstance().getReference("Group");
                                        DatabaseReference ref_user =
                                                FirebaseDatabase.getInstance().getReference("User");

                                        ref_group.child(groupID).child("groupMem").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists())
                                                {
                                                    String[] memArr = snapshot.getValue().toString().split(",");
                                                    for(int i=0;i<memArr.length;i++)
                                                    {
                                                        String currUserID = memArr[i];
                                                        ref_assign.child(assignID).child("participant").child(memArr[i]).setValue("0");
                                                        ref_assign.child(assignID).child("transcript").child(memArr[i]).setValue("Unknown");
                                                        ref_user.child(memArr[i]).child("notification").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                String newNotID;
                                                                if(snapshot.exists()) {
                                                                    long countNotify = snapshot.getChildrenCount();
                                                                    newNotID = "no"+String.valueOf(countNotify+1);
                                                                }
                                                                else {
                                                                    newNotID = "no1";

                                                                }
                                                                String date_happen = DateTimeStart;
                                                                Date currentTime = Calendar.getInstance().getTime();
                                                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm.dd/MM/yyyy");
                                                                String date = dateFormat.format(currentTime);
                                                                String title = AssignName;
                                                                String content;
                                                                if(!defaultNotification)
                                                                {
                                                                    content = tvViewContent.getText().toString() + ". Bắt đầu: " + DateTimeStart +". Kết thúc: "+DateTimeEnd;
                                                                }
                                                                else
                                                                {
                                                                    content = AssignName + ". Bắt đầu: " + DateTimeStart +". Kết thúc: "+DateTimeEnd;
                                                                }
                                                                Notify_Class notify = new Notify_Class(newNotID, groupName, date, date_happen, title, content,"false");
                                                                ref_user.child(currUserID).child("notification").child(newNotID).setValue(notify);
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

                                        groupReference.child(groupID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String allAsID = snapshot.child("allAsID").getValue().toString();
                                                String newAllAsID;
                                                if(allAsID.matches("") || allAsID.matches("Unknown"))
                                                {
                                                    newAllAsID = assignID;
                                                }
                                                else
                                                {
                                                    newAllAsID = allAsID+","+assignID;
                                                }
                                                groupReference.child(groupID).child("allAsID").setValue(newAllAsID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getApplicationContext(),"Đã tạo "+AssignName,Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getApplicationContext(),MainGroupActivity.class);
                                                        intent.putExtra("userID",userID);
                                                        intent.putExtra("groupID",groupID);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }
                                    else
                                    {
                                        DatabaseReference ref_group =
                                                FirebaseDatabase.getInstance().getReference("Group");
                                        DatabaseReference ref_user =
                                                FirebaseDatabase.getInstance().getReference("User");

                                        ref_group.child(groupID).child("groupMem").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(snapshot.exists())
                                                {
                                                    String[] memArr = snapshot.getValue().toString().split(",");
                                                    for(int i=0;i<memArr.length;i++)
                                                    {
                                                        String currUserID = memArr[i];
                                                        ref_user.child(memArr[i]).child("notification").addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                String newNotID;
                                                                if(snapshot.exists()) {
                                                                    long countNotify = snapshot.getChildrenCount();
                                                                    newNotID = "no"+String.valueOf(countNotify+1);
                                                                }
                                                                else {
                                                                    newNotID = "no1";

                                                                }
                                                                String date_happen = DateTimeStart;
                                                                Date currentTime = Calendar.getInstance().getTime();
                                                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm.dd/MM/yyyy");
                                                                String date = dateFormat.format(currentTime);
                                                                String title = AssignName;
                                                                String content;
                                                                if(!defaultNotification)
                                                                {
                                                                    content = tvViewContent.getText().toString() + ". Bắt đầu: " + DateTimeStart +". Kết thúc: "+DateTimeEnd;
                                                                }
                                                                else
                                                                {
                                                                    content = AssignName + ". Bắt đầu: " + DateTimeStart +". Kết thúc: "+DateTimeEnd;
                                                                }
                                                                Notify_Class notify = new Notify_Class(newNotID, groupName, date, date_happen, title, content,"false");
                                                                ref_user.child(currUserID).child("notification").child(newNotID).setValue(notify);
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
                                        ref_assign.child(assignID).child("asName").setValue(AssignName);
                                        ref_assign.child(assignID).child("date_end").setValue(DateTimeEnd);
                                        ref_assign.child(assignID).child("date_end").setValue(DateTimeEnd);
                                        ref_assign.child(assignID).child("setID").setValue(quesSetID);
                                        ref_assign.child(assignID).child("setHash").setValue(QuestionSetCode);
                                        ref_assign.child(assignID).child("doingTime").setValue(DoingTime);
                                        ref_assign.child(assignID).child("maxAttempt").setValue(maxAttempt).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getApplicationContext(),"Đã chỉnh sửa "+AssignName,Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(),MainGroupActivity.class);
                                                intent.putExtra("userID",userID);
                                                intent.putExtra("groupID",groupID);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
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
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAssignment.this);
        builder.setTitle("Bạn có chắc chắn muốn thoát? ")
                .setMessage("Mọi thay đổi sẽ không được lưu")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        groupID = intent.getStringExtra("groupID");
                        SharedPreferences preferences = getSharedPreferences("UserID", MODE_PRIVATE);
                        userID = preferences.getString("userID", "Unknown");
                        Intent intent = new Intent(getApplicationContext(),MainGroupActivity.class);
                        intent.putExtra("userID",userID);
                        intent.putExtra("groupID",groupID);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.cancel();
                    }
                })
                .show();
    }

    //
    private void ShowNotifyContentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAssignment.this);
        builder.setTitle("Nhập thông báo:");

        // Set up the input
        final EditText input = new EditText(getApplicationContext());
        input.setMinLines(2);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvViewContent.setText(input.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private boolean IsValidInformation() {
        if (AssignName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Tên bài làm trống!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TimeStart.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Bạn chưa chọn giờ bắt đầu!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (DateStart.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Bạn chưa chọn ngày bắt đầu!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (TimeEnd.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Bạn chưa chọn giờ kết thúc!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (DateEnd.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Bạn chưa chọn ngày kết thúc!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (StartYear > EndYear) {
            Toast.makeText(getApplicationContext(),"Năm bắt đầu không hợp lệ. " +
                    StartYear + " > end year " + EndYear,Toast.LENGTH_SHORT).show();
            return false;
        } else if (StartYear == EndYear && StartMonth > EndMonth) {
            Toast.makeText(getApplicationContext(), "Tháng bắt đầu không hợp lệ. " +
                    StartMonth + " > end month " + EndMonth, Toast.LENGTH_SHORT).show();
            return false;
        } else if (StartYear == EndYear && StartMonth == EndMonth && StartDay > EndDay) {
            Toast.makeText(getApplicationContext(), "Ngày bắt đầu không hợp lệ.  " +
                    StartDay + " > end day " + EndDay, Toast.LENGTH_SHORT).show();
            return false;
        } else if (StartYear == EndYear && StartMonth == EndMonth && StartDay == EndDay &&
                StartHour > EndHour) {
            Toast.makeText(getApplicationContext(), "Giờ bắt đầu không hợp lệ. " +
                    StartHour + " > end hour " + EndHour, Toast.LENGTH_SHORT).show();
            return false;
        } else if (StartYear == EndYear && StartMonth == EndMonth && StartDay == EndDay &&
                StartHour == EndHour && StartMinute >= EndMinute) {
            Toast.makeText(getApplicationContext(), "Phút bắt đầu không hợp lệ. " +
                    StartMinute + " >= end minute " + EndMinute, Toast.LENGTH_SHORT).show();
            return false;
        } else if (DoingTime.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Bạn chưa chọn thời gian làm bài!", Toast.LENGTH_SHORT).show();
            return false;
        } else if(etMaxAttempt.getText().toString().matches("")) {
            Toast.makeText(getApplicationContext(), "Số lần làm bài trống!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}