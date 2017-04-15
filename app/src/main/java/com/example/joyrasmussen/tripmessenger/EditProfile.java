package com.example.joyrasmussen.tripmessenger;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    EditText fnameET, lnameET, genderET;
    ImageView image;

    String fname, lname, gender, userID;

    private DatabaseReference mDatabase;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        fnameET = (EditText) findViewById(R.id.fNameEditText);
        lnameET = (EditText) findViewById(R.id.lNameEditText);
        genderET = (EditText) findViewById(R.id.genderProfileET);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        //put code here to auto-fill first name, last name, gender if they are already set

    }


    public void update(View v) {

        fname = fnameET.getText().toString();
        lname = lnameET.getText().toString();
        gender = genderET.getText().toString();

        mDatabase.child("users").child(userID).child("firstName").setValue(fname);
        mDatabase.child("users").child(userID).child("lastName").setValue(lname);
        mDatabase.child("users").child(userID).child("gender").setValue(gender);

    }


    public void upload(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();

            try {
                StorageReference avatarRef = storageRef.child("images/" + userID + ".png");

                UploadTask uploadTask = avatarRef.putFile(filePath);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(EditProfile.this, "Image upload failure", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        mDatabase.child("users").child(userID).child("imageURL").setValue(downloadUrl);

                        Toast.makeText(EditProfile.this, "Image upload success", Toast.LENGTH_SHORT).show();

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cancel(View v) {
        finish();
    }
}
