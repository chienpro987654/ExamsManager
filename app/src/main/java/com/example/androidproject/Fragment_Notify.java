package com.example.androidproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.collections4.Get;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Notify#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Notify extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Notify() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Notify.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Notify newInstance(String param1, String param2) {
        Fragment_Notify fragment = new Fragment_Notify();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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

    private ExpandableListView listView;
    private ImageView img_read_all;
    ArrayList<String> listGroup = new ArrayList<>();
    HashMap<String, Notify_Class> listChild = new HashMap<>();
    ArrayList<String> listGroup_sorted;
    DatabaseReference reference;
    String curr_userID;
    NotifyMainAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__notify, container, false);
        listView = view.findViewById(R.id.expandable_listview_notify);
        img_read_all = view.findViewById(R.id.img_check_notify);

        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        SharedPreferences preferences = activity.getSharedPreferences("UserID", Context.MODE_PRIVATE);
        curr_userID = preferences.getString("userID","Unknown");

        reference = FirebaseDatabase.getInstance().getReference("User").child(curr_userID).child("notification");

        GetNotifyData();

        img_read_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ss: snapshot.getChildren()){
                            String id = String.valueOf(ss.child("id").getValue());
                            reference.child(id).child("read").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    GetNotifyData();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        listView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Query checkUser = reference.orderByChild("name").equalTo(curr_userID);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reference.child(listGroup_sorted.get(groupPosition)).child("read").setValue("true");
                        Notify_Class nc = new Notify_Class(Objects.requireNonNull(listChild.get(listGroup_sorted.get(groupPosition))));
                        nc.setRead("true");
                        listChild.replace(listGroup_sorted.get(groupPosition),nc);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return view;
    }

    void GetNotifyData()
    {
        Query checkUser = reference.orderByChild("name").equalTo(curr_userID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String,String> list_sort = new HashMap<>();
                for(DataSnapshot ss: snapshot.getChildren()){
                    long counter = snapshot.getChildrenCount();
                    String id = String.valueOf(ss.child("id").getValue());
                    if (listGroup.size()<counter)
                    {
                        listGroup.add(id);
                    }

                    String content = String.valueOf(ss.child("content").getValue());
                    String date = String.valueOf(ss.child("date").getValue());
                    String date_happen = String.valueOf(ss.child("date_happen").getValue());
                    String grade = String.valueOf(ss.child("grade").getValue());
                    String title = String.valueOf(ss.child("title").getValue());
                    String read = String.valueOf(ss.child("read").getValue());
                    Notify_Class nc = new Notify_Class(id, grade, date, date_happen,title,content,read);
                    if (listChild.size()<counter)
                    {
                        listChild.put(id,nc);
//                        list_sort.put(id,date);
                    }
                    else
                    {
                        listChild.replace(id,nc);
//                        list_sort.replace(id,date);
                    }
                    list_sort.put(id,date);
                }

                List<Map.Entry<String, String> > list =
                        new LinkedList<Map.Entry<String, String>>(list_sort.entrySet());

                Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
                    public int compare(Map.Entry<String, String> o1,
                                       Map.Entry<String, String> o2)
                    {
                        String[] tmp1 = o1.getValue().split("\\.");
                        String[] tmp2 = o2.getValue().split("\\.");
                        if (tmp1[1].compareTo(tmp2[1]) > 0)
                        {
                            return -1;
                        }
                        else if (tmp1[1].compareTo(tmp2[1]) < 0)
                            return 1;
                        else
                        {
                            if (tmp1[0].compareTo(tmp2[0]) > 0)
                                return -1;
                            else if (tmp1[0].compareTo(tmp2[0]) > 0)
                                return 1;
                            else return 0;
                        }
                    }
                });
                listGroup_sorted = new ArrayList<>();
                for (Map.Entry<String, String> aa : list) {
                    listGroup_sorted.add(aa.getKey());
                }

                adapter = new NotifyMainAdapter(listGroup_sorted, listChild);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}