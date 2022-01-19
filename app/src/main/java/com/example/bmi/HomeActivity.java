package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bmi.adapter.AdapterOldStatus;
import com.example.bmi.model.OldStatues;
import com.example.bmi.model.ViewFood;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {
    RecyclerView list;
    AdapterOldStatus adapter;
    Button btn_add_record,btn_add_food,btn_view_food;
    TextView txt_name_home;
    String name,weight,length,currentDate,classified;
    OldStatues oldStatues;
    DatabaseReference Ref;
    TextView txt_logout,currentStateUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Ref = FirebaseDatabase.getInstance().getReference().child("BMI").child("Record");
        list = findViewById(R.id.list);
        btn_add_record = findViewById(R.id.btn_add_record);
        btn_add_food = findViewById(R.id.btn_add_food);
        btn_view_food = findViewById(R.id.btn_view_food);
        txt_name_home = findViewById(R.id.txt_name_home);
        txt_logout = findViewById(R.id.txt_logout);
        currentStateUser = findViewById(R.id.currentStateUser);

        name = getIntent().getExtras().getString("name");
        currentDate = getIntent().getExtras().getString("year");

        Ref = FirebaseDatabase.getInstance().getReference().child("BMI").child("Record");

        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    weight = snapshot.child("weight").getValue().toString();
                    length = snapshot.child("length").getValue().toString();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
            currentStateUser.setText(classified + valuelist.get(4).toString());

        }else if (18.5 <= BMI && BMI < 25){
            classified = "Healthy Weight";
            currentStateUser.setText(classified + valuelist.get(3).toString());

        }else if (25 <= BMI && BMI < 30){
            classified = "Overweight";
            currentStateUser.setText(classified + valuelist.get(1).toString());
        }else if (BMI > 30){
            classified = "Obesity";
            currentStateUser.setText(classified + valuelist.get(0).toString());

        }



        txt_name_home.setText("Hi, " + name);

        txt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_add_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddRecordActivity.class);
               intent.putExtra("currentDate","currentDate");
                startActivity(intent);
            }
        });

        btn_view_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ViewFoodActivity.class);
                startActivity(intent);
            }
        });

        btn_add_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddFoodDetailsActivity.class);
                startActivity(intent);
            }
        });


        ArrayList<OldStatues> models = new ArrayList<>();
        list.setLayoutManager(new LinearLayoutManager(HomeActivity.this));


        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                models.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    models.add(oldStatues);

                }
                adapter = new AdapterOldStatus(models, HomeActivity.this);
                list.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
