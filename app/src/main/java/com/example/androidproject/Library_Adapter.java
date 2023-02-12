package com.example.androidproject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Objects;

public class Library_Adapter extends ArrayAdapter<QuestionSet_Class> {
    ArrayList<QuestionSet_Class> set = new ArrayList<>();
    Context context;

    Library_Adapter(Context context,ArrayList<QuestionSet_Class> set)
    {
        super(context,R.layout.item_library,set);
        this.set = set;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_library, null, false);
        }

        TextView tv_name = convertView.findViewById(R.id.tv_name_set_library);
        TextView tv_grade = convertView.findViewById(R.id.tv_grade_library);
        TextView tv_creator = convertView.findViewById(R.id.tv_creator_library);
        Button Btn_Copy = convertView.findViewById(R.id.Btn_Copy_Code_Library);
        Button Btn_Detail = convertView.findViewById(R.id.Btn_Detail_Library);

        QuestionSet_Class ques_set = (QuestionSet_Class) getItem(position);

        String name_suffix = "Tên bộ câu hỏi: ";
        String grade = tv_grade.getText().toString()+ques_set.getGrade();
        String creator = tv_creator.getText().toString()+ques_set.getSet_creator();
        if (tv_name.getText().toString().equals(name_suffix))
        {
            tv_name.append(ques_set.getName());
            tv_grade.append(ques_set.getGrade());
            tv_creator.append(ques_set.getSet_creator());
        }


        DatabaseReference ref_hash = FirebaseDatabase.getInstance().getReference("Hash");

        Btn_Copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref_hash.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String hash = null;
                        for(DataSnapshot ss:snapshot.getChildren())
                        {
                            String tmp = Objects.requireNonNull(ss.getValue()).toString();
                            if (tmp.equals(ques_set.getSetID()))
                            {
                                hash = ss.getKey();
                            }
                        }
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Copy text",hash);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context, "Đã sao chép vào clipboard, Mã: "+hash, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        Btn_Detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(),Detail_Item_Library_Activity.class);
                intent.putExtra("setID",ques_set.getSetID());
                parent.getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
