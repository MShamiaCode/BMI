package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bmi.adapter.AdapterOldStatus;
import com.example.bmi.adapter.AdapterViewFood;
import com.example.bmi.model.OldStatues;
import com.example.bmi.model.ViewFood;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewFoodActivity extends AppCompatActivity {
    RecyclerView list_food;
    AdapterViewFood adapter;
    ViewFood viewFood;
    DatabaseReference Ref;
    ArrayList<ViewFood> models;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_food);

        list_food = findViewById(R.id.list_food);
        Ref = FirebaseDatabase.getInstance().getReference().child("BMI").child("Food");

        models = new ArrayList<>();
        list_food.setLayoutManager(new LinearLayoutManager(ViewFoodActivity.this));


        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                models.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    models.add(viewFood);

                }
                adapter = new AdapterViewFood(models, ViewFoodActivity.this);
                list_food.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}