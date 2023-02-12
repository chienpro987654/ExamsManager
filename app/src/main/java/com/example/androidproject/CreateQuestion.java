package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateQuestion extends AppCompatActivity {

    TextView tv_counter,tv_question, tv_right_answer,tv_finish;
    RadioGroup radioGroup;
    RadioButton Btn_A, Btn_B,Btn_C,Btn_D;
    Button Btn_Next;

    DatabaseReference ref_set = FirebaseDatabase.getInstance().getReference("QuestionSet");
    String setID;
    int next_quesID;
    String right_answer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question);

        tv_question = findViewById(R.id.tv_question);
        tv_counter = findViewById(R.id.tv_question_counter);
        tv_right_answer = findViewById(R.id.tv_right_answer);
        radioGroup = findViewById(R.id.radioGroup_answer);
        Btn_A = findViewById(R.id.radio_button_1);
        Btn_B = findViewById(R.id.radio_button_2);
        Btn_C = findViewById(R.id.radio_button_3);
        Btn_D = findViewById(R.id.radio_button_4);
        Btn_Next = findViewById(R.id.Btn_Next_Question);
        tv_finish = findViewById(R.id.tv_finish_question);

        Intent intent = getIntent();
        setID = intent.getStringExtra("setID");
        next_quesID = intent.getIntExtra("next_quesID",1);

        Btn_A.setText("A. ");
        Btn_B.setText("B. ");
        Btn_C.setText("C. ");
        Btn_D.setText("D. ");
        tv_counter.setText(next_quesID+"/"+next_quesID);

        ClickAnswer(Btn_A, CreateQuestion.this);
        ClickAnswer(Btn_B, CreateQuestion.this);
        ClickAnswer(Btn_C, CreateQuestion.this);
        ClickAnswer(Btn_D, CreateQuestion.this);

        ClickRightAnswer(Btn_A);
        ClickRightAnswer(Btn_B);
        ClickRightAnswer(Btn_C);
        ClickRightAnswer(Btn_D);

        tv_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TapToFinish();
            }
        });
        Btn_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_input())
                {
                    CheckRightAnswer();
                    String quesID = "ques"+ next_quesID;
                    String question = tv_question.getText().toString();
                    String answerA = Btn_A.getText().toString();
                    String answerB = Btn_B.getText().toString();
                    String answerC = Btn_C.getText().toString();
                    String answerD = Btn_D.getText().toString();
                    Question_Class question_class = new Question_Class(quesID,question,answerA,
                            answerB,answerC,answerD, right_answer);
                    ref_set.child(setID).child("quantity").setValue(next_quesID);
                    ref_set.child(setID).child("ques").child(quesID).setValue(question_class).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Thêm câu hỏi thành công", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(getApplicationContext(),CreateQuestion.class);
                            int tmp = next_quesID+1;
                            intent1.putExtra("next_quesID",tmp);
                            intent1.putExtra("setID",setID);
                            startActivity(intent1);
                            finish();
                        }
                    });

                }
            }
        });
    }

    void CheckRightAnswer()
    {
        if (tv_right_answer.getText().toString().contains("Đáp Án A"))
        {
            right_answer="answerA";
        }

        if (tv_right_answer.getText().toString().contains("Đáp Án B"))
        {
            right_answer="answerB";
        }

        if (tv_right_answer.getText().toString().contains("Đáp Án C"))
        {
            right_answer="answerC";
        }

        if (tv_right_answer.getText().toString().contains("Đáp Án D"))
        {
            right_answer="answerD";
        }
    }

    private void ClickRightAnswer(RadioButton button) {
        final String[] result = new String[1];
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (button.getText().toString().contains("A. "))
                {
                    result[0] ="answerA";
                    tv_right_answer.setText("Câu trả lời đúng: Đáp Án A");
                }
                else if (button.getText().toString().contains("B. "))
                {
                    result[0] ="answerB";
                    tv_right_answer.setText("Câu trả lời đúng: Đáp Án B");
                }

                else if (button.getText().toString().contains("C. "))
                {
                    result[0] ="answerC";
                    tv_right_answer.setText("Câu trả lời đúng: Đáp Án C");
                }

                else
                {
                    result[0] ="answerD";
                    tv_right_answer.setText("Câu trả lời đúng: Đáp Án D");
                }
                return true;
            }
        });
    }

    private void ClickAnswer(Button button, Context context) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Nhập đáp án");

                // Set up the input
                final EditText input = new EditText(context);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        button.append(input.getText().toString());
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
        });
    }

    boolean check_input()
    {
        String question = tv_question.getText().toString();
        long length_answer = 3;
        if (question.equals(""))
        {
            tv_question.setError(getString(R.string.edit_text_error_empty));
            tv_question.requestFocus();
            return false;
        }

        long length_A = Btn_A.getText().toString().length();
        long length_B = Btn_B.getText().toString().length();
        long length_C = Btn_C.getText().toString().length();
        long length_D = Btn_D.getText().toString().length();

        if (length_A<=length_answer || length_B<=length_answer
                ||length_C<=length_answer ||length_D<=length_answer)
        {
            Toast.makeText(getApplicationContext(), "Các đáp án không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        String suffix = "Câu trả lời đúng: ";
        long suffix_length = suffix.length();
        long right_answer_length = tv_right_answer.getText().toString().length();

        if (right_answer_length==suffix_length)
        {
            Toast.makeText(getApplicationContext(), "Chưa chọn đáp án đúng", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            TapToFinish();

            //moveTaskToBack(false);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void TapToFinish() {

        AlertDialog alertBox = new AlertDialog.Builder(this)
                .setMessage("Bạn có muốn kết thúc? Câu hỏi chưa được tạo sẽ không được lưu")
                .setTitle("Kết thúc")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {

                        //refresh MainActivity
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        SharedPreferences preferences = getSharedPreferences("UserID",MODE_PRIVATE);
                        String userID = preferences.getString("userID","Unknown");
                        intent.putExtra("userID",userID);
                        intent.putExtra("itemFrag",1);
                        startActivity(intent);
                        finish();
                        //close();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }
}