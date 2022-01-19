package com.example.bmi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bmi.model.ViewFood;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditFoodActivity extends AppCompatActivity {
    ImageView image_food_edit;

    EditText ed_name_food_edit, ed_calory_edit;
    Button btn_upload_photo_edit,btn_save_food_edit;
    DatabaseReference Ref;
    String name,calory, image ,key;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String  downloadImageUrl;
    private StorageReference ImagesRef;
    StorageReference filePath;
    Spinner spinner_category_edit;
    String category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

//        getCategory = getIntent().getExtras().getString("getCategory");
        Ref = FirebaseDatabase.getInstance().getReference().child("BMI").child("Food").child(key);

        ImagesRef = FirebaseStorage.getInstance().getReference().child("Food Images");

        ed_name_food_edit = findViewById(R.id.ed_name_food_edit);
        spinner_category_edit = findViewById(R.id.spinner_category_edit);
        ed_calory_edit = findViewById(R.id.ed_calory_edit);

        image_food_edit = findViewById(R.id.image_food_edit);
        btn_upload_photo_edit = findViewById(R.id.btn_upload_photo_edit);
        btn_save_food_edit = findViewById(R.id.btn_save_food_edit);



        Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    key = snapshot.child("key").getValue().toString();
                    ViewFood viewFood = snapshot.getValue(ViewFood.class);
                    ed_name_food_edit.setText(viewFood.getName());
                    ed_calory_edit.setText(viewFood.getCalory()+"");
                    spinner_category_edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            category = spinner_category_edit.getItemAtPosition(position).toString();

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    Picasso.get().load(viewFood.getImage()).into(image_food_edit);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btn_save_food_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StoreProductInformation();
                SaveProductInfoToDatabase();

                finish();
            }

        });

        btn_upload_photo_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });


    }




    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==GalleryPick  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            ImageUri = data.getData();
            image_food_edit.setImageURI(ImageUri);
        }
    }


    private void StoreProductInformation()
    {

        filePath = ImagesRef.child(ImageUri.getLastPathSegment()  + ".jpg");

        final UploadTask uploadTask = filePath.putFile(ImageUri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                String message = e.toString();
                Toast.makeText(EditFoodActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(EditFoodActivity.this, "Product Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        downloadImageUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl = task.getResult().toString();

                            Toast.makeText(EditFoodActivity.this, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show();

                            SaveProductInfoToDatabase();
                        }
                    }
                });
            }
        });
    }



    private void SaveProductInfoToDatabase()
    {


        name = ed_name_food_edit.getText().toString();
        calory = ed_calory_edit.getText().toString();



        HashMap<String, Object> ChaletMap = new HashMap<>();
        ChaletMap.put("name", name);
        ChaletMap.put("image", downloadImageUrl);
        ChaletMap.put("calory", calory);
        ChaletMap.put("category", category);


        Ref.updateChildren(ChaletMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(EditFoodActivity.this, "تم التعديل", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}