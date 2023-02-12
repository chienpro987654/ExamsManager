package com.example.androidproject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Fragment_Edit_Question extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";

    private String setID;
    private String quesID;
    private int position;

    public Fragment_Edit_Question() {
        // Required empty public constructor
    }

    public static Fragment_Edit_Question newInstance(String setID, String quesID, int position) {
        Fragment_Edit_Question fragment = new Fragment_Edit_Question();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, setID);
        args.putString(ARG_PARAM2, quesID);
        args.putInt(ARG_PARAM3,position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            setID = getArguments().getString(ARG_PARAM1);
            quesID = getArguments().getString(ARG_PARAM2);
        }
    }

    DatabaseReference ref_set = FirebaseDatabase.getInstance().getReference("QuestionSet");
    DatabaseReference ref_ques;

    TextView tv_counter,tv_question, tv_right_answer, tv_delete;
    RadioGroup radioGroup;
    RadioButton Btn_A, Btn_B,Btn_C,Btn_D;
    Button Btn_Save;

    EditQuestionActivity activity;

    String right_answer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment__edit__question, container, false);

        activity = (EditQuestionActivity) getActivity();


        if (getArguments() != null) {
            setID = getArguments().getString(ARG_PARAM1);
            quesID = getArguments().getString(ARG_PARAM2);
            position = getArguments().getInt(ARG_PARAM3);
        }
        ref_ques = FirebaseDatabase.getInstance().getReference("QuestionSet").child(setID).child("ques");

        tv_counter = view.findViewById(R.id.tv_question_counter_edit_question);
        tv_question = view.findViewById(R.id.tv_question_edit_question);
        tv_right_answer = view.findViewById(R.id.tv_right_answer_edit_question);
        tv_delete = view.findViewById(R.id.tv_delete_edit_question);

        radioGroup = view.findViewById(R.id.radioGroup_answer_edit_question);
        Btn_A = view.findViewById(R.id.radio_button_1_edit_question);
        Btn_B = view.findViewById(R.id.radio_button_2_edit_question);
        Btn_C = view.findViewById(R.id.radio_button_3_edit_question);
        Btn_D = view.findViewById(R.id.radio_button_4_edit_question);
        Btn_Save = view.findViewById(R.id.Btn_Save_Edit_Question);

        ShowData();

        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("Bạn có chắc chắn muốn xóa? ")
                        .setMessage("Thay đổi này không thể phục hồi")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ref_set.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int quantity = Integer.parseInt(Objects.requireNonNull(snapshot.child(setID).child("quantity").getValue()).toString());
                                        quantity = quantity-1;
                                        ref_set.child(setID).child("quantity").setValue(quantity);
                                        ref_ques.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                ref_ques.child(quesID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(activity.getApplicationContext(),
                                                                "Xóa thành công, đang làm mới bộ đệm",
                                                                Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(activity.getApplicationContext(),EditQuestionActivity.class);
                                                        intent.putExtra("setID",setID);
                                                        startActivity(intent);
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
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                arg0.cancel();
                            }
                        })
                        .show();
            }
        });

        assert activity != null;
        ClickAnswer(Btn_A, activity.getApplicationContext(),"A. ");
        ClickAnswer(Btn_B, activity.getApplicationContext(),"B. ");
        ClickAnswer(Btn_C, activity.getApplicationContext(),"C. ");
        ClickAnswer(Btn_D, activity.getApplicationContext(),"D. ");

        ClickRightAnswer(Btn_A);
        ClickRightAnswer(Btn_B);
        ClickRightAnswer(Btn_C);
        ClickRightAnswer(Btn_D);

        Btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_input())
                {
                    CheckRightAnswer();
                    String question = tv_question.getText().toString();
                    String answerA = Btn_A.getText().toString();
                    String answerB = Btn_B.getText().toString();
                    String answerC = Btn_C.getText().toString();
                    String answerD = Btn_D.getText().toString();
                    Question_Class question_class = new Question_Class(quesID,question,answerA,
                            answerB,answerC,answerD, right_answer);
                    ref_set.child(setID).child("ques").child(quesID).setValue(question_class).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(activity.getApplicationContext(), "Cập nhật câu hỏi thành công", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return view;
    }

    private void ShowData() {
        ref_set.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String quantity = Objects.requireNonNull(snapshot.child(setID).child("quantity").getValue()).toString();
                ref_ques.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String counter = position+"/"+quantity;
                        String question = Objects.requireNonNull(snapshot.child(quesID).child("question").getValue()).toString();
                        String answerA = Objects.requireNonNull(snapshot.child(quesID).child("answerA").getValue()).toString();
                        String answerB = Objects.requireNonNull(snapshot.child(quesID).child("answerB").getValue()).toString();
                        String answerC = Objects.requireNonNull(snapshot.child(quesID).child("answerC").getValue()).toString();
                        String answerD = Objects.requireNonNull(snapshot.child(quesID).child("answerD").getValue()).toString();
                        String right_answer = Objects.requireNonNull(snapshot.child(quesID).child("right_answer").getValue()).toString();

                        String temp;
                        if (right_answer.equals("answerA"))
                            temp = "Đáp án A";
                        else if (right_answer.equals("answerB"))
                            temp = "Đáp án B";
                        else if (right_answer.equals("answerC"))
                            temp = "Đáp án C";
                        else temp = "Đáp án D";

                        tv_counter.setText(counter);
                        tv_question.setText(question);
                        Btn_A.setText(answerA);
                        Btn_B.setText(answerB);
                        Btn_C.setText(answerC);
                        Btn_D.setText(answerD);
                        tv_right_answer.append(temp);
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

    private void ClickAnswer(Button button, Context context, String suffix) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
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
                        button.setText(suffix+input.getText().toString());
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
            Toast.makeText(activity.getApplicationContext(), "Các đáp án không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }

        String suffix = "Câu trả lời đúng: ";
        long suffix_length = suffix.length();
        long right_answer_length = tv_right_answer.getText().toString().length();

        if (right_answer_length==suffix_length)
        {
            Toast.makeText(activity.getApplicationContext(), "Chưa chọn đáp án đúng", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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
}