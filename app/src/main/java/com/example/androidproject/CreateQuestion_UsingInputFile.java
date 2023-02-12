package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.internal.TextWatcherAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.w3c.dom.Text;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

public class CreateQuestion_UsingInputFile extends AppCompatActivity {

    String setID, PathOfSelectedFile, NumberOfQues = "1";
    int QuesNumber = 1;

    TextView tvDone, tvQuesCounter, tvRightAnswer, tvQues;
    RadioButton rbA, rbB, rbC, rbD;
    Button btNext, btPrevious, btDelete, btAdd;

    ArrayList<Question_Class> questions;

    DatabaseReference ref_set = FirebaseDatabase.getInstance().getReference("QuestionSet");
    DatabaseReference ref_hash = FirebaseDatabase.getInstance().getReference("Hash");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question_using_input_file);

        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");

        tvDone = (TextView) findViewById(R.id.tv_Done);
        tvQuesCounter = (TextView) findViewById(R.id.tv_question_counter);
        tvRightAnswer = (TextView) findViewById(R.id.tv_right_answer);
        tvQues = (TextView) findViewById(R.id.tv_question);
        rbA = (RadioButton) findViewById(R.id.radio_button_1);
        rbB = (RadioButton) findViewById(R.id.radio_button_2);
        rbC = (RadioButton) findViewById(R.id.radio_button_3);
        rbD = (RadioButton) findViewById(R.id.radio_button_4);
        btNext = (Button) findViewById(R.id.Btn_Next_Question);
        btPrevious = (Button) findViewById(R.id.Btn_Previous_Question);
        btDelete = (Button) findViewById(R.id.Btn_Delete_Question);
        btAdd = (Button) findViewById(R.id.Btn_Add_Question);

        Intent intent = getIntent();
        setID = intent.getStringExtra("setID");
        PathOfSelectedFile = intent.getStringExtra("Path_Selectedfile");

        String ques = "";
        String quesID = "";
        String A, B, C, D;
        A = B = C = D = "";
        String answer = "answerA";

        questions = new ArrayList<>();

        try {
            String path = getApplicationContext().getFilesDir().getAbsolutePath();
            path = path + PathOfSelectedFile;
            InputStream is = new FileInputStream(PathOfSelectedFile);
            XWPFDocument doc = new XWPFDocument(is);
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                String bullet = paragraph.getNumFmt(); //get bullet
                String temp = paragraph.getText(); //get text in paragraph

                if (bullet == null) { //don't have bullet
                    String head = "";
                    if (temp.contains(". "))
                        head = temp.substring(0, temp.indexOf("."));
                    try {
                        Integer.parseInt(head);
                        QuesNumber++;
                        if (ques.compareTo("") != 0) {
                            Question_Class question = new Question_Class(
                                    quesID, ques, A, B, C, D, answer);
                            questions.add(question);
                            ques = A = B = C = D = "";
                            answer = "answerA";
                        }
                        ques = temp;
                    } catch (NumberFormatException e) {
                        if (temp.startsWith("a. ") || temp.startsWith("A. ") || temp.startsWith("a) ")
                                || temp.startsWith("A) ")) {
                            A = temp;
                            for (XWPFRun run : paragraph.getRuns())
                                if (run.isBold()) {
                                    answer = "answerA";
                                    break;
                                }
                        } else if (temp.startsWith("b. ") || temp.startsWith("B. ") ||
                                temp.startsWith("b) ") || temp.startsWith("B) ")) {
                            B = temp;
                            for (XWPFRun run : paragraph.getRuns())
                                if (run.isBold()) {
                                    answer = "answerB";
                                    break;
                                }
                        } else if (temp.startsWith("c. ") || temp.startsWith("C. ") ||
                                temp.startsWith("c) ") || temp.startsWith("C) ")) {
                            C = temp;
                            for (XWPFRun run : paragraph.getRuns())
                                if (run.isBold()) {
                                    answer = "answerC";
                                    break;
                                }
                        } else if (temp.startsWith("d. ") || temp.startsWith("D. ") ||
                                temp.startsWith("d) ") || temp.startsWith("D) ")) {
                            D = temp;
                            for (XWPFRun run : paragraph.getRuns())
                                if (run.isBold()) {
                                    answer = "answerD";
                                    break;
                                }
                        } else
                            ques += "\n" + temp;
                    }
                } else { //have bullet
                    if (bullet.contains("decimal")) { //bullet format is number
                        QuesNumber++;
                        if (ques.compareTo("") != 0) {
                            Question_Class question = new Question_Class(
                                    quesID, ques, A, B, C, D, answer);
                            questions.add(question);
                            ques = A = B = C = D = "";
                            answer = "answerA";
                        }
                        ques = temp;
                    } else { //bullet format is letter
                        if (A.compareTo("") == 0) {
                            A = "A. " + temp;
                            for (XWPFRun run : paragraph.getRuns())
                                if (run.isBold()) {
                                    answer = "answerA";
                                    break;
                                }
                        } else if (B.compareTo("") == 0) {
                            B = "B. " + temp;
                            for (XWPFRun run : paragraph.getRuns())
                                if (run.isBold()) {
                                    answer = "answerB";
                                    break;
                                }
                        } else if (C.compareTo("") == 0) {
                            C = "C. " + temp;
                            for (XWPFRun run : paragraph.getRuns())
                                if (run.isBold()) {
                                    answer = "answerC";
                                    break;
                                }
                        } else if (D.compareTo("") == 0) {
                            D = "D. " + temp;
                            for (XWPFRun run : paragraph.getRuns())
                                if (run.isBold()) {
                                    answer = "answerD";
                                    break;
                                }
                        }
                    }
                }
            }

            Question_Class question = new Question_Class(
                    quesID, ques, A, B, C, D, answer);
            questions.add(question);

            if (QuesNumber > 1)
                NumberOfQues = String.valueOf(questions.size());
            QuesNumber = 1;

            tvQuesCounter.setText(String.valueOf(QuesNumber) + "/" + NumberOfQues);
            tvQues.setText(questions.get(0).getQuestion());
            rbA.setText(questions.get(0).getAnswerA());
            rbB.setText(questions.get(0).getAnswerB());
            rbC.setText(questions.get(0).getAnswerC());
            rbD.setText(questions.get(0).getAnswerD());
            tvRightAnswer.setText("Câu trả lời đúng: Đáp Án " +
                    questions.get(0).getRight_answer().charAt(6));

            if (Integer.parseInt(NumberOfQues) <= 1) {
                btNext.setVisibility(View.GONE);
                btAdd.setVisibility(View.VISIBLE);
                btDelete.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Không hỗ trợ định dạng file đã chọn, vui lòng chọn file .doc hoặc .docx", Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();

            btAdd.setVisibility(View.VISIBLE);
            btNext.setVisibility(View.GONE);
            btDelete.setVisibility(View.GONE);
            questions.add(new Question_Class("", String.valueOf(QuesNumber) + ". none",
                    "A. ", "B. ", "C. ", "D. ", "answerA"));
            tvQuesCounter.setText(String.valueOf(QuesNumber) + "/" + NumberOfQues);
            tvQues.setText(String.valueOf(QuesNumber) + ". none");
            rbA.setText("A. ");
            rbB.setText("B. ");
            rbC.setText("C. ");
            rbD.setText("D. ");
            tvRightAnswer.setText("Câu trả lời đúng: Đáp Án A");
        }

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvQuesCounter.setText(String.valueOf(QuesNumber+1) + "/" + NumberOfQues);
                tvQues.setText(questions.get(QuesNumber).getQuestion());
                rbA.setText(questions.get(QuesNumber).getAnswerA());
                rbB.setText(questions.get(QuesNumber).getAnswerB());
                rbC.setText(questions.get(QuesNumber).getAnswerC());
                rbD.setText(questions.get(QuesNumber).getAnswerD());
                tvRightAnswer.setText("Câu trả lời đúng: Đáp Án " +
                        questions.get(QuesNumber).getRight_answer().charAt(6));
                QuesNumber++;
                if (QuesNumber == Integer.parseInt(NumberOfQues)) {
                    btNext.setVisibility(View.INVISIBLE);
                    btAdd.setVisibility(View.VISIBLE);
                }
                if (btPrevious.getVisibility() == View.GONE)
                    btPrevious.setVisibility(View.VISIBLE);
            }
        });

        btPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuesNumber--;
                if (QuesNumber == 1)
                    btPrevious.setVisibility(View.GONE);
                if (btAdd.getVisibility() == View.VISIBLE) {
                    btAdd.setVisibility(View.GONE);
                    btNext.setVisibility(View.VISIBLE);
                }
                tvQuesCounter.setText(String.valueOf(QuesNumber) + "/" + NumberOfQues);
                tvQues.setText(questions.get(QuesNumber-1).getQuestion());
                rbA.setText(questions.get(QuesNumber-1).getAnswerA());
                rbB.setText(questions.get(QuesNumber-1).getAnswerB());
                rbC.setText(questions.get(QuesNumber-1).getAnswerC());
                rbD.setText(questions.get(QuesNumber-1).getAnswerD());
                tvRightAnswer.setText("Câu trả lời đúng: Đáp Án " +
                        questions.get(QuesNumber-1).getRight_answer().charAt(6));
            }
        });

        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CreateQuestion_UsingInputFile.this);
                builder.setTitle("Bạn có chắc muốn xóa câu hỏi này?");

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (QuesNumber > 1 && QuesNumber == Integer.parseInt(NumberOfQues)) {
                            questions.remove(questions.get(QuesNumber-1));
                            QuesNumber--;
                            NumberOfQues = String.valueOf(QuesNumber);
                            if (QuesNumber == 1) {
                                btPrevious.setVisibility(View.GONE);
                                btDelete.setVisibility(View.GONE);
                                btNext.setVisibility(View.GONE);
                                btAdd.setVisibility(View.VISIBLE);
                            }
                        } else if (QuesNumber >= 1) {
                            questions.remove(questions.get(QuesNumber-1));
                            NumberOfQues = String.valueOf(Integer.parseInt(
                                    NumberOfQues) - 1);
                            if (QuesNumber == Integer.parseInt(NumberOfQues)) {
                                if (QuesNumber != 1) {
                                    btNext.setVisibility(View.GONE);
                                    btAdd.setVisibility(View.VISIBLE);
                                } else {
                                    btPrevious.setVisibility(View.GONE);
                                    btDelete.setVisibility(View.GONE);
                                    btNext.setVisibility(View.GONE);
                                    btAdd.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        tvQuesCounter.setText(String.valueOf(QuesNumber) + "/" + NumberOfQues);
                        tvQues.setText(questions.get(QuesNumber - 1).getQuestion());
                        rbA.setText(questions.get(QuesNumber - 1).getAnswerA());
                        rbB.setText(questions.get(QuesNumber - 1).getAnswerB());
                        rbC.setText(questions.get(QuesNumber - 1).getAnswerC());
                        rbD.setText(questions.get(QuesNumber - 1).getAnswerD());
                        tvRightAnswer.setText("Câu trả lời đúng: Đáp Án " +
                                questions.get(QuesNumber-1).getRight_answer().charAt(6));
                        Toast.makeText(getApplicationContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
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

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QuesNumber++;
                questions.add(new Question_Class("", String.valueOf(QuesNumber) + ". none",
                        "A. ", "B. ", "C. ", "D. ", "answerA"));
                NumberOfQues = String.valueOf(QuesNumber);
                tvQuesCounter.setText(String.valueOf(QuesNumber) + "/" + NumberOfQues);
                tvQues.setText(String.valueOf(QuesNumber) + ". none");
                rbA.setText("A. ");
                rbB.setText("B. ");
                rbC.setText("C. ");
                rbD.setText("D. ");
                tvRightAnswer.setText("Câu trả lời đúng: Đáp Án A");
                Toast.makeText(getApplicationContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();

                if (btPrevious.getVisibility() == View.GONE) {
                    btPrevious.setVisibility(View.VISIBLE);
                    btDelete.setVisibility(View.VISIBLE);
                }
            }
        });

        tvQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CreateQuestion_UsingInputFile.this);
                builder.setTitle("Nhập câu hỏi");

                final EditText input = new EditText(CreateQuestion_UsingInputFile.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(questions.get(QuesNumber-1).getQuestion());
                input.setSingleLine(false);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tvQues.setText(input.getText().toString());
                        questions.get(QuesNumber-1).setQuestion(input.getText().toString());
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

        rbA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CreateQuestion_UsingInputFile.this);
                builder.setTitle("Nhập đáp án");

                final EditText input = new EditText(CreateQuestion_UsingInputFile.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(questions.get(QuesNumber-1).getAnswerA());
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().startsWith("a. ") ||
                                input.getText().toString().startsWith("A. ") ||
                                input.getText().toString().startsWith("a) ") ||
                                input.getText().toString().startsWith("A) ")) {
                            rbA.setText(input.getText().toString());
                            questions.get(QuesNumber - 1).setAnswerA(input.getText().toString());
                        } else {
                            rbA.setText("A. " + input.getText().toString());
                            questions.get(QuesNumber - 1).setAnswerA("A. " + input.getText().toString());
                        }
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

        rbB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CreateQuestion_UsingInputFile.this);
                builder.setTitle("Nhập đáp án");

                final EditText input = new EditText(CreateQuestion_UsingInputFile.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(questions.get(QuesNumber-1).getAnswerB());
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().startsWith("b. ") ||
                                input.getText().toString().startsWith("B. ") ||
                                input.getText().toString().startsWith("b) ") ||
                                input.getText().toString().startsWith("B) ")) {
                            rbB.setText(input.getText().toString());
                            questions.get(QuesNumber - 1).setAnswerB(input.getText().toString());
                        } else {
                            rbB.setText("B. " + input.getText().toString());
                            questions.get(QuesNumber - 1).setAnswerB("B. " + input.getText().toString());
                        }
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

        rbC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CreateQuestion_UsingInputFile.this);
                builder.setTitle("Nhập đáp án");

                final EditText input = new EditText(CreateQuestion_UsingInputFile.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(questions.get(QuesNumber-1).getAnswerC());
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().startsWith("c. ") ||
                                input.getText().toString().startsWith("C. ") ||
                                input.getText().toString().startsWith("c) ") ||
                                input.getText().toString().startsWith("C) ")) {
                            rbC.setText(input.getText().toString());
                            questions.get(QuesNumber - 1).setAnswerC(input.getText().toString());
                        } else {
                            rbC.setText("C. " + input.getText().toString());
                            questions.get(QuesNumber - 1).setAnswerC("C. " + input.getText().toString());
                        }
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

        rbD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CreateQuestion_UsingInputFile.this);
                builder.setTitle("Nhập đáp án");

                final EditText input = new EditText(CreateQuestion_UsingInputFile.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(questions.get(QuesNumber-1).getAnswerD());
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().startsWith("d. ") ||
                                input.getText().toString().startsWith("D. ") ||
                                input.getText().toString().startsWith("d) ") ||
                                input.getText().toString().startsWith("D) ")) {
                            rbD.setText(input.getText().toString());
                            questions.get(QuesNumber - 1).setAnswerD(input.getText().toString());
                        } else {
                            rbD.setText("D. " + input.getText().toString());
                            questions.get(QuesNumber - 1).setAnswerD("D. " + input.getText().toString());
                        }
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

        rbA.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (tvRightAnswer.getText().toString().compareTo("Câu trả lời đúng: Đáp Án A") != 0) {
                    questions.get(QuesNumber - 1).setRight_answer("answerA");
                    tvRightAnswer.setText("Câu trả lời đúng: Đáp Án A");
                    Toast.makeText(getApplicationContext(), "Right answer is changed to A", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        rbB.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (tvRightAnswer.getText().toString().compareTo("Câu trả lời đúng: Đáp Án B") != 0) {
                    questions.get(QuesNumber - 1).setRight_answer("answerB");
                    tvRightAnswer.setText("Câu trả lời đúng: Đáp Án B");
                    Toast.makeText(getApplicationContext(), "Right answer is changed to B", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        rbC.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (tvRightAnswer.getText().toString().compareTo("Câu trả lời đúng: Đáp Án C") != 0) {
                    questions.get(QuesNumber - 1).setRight_answer("answerC");
                    tvRightAnswer.setText("Câu trả lời đúng: Đáp Án C");
                    Toast.makeText(getApplicationContext(), "Right answer is changed to C", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        rbD.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (tvRightAnswer.getText().toString().compareTo("Câu trả lời đúng: Đáp Án D") != 0) {
                    questions.get(QuesNumber - 1).setRight_answer("answerD");
                    tvRightAnswer.setText("Câu trả lời đúng: Đáp Án D");
                    Toast.makeText(getApplicationContext(), "Right answer is changed to D", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        tvDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        CreateQuestion_UsingInputFile.this);
                builder.setTitle("Bạn có muốn lưu kết quả trước khi thoát");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        QuesNumber = 1;
                        for (Question_Class q: questions) {
                            q.setQuesID("ques" + QuesNumber);
                            QuesNumber++;

                            ref_set.child(setID).child("ques").child(q.getQuesID()).setValue(q);
                        }

                        ref_set.child(setID).child("quantity").setValue(Integer.parseInt(NumberOfQues));

                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        SharedPreferences preferences = getSharedPreferences("UserID",MODE_PRIVATE);
                        String userID = preferences.getString("userID","Unknown");
                        intent.putExtra("userID",userID);
                        intent.putExtra("itemFrag",1);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ref_set.child(setID).removeValue();
                        ref_hash.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String tmp;
                                for(DataSnapshot ss:snapshot.getChildren()) {
                                    tmp = Objects.requireNonNull(ss.getValue()).toString();
                                    if (tmp.equals(setID)) {
                                        String temp = ss.toString();
                                        ref_hash.child(temp.substring(temp.indexOf("=") + 2,
                                                temp.indexOf(","))).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });

                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        SharedPreferences preferences = getSharedPreferences("UserID",MODE_PRIVATE);
                        String userID = preferences.getString("userID","Unknown");
                        intent.putExtra("userID",userID);
                        intent.putExtra("itemFrag",1);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.cancel(); }
                });

                builder.show();
            }
        });
    }
}