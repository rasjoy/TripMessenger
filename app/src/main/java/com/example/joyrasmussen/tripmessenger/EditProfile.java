package com.example.joyrasmussen.tripmessenger;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

public class EditProfile extends AppCompatActivity {

    EditText fnameET, lnameET, genderET;
    ImageView image;
    User userObject;
    Button update, cancel;
    String fname, lname, gender, userID, path;

    private DatabaseReference mDatabase, userReference;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;
    Uri filePath;
    String imageURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        update = (Button) findViewById(R.id.updateProfileButton);
        cancel = (Button) findViewById(R.id.cancelProfilebutton);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        fnameET = (EditText) findViewById(R.id.tripNameETEditTrip);
        lnameET = (EditText) findViewById(R.id.locationETTripEdit);
        genderET = (EditText) findViewById(R.id.genderProfileET);
        image = (ImageView) findViewById(R.id.imageView);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userReference = mDatabase.child("users");
        userID = user.getUid();
        userObject = new User();
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(user.getUid())){
                    Log.d( "onDataChange: ", "user is in database");
                    userObject = dataSnapshot.child(userID).getValue(User.class);
                    //populate the edit text here

                    fnameET.setText(userObject.getFirstName());
                    lnameET.setText(userObject.getLastName());
                    genderET.setText(userObject.getGender());

                    //use the setimage method to populate image
                    imageURL = userObject.getImageURL();

                    setImage(imageURL);
                }else {

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void update(View v) {

        fname = fnameET.getText().toString();
        lname = lnameET.getText().toString();
        gender = genderET.getText().toString();
        String fullName = fname + "_" + lname;
        userObject.setFirstName(fname);
        userObject.setLastName(lname);
        userObject.setGender(gender);
        userObject.setId(userID);
        userObject.setFullName(fullName);
        update.setEnabled(false);
        cancel.setEnabled(false);
        if(filePath != null) {
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

                        @SuppressWarnings("VisibleForTests") String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                        //mDatabase.child("users").child(userID).child("imageURL").setValue(downloadUrl);

                        path = "images/" + userID + ".png";

                        Log.i("image: ", downloadUrl);
                        //path = "images/" + userID + ".png";
                        path = downloadUrl;
                        setImage(downloadUrl);
                        userObject.setImageURL(downloadUrl);
                        userObject.setId(userID);
                        userReference.child(userID).setValue(userObject);

                        userReference.child(userID).setValue(userObject);
                        Toast.makeText(EditProfile.this, "Profile Successfully Updated", Toast.LENGTH_LONG).show();
                        finish();

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            userReference.child(userID).setValue(userObject);
            finish();
        }
       // Log.d("update: ", path);
        //userObject.setImageURL(path);

        //mDatabase.child("users").child(userID).child("firstName").setValue(fname);
        //mDatabase.child("users").child(userID).child("lastName").setValue(lname);
        //mDatabase.child("users").child(userID).child("gender").setValue(gender);

    }


    public void upload(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
           setImage(filePath.toString());


        }
    }

    public void cancel(View v) {
        finish();
    }

    public void setImage(String url){

        if(url != null && !url.equals("")) {

            Picasso.Builder builder = new Picasso.Builder(EditProfile.this);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                    Log.i("uri: ", uri + "");
                }
            });
            builder.build().load(url).into(image);
        }
    }
}
