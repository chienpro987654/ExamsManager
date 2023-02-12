package com.example.androidproject;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextView name_input, email_input, pwd_input, confirm_pwd_input;
    private Button Btn_Signup;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        name_input = findViewById(R.id.name_input);
        email_input = findViewById(R.id.email_input);
        pwd_input = findViewById(R.id.password_input);
        confirm_pwd_input = findViewById(R.id.confirm_password_input);
        Btn_Signup = findViewById(R.id.Btn_Signup);
        progressBar = findViewById(R.id.pro_bar_register);

        //test
        name_input.setText("user");
        email_input.setText("user@gmail.com");
        pwd_input.setText("1234567");
        confirm_pwd_input.setText("1234567");

        Btn_Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    void register() {
        String name = name_input.getText().toString().trim();
        String email = email_input.getText().toString().trim();
        String pwd = pwd_input.getText().toString().trim();
        String re_pwd = confirm_pwd_input.getText().toString().trim();

        if (name.isEmpty()) {
            name_input.setError(getString(R.string.edit_text_error_empty));
            name_input.requestFocus();
            return;
        }

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

        if (re_pwd.isEmpty()) {
            confirm_pwd_input.setError(getString(R.string.edit_text_error_empty));
            confirm_pwd_input.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_input.setError(getString(R.string.email_invalid_error));
            email_input.requestFocus();
            return;
        }

        if (pwd.length() < 6) {
            pwd_input.setError(getString(R.string.password_short_error));
            pwd_input.requestFocus();
            return;
        }

        if (!pwd.equals(re_pwd)) {
            confirm_pwd_input.setError(getString(R.string.confirm_pwd_not_match_error));
            confirm_pwd_input.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(name, email, pwd);
                            String _name;
                            String[] tmp = email.split("@");
                            _name = tmp[0];
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(_name).setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Đăng ký thành công",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Đăng ký không thành công, vui lòng thử lại!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Đăng ký không thành công, vui lòng thử lại!",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}