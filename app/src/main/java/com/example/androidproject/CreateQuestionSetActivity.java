package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class CreateQuestionSetActivity extends AppCompatActivity {

    Button Btn_Confirm;
    TextView name_input, creator_input,grade_input, date_create_input, tvFile, tvPath_of_file;
    RadioGroup radioGroup;
    RadioButton rbCreateQuesManually, rbUsingWordInput;
    private BroadcastReceiver mReceiver;
    String userID;
    String setID;
    String selectedfile;

    DatabaseReference ref_user = FirebaseDatabase.getInstance().getReference("User");
    DatabaseReference ref_set = FirebaseDatabase.getInstance().getReference("QuestionSet");
    DatabaseReference ref_hash = FirebaseDatabase.getInstance().getReference("Hash");

    private static final int MY_REQUEST_CODE_PERMISSION = 1000;
    private static final int MY_RESULT_CODE_FILECHOOSER = 2000;
    private static final int MY_KITKAT_RESULT_CODE_FILECHOOSER = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_question_set);

        SharedPreferences preferences = getSharedPreferences("UserID",MODE_PRIVATE);
        userID = preferences.getString("userID","Unknown");

        Btn_Confirm =findViewById(R.id.Btn_Confirm_Create);
        name_input = findViewById(R.id.name_set_input_create);
        creator_input = findViewById(R.id.creator_input_create);
        date_create_input = findViewById(R.id.date_create_input_create);
        grade_input = findViewById(R.id.grade_input_create);
        rbCreateQuesManually = findViewById(R.id.radio_button_1);
        rbUsingWordInput = findViewById(R.id.radio_button_2);
        radioGroup = findViewById(R.id.radioGroup_select_create_question_by);
        tvFile = findViewById(R.id.tv_file);
        tvPath_of_file = findViewById(R.id.tv_path_of_selected_file);

        check_input_run(userID);

        rbCreateQuesManually.setChecked(true);

        Btn_Confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_input_run(userID);
                String name = name_input.getText().toString();
                String creator = creator_input.getText().toString();
                String grade = grade_input.getText().toString();
                String date_create = date_create_input.getText().toString();

                ArrayList<String> listID = new ArrayList<>();
                ref_set.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ss: snapshot.getChildren()){
                            String id = String.valueOf(ss.child("setID").getValue());
                            listID.add(id);
                        }

                        boolean isCreated = false;
                        long counter=1;
                        while(!isCreated)
                        {
                            String id ="set"+counter;
                            if (listID.contains(id))
                                counter++;
                            else isCreated=true;
                        }
                        setID ="set"+counter;
                        boolean checkFileType = selectedfile.endsWith(".docx") || selectedfile.endsWith(".doc");
                        if (rbCreateQuesManually.isChecked()) {
                            Intent intent = new Intent(getApplicationContext(), CreateQuestion.class);
                            intent.putExtra("setID", setID);
                            startActivity(intent);
                            finish();
                        } else if (rbUsingWordInput.isChecked()) {
                            if (checkFileType)
                            {
                                Intent intent = new Intent(getApplicationContext(),
                                        CreateQuestion_UsingInputFile.class);
                                intent.putExtra("setID", setID);
                                intent.putExtra("Path_Selectedfile", selectedfile);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Vui lòng kiểm tra lại file và thử lại!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        CreateSet(name, creator, userID, grade, date_create);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_button_1) {
                    if (tvFile.getVisibility() == View.VISIBLE) {
                        tvFile.setVisibility(View.GONE);
                        tvPath_of_file.setVisibility(View.GONE);
                    }
                } else {
                    askPermissionAndBrowseFile();
                }
            }
        });
    }

    void CreateSet(String name, String creator,String userID,String grade, String date_create)
    {
        ArrayList<String> listID = new ArrayList<>();
        ref_set.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ss: snapshot.getChildren()){
                    String id = String.valueOf(ss.child("setID").getValue());
                    listID.add(id);
                }

                boolean isCreated = false;
                long counter=1;
                while(!isCreated)
                {
                    String id ="set"+counter;
                    if (listID.contains(id))
                        counter++;
                    else isCreated=true;
                }
                setID ="set"+counter;
                QuestionSet_Class set = new QuestionSet_Class(setID,name,userID,creator,date_create,grade);
                ref_set.child(setID).setValue(set).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ref_set.child(setID).child("quantity").setValue(0);
                        ref_hash.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                RandomClass randomClass = new RandomClass();
                                String hash = randomClass.getRandomString(15);
                                ref_hash.child(hash).setValue(setID);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        Toast.makeText(getApplicationContext(), "Tạo thành công", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    void check_input_run(String userID)
    {
        String name = name_input.getText().toString();
        String creator = creator_input.getText().toString();
        String grade = creator_input.getText().toString();

        if (name.equals(""))
        {
            name_input.setText("Không tên");
        }

//        Query checkUser = ref_user.orderByChild("name").equalTo(userID);

        ref_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tmp = Objects.requireNonNull(snapshot.child(userID).child("name").getValue()).toString();
                if (creator.equals(""))
                {
                    creator_input.setText(tmp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (grade.equals(""))
        {
            grade_input.setText("Không xác định");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(new Date());

        date_create_input.setText(date);
    }

    private void askPermissionAndBrowseFile()  {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            int permisson = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permisson != PackageManager.PERMISSION_GRANTED) {
                this.requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_REQUEST_CODE_PERMISSION
                );
                return;
            }
        }
        doBrowseFile();
    }

    private void doBrowseFile()  {
        if (Build.VERSION.SDK_INT <28) {
            Intent chooseFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            chooseFileIntent.setType("*/*");
            // Only return URIs that can be opened with ContentResolver
            chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");

            startActivityForResult(chooseFileIntent, MY_RESULT_CODE_FILECHOOSER);
        } else {
            Intent chooseFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            chooseFileIntent.setType("*/*");
            chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFileIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            chooseFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
            startActivityForResult(chooseFileIntent, MY_KITKAT_RESULT_CODE_FILECHOOSER);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted!", Toast.LENGTH_SHORT).show();

                    doBrowseFile();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();

            if (requestCode == MY_KITKAT_RESULT_CODE_FILECHOOSER) {
                int takeFlags = data.getFlags();
                takeFlags &= (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
            }

            if (uri.getPath().contains(":"))
            {
                String[] tmp = uri.getPath().split(":");
                selectedfile = tmp[1];
                String path = Environment.getExternalStorageDirectory().toString();
                selectedfile = path +"/"+ selectedfile;
                tvPath_of_file.setText(selectedfile);
                if (tvFile.getVisibility() == View.GONE) {
                    tvPath_of_file.setVisibility(View.VISIBLE);
                    tvFile.setVisibility(View.VISIBLE);
                }
            }
            else {
                selectedfile = uri.getPath();
                tvPath_of_file.setText(selectedfile);
                if (tvFile.getVisibility() == View.GONE) {
                    tvPath_of_file.setVisibility(View.VISIBLE);
                    tvFile.setVisibility(View.VISIBLE);
                }
            }
        }
        else
        {
            rbCreateQuesManually.setChecked(true);
        }

    }

}