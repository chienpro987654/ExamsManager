package com.example.androidproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Assignment_Window extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment_window);

        TextView tvAssignment = (TextView) findViewById(R.id.TextView_Assignment_Window);
        TextView tvTimeRemain = (TextView) findViewById(R.id.TextView_TimeRemain_Assignment_Window);
        Button btSubmit = (Button) findViewById(R.id.Button_Submit_Assignment_Window);
        GridView gvAssignment = (GridView) findViewById(R.id.Grid_Assignment_Window);

        ArrayList<Answer_Class> answers = new ArrayList<>();
        Answer_Adapter answer_adapter = new Answer_Adapter(this,
                android.R.layout.simple_list_item_1, answers);
        gvAssignment.setAdapter(answer_adapter);


    }
}