package com.example.joyrasmussen.tripmessenger;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.UUID;

public class EditTripActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ImageView image;
    EditText name, location;
    Trip thisTrip;
    private DatabaseReference mDatabase, tripReference;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef;
    Uri filePath;
    String imageURL;
    String tripID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        auth = FirebaseAuth.getInstance();
        authListener();
        tripID = getIntent().getStringExtra("tripID");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        populateViews();
    }
    public void populateViews(){
        if(tripID.equals("") || tripID== null){
            tripID = UUID.randomUUID().toString();
        }
        tripReference = mDatabase.child("trips");


        name = (EditText) findViewById(R.id.tripNameETEditTrip);
        location = (EditText) findViewById(R.id.locationETTripEdit);
        image = (ImageView) findViewById(R.id.imageTripEdit);
        tripReference.child(tripID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisTrip = dataSnapshot.getValue(Trip.class);
                if (thisTrip != null) {
                    name.setText(thisTrip.getName());
                    if (thisTrip.getLocation() != null) {
                        location.setText(thisTrip.getLocation());
                    }
                    setImage(thisTrip.getPhoto());
                }else{
                    thisTrip = new Trip(tripID);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sign_out_only, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.signOutONly:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(EditTripActivity.this, "Sign out was successful", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {

                                }
                            }
                        });
                return true;
            default:
                return true;
        }
    }

    @Override
    protected void onStart() {
        auth.addAuthStateListener(mAuthListener);
        super.onStart();
    }

    private void authListener(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d( "onAuthStateChanged: ", "signed in");

                    //start edit profile automatically
                } else {
                    finish();
                }
            }
        };
    }

    public void onCancelListener(View v){
        finish();
    }
    public void onEditListener(View v){
        thisTrip.setName(name.getText().toString());
        thisTrip.setLocation(location.getText().toString());
        thisTrip.setCreator(user.getUid());
        final DatabaseReference tripMembers =  mDatabase.child("tripMembers").child(tripID);
        tripMembers.child(user.getUid()).setValue(true);
        tripMembers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userz = dataSnapshot.child(user.getUid()).getKey();

                Log.d( "onDataChange: ", "tripMembers" + userz);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(filePath != null) {
            try {


                StorageReference avatarRef = storageRef.child("imagesTrips/" + tripID  + ".png");

                UploadTask uploadTask = avatarRef.putFile(filePath);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(EditTripActivity.this, "Image upload failure", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        @SuppressWarnings("VisibleForTests") String downloadUrl = taskSnapshot.getDownloadUrl().toString();

                        //mDatabase.child("users").child(userID).child("imageURL").setValue(downloadUrl);


                        Log.i("image: ", downloadUrl);
                        //path = "images/" + userID + ".png";

                        setImage(downloadUrl);
                        thisTrip.setPhoto(downloadUrl);
                        tripReference.child(tripID).setValue(thisTrip);

                        //Need to update join list here



                        Toast.makeText(EditTripActivity.this, "Trip Successfully Updated", Toast.LENGTH_LONG).show();
                        finish();

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
           tripReference.child(tripID).setValue(thisTrip);
            finish();
        }

    }

    public void uploadTripImage(View v){
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
    public void setImage(String url){

        if(url != null && !url.equals("")) {

            Picasso.Builder builder = new Picasso.Builder(EditTripActivity.this);
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
