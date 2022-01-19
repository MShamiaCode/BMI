package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.bmi.model.DatePickerFragment;
import com.example.bmi.model.OldStatues;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CreateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    Button btn_save_data;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;
    String valueSex,dateOfBirth;
    EditText ed_weight_kg,ed_length_cm,ed_dateOfBirth;
    int count1,count2;
    private DatabaseReference Ref;
    String uid,name,email,password;
    FirebaseAuth mAuth;
    int dateOfBirth_year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        mAuth = FirebaseAuth.getInstance();

        name = getIntent().getExtras().getString("name");
        uid = getIntent().getExtras().getString("uid");
        email = getIntent().getExtras().getString("email");
        password = getIntent().getExtras().getString("password");

        btn_save_data = findViewById(R.id.btn_save_data);
        radioSexGroup = findViewById(R.id.radioSex);
        ed_weight_kg = findViewById(R.id.ed_weight_kg);
        ed_length_cm = findViewById(R.id.ed_length_cm);
        ed_dateOfBirth = findViewById(R.id.ed_dateOfBirth);

        btn_save_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addListenerOnButton();
                SaveInfoToDatabase();
//                Intent intent = new Intent(CreateActivity.this, HomeActivity.class);
//                startActivity(intent);
//                finish();
            }
        });


        ed_weight_kg.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (!ed_weight_kg.getText().toString().isEmpty()) {
                    count1 = Integer.parseInt(ed_weight_kg.getText().toString());
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (ed_weight_kg.getRight() - ed_weight_kg.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        count1++;
                        ed_weight_kg.setText(count1+"");
                        return true;
                    } else if (event.getRawX() >= (ed_weight_kg.getLeft() - ed_weight_kg.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        count1--;
                        ed_weight_kg.setText(count1+"");
                        return true;
                    }else {
                    }
                }
                return false;
            }
        });

        ed_length_cm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (!ed_length_cm.getText().toString().isEmpty()) {
                    count2 = Integer.parseInt(ed_length_cm.getText().toString());
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (ed_length_cm.getRight() - ed_length_cm.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        count2++;
                        ed_length_cm.setText(count2+"");
                        return true;
                    } else if (event.getRawX() >= (ed_length_cm.getLeft() - ed_length_cm.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                        count2--;
                        ed_length_cm.setText(count2+"");
                        return true;
                    }else {
                    }
                }
                return false;
            }
        });

        ed_dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

    }
    public void addListenerOnButton() {
    // get selected radio button from radioGroup
    int selectedId = radioSexGroup.getCheckedRadioButtonId();

    // find the radiobutton by returned id
    radioSexButton =  findViewById(selectedId);

			 valueSex = radioSexButton.getText().toString();
        Toast.makeText(CreateActivity.this, valueSex, Toast.LENGTH_SHORT).show();
    }




    private void SaveInfoToDatabase() {
        dateOfBirth = ed_dateOfBirth.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    uid = mAuth.getCurrentUser().getUid();
                    finish();
                    HashMap Map =new HashMap();
                    Map.put("Uid",uid);
                    Map.put("name",name);
                    Map.put("email",email);
                    Map.put("password",password+"");
                    Map.put("gender", valueSex);
                    Map.put("weight", count1 + " kg");
                    Map.put("length", count2+" cm");
                    Map.put("date of birth", dateOfBirth);

                    Ref.child("Users").child(uid).setValue(Map);

                    Intent intent = new Intent(CreateActivity.this, HomeActivity.class);
                    intent.putExtra("name",name);
                    intent.putExtra("year",dateOfBirth_year+"");
                    startActivity(intent);

                }
                else {

                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }


        });


    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
         dateOfBirth_year = year;
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        ed_dateOfBirth.setText(currentDateString);
    }
}