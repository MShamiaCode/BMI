package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    TextView go_to_sign_up;
    Button btn_login;
    EditText ed_username,ed_password;
    private FirebaseAuth mAuth;
    String Username,Password , Uid;
    private FirebaseDatabase database;
    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        rootRef=database.getReference("BMI");
        go_to_sign_up = findViewById(R.id.go_to_sign_up);
        btn_login = findViewById(R.id.btn_login);

        ed_password  =findViewById(R.id.ed_password);
        ed_username  =findViewById(R.id.ed_username);


        go_to_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                singin();
            }
        });


    }


    private void singin() {
        Username= ed_username.getText().toString().trim();
        Password= ed_password.getText().toString().trim();

        if (Username.isEmpty()) {
            ed_username.setError("username is required");
            ed_username.requestFocus();
            return;
        }

//        if (!Patterns.EMAIL_ADDRESS.matcher(Username).matches()) {
//            ed_username.setError("Please enter a valid Username");
//            ed_username.requestFocus();
//            return;
//        }

        if (Password.isEmpty()) {
            ed_password.setError("Password is required");
            ed_password.requestFocus();
            return;
        }

        if (Password.length() < 6) {
            ed_password.setError("Minimum lenght of password should be 6");
            ed_password.requestFocus();
            return;
        }
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String pass = snapshot.child("password").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);

                    if (pass.equals(Password)){
                        if (name.equals(Username)){
                        Intent intent =  new Intent(LoginActivity.this,HomeActivity.class);
                        intent.putExtra("name",name);
                        startActivity(intent);
                        }else {
                            Toast.makeText(LoginActivity.this, "error username", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(LoginActivity.this, "error password", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        };
//        FirebaseUser user = mAuth.getCurrentUser();
//         Uid = user.getUid();
//        if (!Uid.isEmpty()) {
        Uid = mAuth.getCurrentUser().getUid();
            rootRef.child("Users").child(Uid).addListenerForSingleValueEvent(listener);
//        }else{
//            Toast.makeText(LoginActivity.this, "error, uid is empty", Toast.LENGTH_SHORT).show();
//        }
//        mAuth.signInWithEmailAndPassword(Username, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//
//                if (task.isSuccessful()) {
//                    finish();
//                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            finish();
//            startActivity(new Intent(this, TenantMainActivity.class));
        }
    }


}