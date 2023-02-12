package com.example.androidproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 create questions and push to firebase
 */
public class Fragment_Library extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Library() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Library.
     */
    // TODO: Rename and change types and number of parameters
//    public static Fragment_Library newInstance(String param1, String param2) {
//        Fragment_Library fragment = new Fragment_Library();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

        public static Fragment_Library newInstance() {
        Fragment_Library fragment = new Fragment_Library();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    String curr_userID;
    ArrayList<QuestionSet_Class> set = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__library, container, false);

        Button Btn_Add = view.findViewById(R.id.Btn_Add_QuestionSet_Library);
        ListView listSet = view.findViewById(R.id.lv_question_set_library);

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        SharedPreferences preferences = activity.getSharedPreferences("UserID", Context.MODE_PRIVATE);
        curr_userID = preferences.getString("userID","Unknown");

        DatabaseReference ref_set = FirebaseDatabase.getInstance().getReference("QuestionSet");

        ref_set.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ss: snapshot.getChildren())
                {
                    String setID = Objects.requireNonNull(ss.child("setID").getValue()).toString();
                    String userID = Objects.requireNonNull(ss.child("userID").getValue()).toString();
                    String name = Objects.requireNonNull(ss.child("name").getValue()).toString();
                    String grade = Objects.requireNonNull(ss.child("grade").getValue()).toString();
                    String set_creator = Objects.requireNonNull(ss.child("set_creator").getValue()).toString();

                    QuestionSet_Class tmp = new QuestionSet_Class(setID,name,userID,set_creator,grade);
                    if (tmp.getUserID().equals(curr_userID))
                        set.add(tmp);
                    Library_Adapter adapter = new Library_Adapter(activity,set);
                    listSet.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        Btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),CreateQuestionSetActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }
}