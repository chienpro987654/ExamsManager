package com.example.androidproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class EditQuestionAdapter extends ArrayAdapter<String> {
    ArrayList<String> arrayList;
    HashMap<String,String> hashMap;
    Context context;

    EditQuestionAdapter(Context context,ArrayList<String> arrayList,HashMap<String,String> hashMap)
    {
        super(context, android.R.layout.simple_list_item_1,arrayList);
        this.arrayList = arrayList;
        this.hashMap = hashMap;
        this.context=context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null)
        {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null, false);
        }

        TextView tv_item = convertView.findViewById(android.R.id.text1);

        String item = "Câu "+(position+1)+": "+hashMap.get(arrayList.get(position));

        tv_item.setText(item);

        return convertView;
    }
}
