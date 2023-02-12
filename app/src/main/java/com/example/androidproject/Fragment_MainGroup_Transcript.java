package com.example.androidproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_MainGroup_Transcript#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_MainGroup_Transcript extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Bundle bundle;

    public Fragment_MainGroup_Transcript() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *.
     * @return A new instance of fragment Fragment_MainGroup_Transcript.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_MainGroup_Transcript newInstance(Bundle bundle) {
        Fragment_MainGroup_Transcript fragment = new Fragment_MainGroup_Transcript();
        Bundle args = new Bundle();
        fragment.setArguments(bundle);
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

    String userID;
    String groupID;
    DatabaseReference assignmentReference = FirebaseDatabase.getInstance().getReference("Assignment");
    DatabaseReference groupReference = FirebaseDatabase.getInstance().getReference("Group");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__main_group__transcript, container, false);

        ListView listView = view.findViewById(R.id.lv_main_group_transcript);

        List<ScoreClass> list = new ArrayList<ScoreClass>();

        Bundle bundle = this.getArguments();
        userID = bundle.getString("userID");
        groupID = bundle.getString("groupID");

        Log.i("Test bundle", userID+" "+groupID);

        Personal_Transcript_Adapter adapter = new Personal_Transcript_Adapter(getContext(),R.layout.personal_transcript_item,list);
        listView.setAdapter(adapter);
        groupReference.child(groupID).child("allAsID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    String allAsID = snapshot.getValue().toString();
                    Log.i("all as id",allAsID);

                    if(allAsID.matches("Unknown"))
                        return;

                    String[] allAsIDArr = allAsID.split(",");
                    for(int i= 0;i<allAsIDArr.length;i++)
                    {
                        Log.i("asID", allAsIDArr[i]);
                        assignmentReference.child(allAsIDArr[i]).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    ScoreClass score = new ScoreClass();
                                    String asName = snapshot.child("asName").getValue().toString();

                                    String point="";
                                    if(snapshot.child("transcript").child(userID).getValue()!=null)
                                    {
                                        point = snapshot.child("transcript").child(userID).getValue().toString();
                                        if(point.matches("Unknown"))
                                            point="";
                                    }

                                    score = new ScoreClass(asName,point);
                                    list.add(score);
                                    adapter.notifyDataSetChanged();

                                    Log.i("all as id",asName+" "+point);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}