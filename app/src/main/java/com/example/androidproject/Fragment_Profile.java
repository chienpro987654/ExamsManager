package com.example.androidproject;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class Fragment_Profile extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Profile() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
//    public static Fragment_Profile newInstance(String param1, String param2) {
//        Fragment_Profile fragment = new Fragment_Profile();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    DatabaseReference ref_user = FirebaseDatabase.getInstance().getReference("User");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref_storage = storage.getReference("avatars");

    MainActivity activity;
    EditText name_input, birth_input, grade_input, school_input;
    TextView tv_email;
    Button Btn_Update,Btn_Upload;
    ImageView img_log_out,img_avatar;
    String curr_userID;
    String name, email, birth, grade, school;
    private final int PICK_IMAGE_REQUEST=1;

    Uri imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__profile, container, false);
        name_input = view.findViewById(R.id.name_input_profile);
        birth_input = view.findViewById(R.id.birth_input_profile);
        grade_input = view.findViewById(R.id.grade_input_profile);
        school_input = view.findViewById(R.id.school_input_profile);
        tv_email = view.findViewById(R.id.tv_email_profile);
        Btn_Update = view.findViewById(R.id.Btn_Update_Profile);
        Btn_Upload = view.findViewById(R.id.Btn_Avatar_Add);
        img_log_out = view.findViewById(R.id.img_log_out);
        img_avatar = view.findViewById(R.id.img_avatar);
        activity = (MainActivity) getActivity();
        assert activity != null;
        SharedPreferences preferences = activity.getSharedPreferences("UserID", Context.MODE_PRIVATE);
        curr_userID = preferences.getString("userID","Unknown");
        ShowData(curr_userID);

        Btn_Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateData();
            }
        });

        Btn_Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(intent,PICK_IMAGE_REQUEST);
            }
        });

        img_log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth;
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            }
        });

        String tmp="xxx";

        return view;
    }

    private void UpdateData()
    {
        Query checkUser = ref_user.orderByChild("name").equalTo(curr_userID);
        ref_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tmp = name_input.getText().toString();
                ref_user.child(curr_userID).child("name").setValue(tmp);
                tmp = birth_input.getText().toString();
                ref_user.child(curr_userID).child("birth").setValue(tmp);
                tmp = grade_input.getText().toString();
                ref_user.child(curr_userID).child("grade").setValue(tmp);
                tmp = school_input.getText().toString();
                ref_user.child(curr_userID).child("school").setValue(tmp);
                if (imageUri!=null)
                {
                    String fileName = curr_userID+"."+getFileExtension(imageUri);
                    ref_storage.child(fileName).putFile(imageUri);
                    ref_user.child(curr_userID).child("avatar").setValue(fileName);
                }
                Toast.makeText(getActivity(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void ShowData(String userID)
    {
        Query checkUser = ref_user.orderByChild("name").equalTo(userID);

        ref_user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = Objects.requireNonNull(snapshot.child(userID).child("name").getValue()).toString();
                email = Objects.requireNonNull(snapshot.child(userID).child("email").getValue()).toString();
                birth = Objects.requireNonNull(snapshot.child(userID).child("birth").getValue()).toString();
                grade = Objects.requireNonNull(snapshot.child(userID).child("grade").getValue()).toString();
                school = Objects.requireNonNull(snapshot.child(userID).child("school").getValue()).toString();
                tv_email.setText(email);
                name_input.setText(name);
                birth_input.setText(birth);
                grade_input.setText(grade);
                school_input.setText(school);

                String avatar = Objects.requireNonNull(snapshot.child(userID).child("avatar").getValue()).toString();
                if (!avatar.equals("default"))
                {
                    final long ONE_MEGABYTE = 1024 * 1024;
                    ref_storage.child(avatar).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            DisplayMetrics dm = new DisplayMetrics();
                            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);

//                                    img_avatar.setMinimumHeight(dm.heightPixels);
//                                    img_avatar.setMinimumWidth(dm.widthPixels);
                            img_avatar.setImageBitmap(bm);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Toast.makeText(activity.getApplicationContext(), exception.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    img_avatar.setImageResource(R.drawable.avatar2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver resolver = activity.getContentResolver();
        MimeTypeMap map = MimeTypeMap.getSingleton();
        return map.getExtensionFromMimeType(resolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imageUri = data.getData();
            Picasso.with(activity.getApplicationContext()).load(imageUri).into(img_avatar);
        }
    }
}