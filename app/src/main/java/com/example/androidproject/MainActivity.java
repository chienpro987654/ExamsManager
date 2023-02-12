package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    FloatingActionButton FBtn_Group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.View_Navigation);
        FBtn_Group = findViewById(R.id.FBtn_Group);

        Bundle bundle = getIntent().getExtras();
        String userID = bundle.getString("userID");

        //Use to send data to all activity that need userID to access fBase
        SharedPreferences preferences = getSharedPreferences("UserID",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userID",userID);
        editor.apply();

        Intent intent = getIntent();
        int select_item = intent.getIntExtra("itemFrag",2);

        FBtn_Group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Fragment_Group();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
                bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(2).getItemId());
            }
        });

        //set Launcher Fragment
        FBtn_Group.performClick();
        if (select_item==1)
        {
            Fragment fragment = new Fragment_Library();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            bottomNavigationView.setSelectedItemId(bottomNavigationView.getMenu().getItem(select_item).getItemId());
        }

        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                Fragment temp = null;

                switch (item.getItemId())
                {
                    case R.id.ic_notify:
                        temp = new Fragment_Notify();
                        break;
                    case R.id.ic_library:
                        temp = new Fragment_Library();
                        break;

                    case R.id.ic_assignment:
                        temp = new Fragment_Assignment();
                        break;

                    case R.id.ic_profile:
                        temp = new Fragment_Profile();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,temp).commit();
                return true;
            }
        });
    }

    long pressedTime;

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
}