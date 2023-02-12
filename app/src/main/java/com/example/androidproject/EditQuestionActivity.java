package com.example.androidproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EditQuestionActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationManagerEditQuestion navigationManager;

    private ListView lv_set;

    String setID;

    ArrayList<String> arrayList = new ArrayList<>();
    HashMap<String,String> hashMap = new HashMap<>();

    DatabaseReference ref_set = FirebaseDatabase.getInstance().getReference("QuestionSet");
    DatabaseReference ref_ques;

    EditQuestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        //initiate ID
        drawerLayout = findViewById(R.id.drawer_layout_edit_question);
        lv_set = findViewById(R.id.lv_list_edit_question);


        navigationManager = FragmentNavigationManagerEditQuestion.getmInstance(this);

        //Get header of navigation drawer
        View header = getLayoutInflater().inflate(R.layout.nav_header_edit_question,null,false);
        TextView tv_name_set = header.findViewById(R.id.tv_name_set_edit_question_header);
        TextView tv_quantity = header.findViewById(R.id.tv_quantity_set_edit_question_header);
        lv_set.addHeaderView(header);

        //Get setID from intent
        Intent intent = getIntent();
        setID = intent.getStringExtra("setID");

        //Show header data
        ref_set.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = Objects.requireNonNull(snapshot.child(setID).child("name").getValue()).toString();
                if (tv_name_set.getText().toString().equals("Tên bộ câu hỏi: "))
                    tv_name_set.append(name);

                String quantity = Objects.requireNonNull(snapshot.child(setID).child("quantity").getValue()).toString();
                if (tv_quantity.getText().toString().equals("Số lượng câu hỏi: "))
                    tv_quantity.append(quantity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref_ques = FirebaseDatabase.getInstance().getReference("QuestionSet").child(setID).child("ques");

        GetData();

        drawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lv_set.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String quesID = arrayList.get(position-1);
                navigationManager.ShowFragment(setID,quesID,position);
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void GetData() {
        ref_ques.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String question,quesID;
                long counter = 1;
                for(DataSnapshot ss:snapshot.getChildren())
                {
                    question = Objects.requireNonNull(ss.child("question").getValue()).toString();
                    quesID = Objects.requireNonNull(ss.child("quesID").getValue()).toString();
                    String item = "Câu "+counter+": "+question;
                    arrayList.add(quesID);
                    hashMap.put(quesID,question);
                }

                adapter = new EditQuestionAdapter(EditQuestionActivity.this,arrayList,hashMap);
                lv_set.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_edit_question,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    long pressedTime;

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
}