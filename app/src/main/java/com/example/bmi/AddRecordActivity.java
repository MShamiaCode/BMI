package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.bmi.model.OldStatues;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AddRecordActivity extends AppCompatActivity {
    private String weight,length,date,time;

    private DatabaseReference Ref;
    private ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    EditText ed_weight,ed_length,ed_date,ed_time;
    Button btn_save_data_record;
    String classified, result,currentDate;
    int count1,count2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        mAuth = FirebaseAuth.getInstance();

        Ref = FirebaseDatabase.getInstance().getReference("BMI");
        currentDate = getIntent().getExtras().getString("currentDate");



        btn_save_data_record =  findViewById(R.id.btn_save_data_record);
        ed_weight =  findViewById(R.id.ed_weight);
        ed_length =  findViewById(R.id.ed_length);
        ed_date =  findViewById(R.id.ed_date);
        ed_time =  findViewById(R.id.ed_time);



        loadingBar = new ProgressDialog(this);

        ed_length.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (!ed_length.getText().toString().isEmpty()) {
                    count2 = Integer.parseInt(ed_length.getText().toString());
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (ed_length.getRight() - ed_length.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        count2++;
                        ed_length.setText(count2+"");
                        return true;
                    } else if (event.getRawX() >= (ed_length.getLeft() - ed_length.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        count2--;
                        ed_length.setText(count2+"");
                        return true;
                    }else {
                    }
                }
                return false;
            }
        });

        ed_weight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (!ed_weight.getText().toString().isEmpty()) {
                    count1 = Integer.parseInt(ed_weight.getText().toString());
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (ed_weight.getRight() - ed_weight.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        count1++;
                        ed_weight.setText(count1+"");
                        return true;
                    } else if (event.getRawX() >= (ed_weight.getLeft() - ed_weight.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        count1--;
                        ed_weight.setText(count1+"");
                        return true;
                    }else {
                    }
                }
                return false;
            }
        });

        btn_save_data_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateRecordData();
            }
        });



    }


    private void ValidateRecordData() {
        date = ed_date.getText().toString();
        time = ed_time.getText().toString();


        if (TextUtils.isEmpty(weight)) {
            Toast.makeText(this, "Please write weight...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(length)) {
            Toast.makeText(this, "Please write length...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(date)) {
            Toast.makeText(this, "Please write date...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(time)) {
            Toast.makeText(this, "Please write time...", Toast.LENGTH_SHORT).show();
        } else {
            SaveRecordInfoToDatabase();
        }


        int AgePercent = 2021-Integer.parseInt(currentDate);

        int Cmlength = Integer.parseInt(length);
        int Kgweight = Integer.parseInt(weight);
        int Mlength = Cmlength/100;


        int BMI = (Kgweight/Mlength^2)*AgePercent;

        ArrayList valuelist = new ArrayList<String>();
        valuelist.add("Little Changes");
        valuelist.add("Normal (Still Good)");
        valuelist.add("Go Ahead");
        valuelist.add("Be Careful");
        valuelist.add("So Bad");

        if (BMI < 18.5){
            classified = "Underweight";
            result = classified + valuelist.get(4).toString();

        }else if (18.5 <= BMI && BMI < 25){
            classified = "Healthy Weight";
            result = classified + valuelist.get(3).toString();

        }else if (25 <= BMI && BMI < 30){
            classified = "Overweight";
            result = classified + valuelist.get(1).toString();
        }else if (BMI > 30){
            classified = "Obesity";
            result = classified + valuelist.get(0).toString();

        }
    }




    private void SaveRecordInfoToDatabase() {
        HashMap<String, Object> ChaletMap = new HashMap<>();
        ChaletMap.put("weight", count1+" kg");
        ChaletMap.put("length", count2+" cm");
        ChaletMap.put("date", date);
        ChaletMap.put("time", time);
        ChaletMap.put("curren status", result);



        String key = FirebaseDatabase.getInstance().getReference("Users").push().getKey();
        Ref.child("Record").child(key).updateChildren(ChaletMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent idToList = new Intent(AddRecordActivity.this, MainActivity.class);
                            startActivity(idToList);

                            loadingBar.dismiss();
                            Toast.makeText(AddRecordActivity.this, "Chalet is added successfully..", Toast.LENGTH_SHORT).show();

                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddRecordActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

