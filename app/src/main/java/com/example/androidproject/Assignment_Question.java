package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Assignment_Question extends AppCompatActivity {
    DatabaseReference firebase;
    TextView tvTimeLeft, tvQuestionNumber, tvQuestion, tvSubmit;
    RadioGroup rgAnswer;
    RadioButton rbAnswer_1, rbAnswer_2, rbAnswer_3, rbAnswer_4;
    int lastCheckedID = -1;
    Button btNext, btSubmit;
    ArrayList<Question_Class> questions;
    int questionNumber = 1;
    float Grade = 0;
    int TimeLeft = 1;
    String userID, assignID, setID, doingTime, attemp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_question);

        tvTimeLeft = findViewById(R.id.tv_time_left);
        tvQuestionNumber = findViewById(R.id.tv_question_counter);
        tvQuestion = findViewById(R.id.tv_question);
        tvSubmit = findViewById(R.id.tv_Submit);
        rgAnswer = findViewById(R.id.radioGroup_answer);
        rbAnswer_1 = findViewById(R.id.radio_button_1);
        rbAnswer_2 = findViewById(R.id.radio_button_2);
        rbAnswer_3 = findViewById(R.id.radio_button_3);
        rbAnswer_4 = findViewById(R.id.radio_button_4);
        btNext = findViewById(R.id.Btn_Next_Question);
        btSubmit = findViewById(R.id.Btn_Submit);

        firebase = FirebaseDatabase.getInstance().getReference();

        questions = new ArrayList<>();

        SharedPreferences preferences = getSharedPreferences("UserID", MODE_PRIVATE);
        userID = preferences.getString("userID", "Unknown");

        Intent intent = getIntent();
        assignID = intent.getStringExtra("assignID");

        firebase.child("Assignment").child(assignID).child("setID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setID = snapshot.getValue().toString();

                firebase.child("QuestionSet").child(setID).child("ques").addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    for (int i=1; i<=snapshot.getChildrenCount(); i++) {
                                        Question_Class question = snapshot.child("ques" +
                                                i).getValue(Question_Class.class);
                                        questions.add(question);
                                    }

                                    Collections.shuffle(questions);

                                    if (questions.size() > 0) {
                                        tvQuestionNumber.setText(String.valueOf(questionNumber) + "/" +
                                                String.valueOf(questions.size()));
                                        tvQuestion.setText(questions.get(0).getQuestion());
                                        rbAnswer_1.setText(questions.get(0).getAnswerA());
                                        rbAnswer_2.setText(questions.get(0).getAnswerB());
                                        rbAnswer_3.setText(questions.get(0).getAnswerC());
                                        rbAnswer_4.setText(questions.get(0).getAnswerD());
                                    } else
                                        MoveBack();
                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
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

        firebase.child("Assignment").child(assignID).child("doingTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doingTime = snapshot.getValue().toString();

                try {
                    String h="0", m="1";
                    String[] array = doingTime.split(":");
                    if (array.length >= 2) {
                        h = array[0];
                        m = array[1];
                    }
                    TimeLeft = Integer.parseInt(h) * 3600 + Integer.parseInt(m) * 60;

                    TimeCounter();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Timer error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    MoveBack();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

//        setID = "set2";
//        doingTime = "2:30";
//        assignID = "assign1";

//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm.dd/MM/yyyy");
//        String currentTime = simpleDateFormat.format(Calendar.getInstance().getTime());
//        Date dateEnd, dateNow;
//        try{
//            dateEnd = simpleDateFormat.parse(TimeEnd);
//            dateNow = simpleDateFormat.parse(currentTime);
//            TimeLeft = dateEnd.getTime() - dateNow.getTime();
//            TimeLeft /= 1000;
//        } catch (Exception e){
//            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
//            MoveBack();
//        }

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        Assignment_Question.this);
                builder.setTitle("Bạn có chắc muốn nộp bài");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CalculateGrade();
                        submit();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rgAnswer.getCheckedRadioButtonId() == -1) {
                    openWarningEmptyAnswerDialog(Gravity.CENTER);
                } else {
                    CalculateGrade();
                    moveToNext();
                }
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rgAnswer.getCheckedRadioButtonId() == -1){
                    openWarningEmptyAnswerDialog(Gravity.CENTER);
                } else {
                    CalculateGrade();
                    submit();
                }
            }
        });

        rgAnswer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                unCheckRadioButton();
                if (checkedID == R.id.radio_button_1 && rbAnswer_1.isChecked())
                    rbAnswer_1.setBackgroundResource(R.drawable.blue_border_yellow_center);
                else if (checkedID == R.id.radio_button_2 && rbAnswer_2.isChecked())
                    rbAnswer_2.setBackgroundResource(R.drawable.blue_border_yellow_center);
                else if (checkedID == R.id.radio_button_3 && rbAnswer_3.isChecked())
                    rbAnswer_3.setBackgroundResource(R.drawable.blue_border_yellow_center);
                else if (checkedID == R.id.radio_button_4 && rbAnswer_4.isChecked())
                    rbAnswer_4.setBackgroundResource(R.drawable.blue_border_yellow_center);
                lastCheckedID = checkedID;
            }
        });
    }

    private void unCheckRadioButton(){
        if (lastCheckedID == R.id.radio_button_1) {
            rbAnswer_1.setBackgroundResource(R.drawable.blue_border_rounded_cornwe);
            rbAnswer_1.setChecked(false);
        } else if (lastCheckedID == R.id.radio_button_2) {
            rbAnswer_2.setBackgroundResource(R.drawable.blue_border_rounded_cornwe);
            rbAnswer_2.setChecked(false);
        } else if (lastCheckedID == R.id.radio_button_3) {
            rbAnswer_3.setBackgroundResource(R.drawable.blue_border_rounded_cornwe);
            rbAnswer_3.setChecked(false);
        } else if (lastCheckedID == R.id.radio_button_4) {
            rbAnswer_4.setBackgroundResource(R.drawable.blue_border_rounded_cornwe);
            rbAnswer_4.setChecked(false);
        }
    }

    private void CalculateGrade(){
        if ((rbAnswer_1.isChecked() &&
                questions.get(questionNumber - 1).getRight_answer().compareTo("answerA") == 0)
                || (rbAnswer_2.isChecked() &&
                questions.get(questionNumber - 1).getRight_answer().compareTo("answerB") == 0)
                || (rbAnswer_3.isChecked() &&
                questions.get(questionNumber - 1).getRight_answer().compareTo("answerC") == 0)
                || (rbAnswer_4.isChecked() &&
                questions.get(questionNumber - 1).getRight_answer().compareTo("answerD") == 0))

            Grade += 10. / questions.size();
    }

    private void moveToNext(){
        questionNumber += 1;

        tvQuestionNumber.setText(String.valueOf(questionNumber) + "/" +
                String.valueOf(questions.size()));
        tvQuestion.setText(questions.get(questionNumber-1).getQuestion());
        rbAnswer_1.setText(questions.get(questionNumber-1).getAnswerA());
        rbAnswer_2.setText(questions.get(questionNumber-1).getAnswerB());
        rbAnswer_3.setText(questions.get(questionNumber-1).getAnswerC());
        rbAnswer_4.setText(questions.get(questionNumber-1).getAnswerD());

        if (rgAnswer.getCheckedRadioButtonId() != -1)
            rgAnswer.clearCheck();

        firebase.child("Assignment").child(assignID).child("transcript").child(userID).setValue(
                String.valueOf((double) Math.ceil(Grade*10) / 10));

        if (questionNumber != questions.size()) {
            btSubmit.setVisibility(View.GONE);
            btNext.setVisibility(View.VISIBLE);
        } else {
            btSubmit.setVisibility(View.VISIBLE);
            btNext.setVisibility(View.GONE);
        }
    }

    private void submit(){
        firebase.child("Assignment").child(assignID).child("transcript").child(userID).setValue(
                String.valueOf((double) Math.ceil(Grade*10) / 10));

        firebase.child("Assignment").child(assignID).child("participant").child(userID).
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        attemp = snapshot.getValue().toString();
                        attemp = String.valueOf(Integer.parseInt(attemp) + 1);
                        firebase.child("Assignment").child(assignID).child("participant").
                                child(userID).setValue(attemp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        openAfterSubmitDialog();
    }

    private void TimeCounter(){
        Timer T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        tvTimeLeft.setText(String.valueOf(TimeLeft/60) + ":" +
                                String.valueOf(TimeLeft%60));
                        TimeLeft--;
                        if (TimeLeft <= 60) {
                            tvTimeLeft.setTextColor(getResources().getColor(R.color.red));
                            if (TimeLeft < 0)
                                submit();
                        }
                    }
                });
            }
        }, 1000, 1000);
    }

    private void openWarningEmptyAnswerDialog(int gravity){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.warning_empty_answer_dialog);

        Window window = dialog.getWindow();
        if (window == null)
            MoveBack();

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAtribute = window.getAttributes();
        windowAtribute.gravity = gravity;
        window.setAttributes(windowAtribute);

        if (Gravity.BOTTOM == gravity)
            dialog.setCancelable(true);
        else
            dialog.setCancelable(false);

        Button btCancel = dialog.findViewById(R.id.bt_cancel_warning_dialog);
        Button btContinue = dialog.findViewById(R.id.bt_continue_warning_dialog);

        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (questionNumber != questions.size())
                    moveToNext();
                else
                    submit();
            }
        });

        dialog.show();
    }

    private void openAfterSubmitDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.after_submit_dialog);

        Window window = dialog.getWindow();
        if (window == null)
            MoveBack();

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvGrade = (TextView) dialog.findViewById(R.id.tv_grade);
        Button btOk = (Button) dialog.findViewById(R.id.bt_OK);

        tvGrade.setText(String.valueOf((double) Math.ceil(Grade*10) / 10));

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoveBack();
            }
        });

        dialog.show();
    }

    private void MoveBack(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        SharedPreferences preferences = getSharedPreferences("UserID",MODE_PRIVATE);
        String userID = preferences.getString("userID","Unknown");
        intent.putExtra("userID",userID);
        intent.putExtra("itemFrag",2);
        startActivity(intent);
        finish();
    }
}