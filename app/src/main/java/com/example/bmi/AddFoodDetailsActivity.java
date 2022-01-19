package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.HashMap;

public class AddFoodDetailsActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener{

    Button btn_upload_photo,btn_save_food;
    private String saveCurrentDate, saveCurrentTime;
    String name,category,calory, key;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String chaletRandomKey, downloadImageUrl;
    private StorageReference ImagesRef;
    private DatabaseReference Ref;
    private ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    private FirebaseDatabase database;
    EditText ed_name_food,ed_calory;
    ImageView image_food;
    String[] country = { " Fruit and vegetables", "Starchy food", "Dairy", "Protein", "Fat"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_details);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        ImagesRef = FirebaseStorage.getInstance().getReference().child("Food Images");
        Ref = FirebaseDatabase.getInstance().getReference("BMI");




        loadingBar = new ProgressDialog(this);


        Spinner spin =  findViewById(R.id.spinner_category);
         ed_name_food =  findViewById(R.id.ed_name_food);
         ed_calory =  findViewById(R.id.ed_calory);
        btn_save_food =  findViewById(R.id.btn_save_food);
        image_food =  findViewById(R.id.image_food);
        btn_upload_photo =  findViewById(R.id.btn_upload_photo);
        spin.setOnItemSelectedListener(AddFoodDetailsActivity.this);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 category = spin.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);



        btn_upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });


        btn_save_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });



    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            image_food.setImageURI(ImageUri);
        }
    }


    private void ValidateProductData() {
        name = ed_name_food.getText().toString();
        calory = ed_calory.getText().toString();



        if (ImageUri == null) {
            Toast.makeText(this, "Food image is mandatory...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please write name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(category)) {
            Toast.makeText(this, "Please write category food...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(calory)) {
            Toast.makeText(this, "Please write calory food...", Toast.LENGTH_SHORT).show();
        } else {
            StoreProductInformation();
        }
    }


    private void StoreProductInformation() {
        loadingBar.setTitle("Add New Food");
        loadingBar.setMessage("Dear user, please wait while we are adding the new Food.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        chaletRandomKey = saveCurrentDate + " " + saveCurrentTime;


        final StorageReference filePath = ImagesRef.child(ImageUri.getLastPathSegment() + chaletRandomKey + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(AddFoodDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddFoodDetailsActivity.this, "Food Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(AddFoodDetailsActivity.this, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }


    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> ChaletMap = new HashMap<>();
        ChaletMap.put("name", name);
        ChaletMap.put("calory", calory+" cal /g");
        ChaletMap.put("image", downloadImageUrl);
        ChaletMap.put("category", category);
        ChaletMap.put("date", saveCurrentDate);
        ChaletMap.put("time", saveCurrentTime);
        ChaletMap.put("key", key);



        key = FirebaseDatabase.getInstance().getReference("Users").push().getKey();
        Ref=database.getReference("BMI");
        Ref.child("Food").child(key).updateChildren(ChaletMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            loadingBar.dismiss();
                            Toast.makeText(AddFoodDetailsActivity.this, "added successfully..", Toast.LENGTH_SHORT).show();

                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(AddFoodDetailsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
        Toast.makeText(getApplicationContext(),country[position] , Toast.LENGTH_LONG).show();
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

}
