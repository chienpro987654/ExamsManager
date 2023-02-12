package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView tv_register, email_input, pwd_input;
    Button Btn_Login;
    ProgressBar progressBar;
    String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        tv_register = findViewById(R.id.tv_register);
        email_input = findViewById(R.id.email_input);
        pwd_input = findViewById(R.id.password_input);
        Btn_Login = findViewById(R.id.Btn_Login);
        progressBar = findViewById(R.id.pro_bar_login);

        //test
        email_input.setText("user@gmail.com");
        pwd_input.setText("1234567");

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(intent);
            }
        });

        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

    }

    void Login()
    {
        String email = email_input.getText().toString().trim();
        String pwd = pwd_input.getText().toString().trim();

        if (email.isEmpty()) {
            email_input.setError(getString(R.string.edit_text_error_empty));
            email_input.requestFocus();
            return;
        }

        if (pwd.isEmpty()) {
            pwd_input.setError(getString(R.string.edit_text_error_empty));
            pwd_input.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_input.setError(getString(R.string.email_invalid_error));
            email_input.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    //When login, renew this user's login session
                    String[] tmp = email.split("@");
                    userID = tmp[0];
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
                    Query checkUser = reference.orderByChild("name").equalTo(userID);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
                            String login_session= simpleDateFormat.format(new Date());
                            reference.child(userID).child("login_session").setValue(login_session);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Login_Put_Data(email);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Đăng nhập thất bại! Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    void Login_Put_Data(String email)
    {
        Toast.makeText(getApplicationContext(), "Đăng nhập thành công!!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        String[] tmp = email.split("@");
        userID = tmp[0];
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and check if his session expired or not and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null)
        {
            String[] tmp = Objects.requireNonNull(currentUser.getEmail()).split("@");
            userID = tmp[0];
            final String[] login_session = new String[1];
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    login_session[0] = Objects.requireNonNull(snapshot.child(userID).child("login_session").getValue()).toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
            Calendar calendar = Calendar.getInstance();
            String date = simpleDateFormat.format(new Date());
            try {
                calendar.setTime(Objects.requireNonNull(simpleDateFormat.parse(date)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.add(Calendar.DATE,-7);
            String login_time = simpleDateFormat.format(calendar.getTime());
            if (!login_time.equals(login_session[0]))
            {
                updateUI(currentUser);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Hết phiên đăng nhập, vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    void updateUI(FirebaseUser firebaseUser)
    {
        String email = firebaseUser.getEmail();
        if (email != null)
        {
            Login_Put_Data(email);
        }
    }
}