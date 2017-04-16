package com.example.joyrasmussen.tripmessenger;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewTripActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;

    TextView location, name, owner;
    Button editTrip, enterChatroom;
    RecyclerView members;
    ImageView image;
    Trip thisTrip;
    DatabaseReference tripReference;
    DatabaseReference mDatabase;
    DatabaseReference userReference;
    String tripID;
    ValueEventListener tripListener;
    boolean isOwner;
    DatabaseReference membersReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);

        populateEverything();

    }
    private void populateEverything(){
        mDatabase = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();
        authListener();
        location = (TextView) findViewById(R.id.locationTripView);
        name = (TextView) findViewById(R.id.tripNameViewTrip);
        editTrip = (Button) findViewById(R.id.editTripButton);
        enterChatroom = (Button) findViewById(R.id.enterChatTripView);
        members = (RecyclerView) findViewById(R.id.tripMemberRecycler);
        owner = (TextView) findViewById(R.id.ownerETtripView);
        image = (ImageView) findViewById(R.id.tripViewImage);
        tripID = getIntent().getStringExtra("tripID");
        tripReference = mDatabase.child("trips").child(tripID);
        membersReference = mDatabase.child("tripMembers").child(tripID);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(!isOwner){
            menu.getItem(R.id.edtiTripView).setVisible(false);

        }
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.trip_view_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()){
           case R.id.signOutTripViewMenu:
               if(isOwner){

                }else{

                }
                return true;
           case R.id.goToChatMenuTrip:
               //start chat intent for this activity
               return true;
           case R.id.edtiTripView:

               return true;
           default:
               return  true;

       }
    }

    @Override
    protected void onStart() {
        auth.addAuthStateListener(mAuthListener);
        super.onStart();
        ValueEventListener listen= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisTrip = dataSnapshot.getValue(Trip.class);
                location.setText(thisTrip.getLocation());
                String creator =thisTrip.getCreator();
                if(creator.equals( tripID)){
                        owner.setText("You");
                    isOwner = true;

                }else{
                    userReference = mDatabase.child("users").child(creator);
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User creater = dataSnapshot.getValue(User.class);
                            owner.setText(creater.getFirstName() + " " + creater.getLastName());
                            isOwner= false;
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        tripListener = listen;


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

    private void populateImage(){


    }
}
