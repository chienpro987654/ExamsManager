package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Objects;

public class Detail_Item_Library_Activity extends AppCompatActivity {

    TextView tv_create, tv_date_create, tv_grade, tv_quantity, tv_hash, tv_copy;
    EditText note_input;
    Button Btn_Edit, Btn_Delete, Btn_Add;

    String setID;

    DatabaseReference ref_set = FirebaseDatabase.getInstance().getReference("QuestionSet");
    DatabaseReference ref_hash = FirebaseDatabase.getInstance().getReference("Hash");
    DatabaseReference ref_ques;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item_library);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        tv_create = findViewById(R.id.tv_creator_detail);
        tv_date_create = findViewById(R.id.tv_date_create_detail);
        tv_grade = findViewById(R.id.tv_grade_detail);
        tv_quantity = findViewById(R.id.tv_quantity_detail);
        tv_hash = findViewById(R.id.tv_hash_detail);
        tv_copy = findViewById(R.id.tv_copy_detail);
        note_input = findViewById(R.id.note_input_detail);
        Btn_Edit = findViewById(R.id.Btn_Edit_Detail);
        Btn_Delete = findViewById(R.id.Btn_Delete_Detail);
        Btn_Add = findViewById(R.id.Btn_Add_Detail);

        ShowData();

        HaveQuestion();

        tv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp[] = tv_hash.getText().toString().split(": ");
                String hash = tmp[1];
                ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copy text",hash);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Đã sao chép vào clipboard, Mã: "+hash, Toast.LENGTH_SHORT).show();
            }
        });

        Btn_Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Detail_Item_Library_Activity.this,EditQuestionActivity.class);
                intent.putExtra("setID",setID);
                startActivity(intent);
            }
        });

        Btn_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Detail_Item_Library_Activity.this);
                builder.setTitle("Bạn có chắc chắn muốn xóa? ")
                        .setMessage("Thay đổi này không thể phục hồi")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {

                            // do something when the button is clicked
                            public void onClick(DialogInterface arg0, int arg1) {

                                ref_hash.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String[] tmp = tv_hash.getText().toString().split(": ");
                                        String hash = tmp[1];
                                        ref_hash.child(hash).removeValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                                ref_set.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ref_set.child(setID).removeValue();
                                        finish();
                                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                        SharedPreferences preferences = getSharedPreferences("UserID",MODE_PRIVATE);
                                        String userID = preferences.getString("userID","Unknown");
                                        intent.putExtra("userID",userID);
                                        intent.putExtra("itemFrag",1);
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
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
        });

        Intent intent = getIntent();
        setID = intent.getStringExtra("setID");

        ref_ques = FirebaseDatabase.getInstance().getReference("QuestionSet").child(setID).child("ques");

        Btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateQues();
            }
        });
    }

    private void CreateQues()
    {
        ArrayList<String> listID = new ArrayList<>();
        ref_ques.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ss: snapshot.getChildren()){
                    String id = String.valueOf(ss.child("quesID").getValue());
                    listID.add(id);
                }

                boolean isCreated = false;
                long counter=1;
                while(!isCreated)
                {
                    String id ="ques"+counter;
                    if (listID.contains(id))
                        counter++;
                    else isCreated=true;
                }
                String quesID ="ques"+counter;

                //Go to CreateQuestion activity
                Intent intent = new Intent(getApplicationContext(),CreateQuestion.class);
                intent.putExtra("next_quesID",quesID);
                intent.putExtra("setID",setID);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                SharedPreferences preferences = getSharedPreferences("UserID",MODE_PRIVATE);
                String userID = preferences.getString("userID","Unknown");

                Intent intent = new Intent(Detail_Item_Library_Activity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("userID",userID);
                intent.putExtra("itemFrag",1);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ShowData() {
        ref_set.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String creator = Objects.requireNonNull(snapshot.child(setID).child("set_creator").getValue()).toString();
                String date_create = Objects.requireNonNull(snapshot.child(setID).child("date_create").getValue()).toString();
                String grade = Objects.requireNonNull(snapshot.child(setID).child("grade").getValue()).toString();
                String quantity = Objects.requireNonNull(snapshot.child(setID).child("quantity").getValue()).toString();
                String note = Objects.requireNonNull(snapshot.child(setID).child("note").getValue()).toString();

                String name = Objects.requireNonNull(snapshot.child(setID).child("name").getValue()).toString();
                Objects.requireNonNull(getSupportActionBar()).setTitle(name);

                tv_create.append(creator);
                tv_date_create.append(date_create);
                tv_grade.append(grade);
                tv_quantity.append(quantity);
                note_input.setText(note);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref_hash.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tmp;
                for(DataSnapshot ss:snapshot.getChildren()) {
                    tmp = Objects.requireNonNull(ss.getValue()).toString();
                    if (tmp.equals(setID)) {
                        tv_hash.append(ss.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void HaveQuestion()
    {
        ref_set.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int quantity = Integer.parseInt(Objects.requireNonNull(snapshot.child(setID).child("quantity").getValue()).toString());
                if (quantity>0) Btn_Edit.setEnabled(true);
                else Btn_Edit.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            TapToFinish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void TapToFinish() {
        ref_set.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String note = Objects.requireNonNull(snapshot.child(setID).child("note").getValue()).toString();
                if (note_input.getText().toString().equals(note))
                {
                    finish();
                }
                else
                {
                    AlertDialog alertBox = new AlertDialog.Builder(Detail_Item_Library_Activity.this)
                            .setMessage("Bạn có muốn lưu ghi chú?")
                            .setTitle("Kết thúc")
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                // do something when the button is clicked
                                public void onClick(DialogInterface arg0, int arg1) {
                                    //Save note to fb
                                    ref_set.child(setID).child("note").setValue(note_input.getText().toString());
                                    finish();
                                }
                            })
                            .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    finish();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}