package com.example.joyrasmussen.tripmessenger;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoom extends AppCompatActivity {
    private String chatID;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tripReference;
    DatabaseReference postReferences;
    DatabaseReference deleteReference, userReference;
    ValueEventListener tripListener, postListener, deleteListener, userListener;
    FirebaseUser user;
    RecyclerView postRecycler;
    ArrayList<String> isVisible;
    EditText message;
    ImageView image;
    User currentUser;
    User postUser;
    TextView name, location;
    FirebaseRecyclerAdapter<Message, ChatPostHolder> mAdapter;
    Query query;
    private FirebaseAuth mAuth;
    Trip thisTrip;
    private FirebaseAuth.AuthStateListener mAuthListener;
    LinearLayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        chatID = getIntent().getStringExtra("chatID");

        populate();

    }

    private void populate(){
        tripReference = mDatabase.child("trips").child(chatID);
        postReferences = mDatabase.child("chat").child(chatID);
        message = (EditText) findViewById(R.id.messageEditTextChat);
        image = (ImageView) findViewById(R.id.tripImage);
        name = (TextView) findViewById(R.id.chatTripName);
        postRecycler = (RecyclerView) findViewById(R.id.chatRoomRecycler);
        location = (TextView) findViewById(R.id.tripLocation);
        deleteReference = mDatabase.child("deleteChats").child(chatID);
        userReference = mDatabase.child("users");
        setmAdapter();


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
        userReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void setmAdapter(){
        query = postReferences.orderByChild("time");
        mAdapter = new FirebaseRecyclerAdapter<Message, ChatPostHolder>(Message.class, R.layout.chat_post_holder,
                ChatPostHolder.class, query) {
            @Override
            protected void populateViewHolder(final ChatPostHolder viewHolder, final Message model, int position) {
                viewHolder.setImage(model.getImageURL());
                viewHolder.setPost(model.getText());
                viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        deleteReference.child(model.getId()).setValue(true);
                        mAdapter.notifyDataSetChanged();
                        return false;
                    }
                });
               deleteReference.child(model.getId()).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.child(currentUser.getId()).exists()){

                            //viewHolder.setUser(mode);

                            try {
                                viewHolder.setTime(model.getTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                           if(model.getId().equals(currentUser.getId())){
                               viewHolder.setUser(currentUser.getFirstName() + " " + currentUser.getLastName());

                           }else{
                               userReference.child(model.getUsrId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(DataSnapshot dataSnapshot) {
                                       User thisUser = dataSnapshot.getValue(User.class);
                                       viewHolder.setUser(thisUser.getFirstName() + " " + thisUser.getLastName());
                                   }

                                   @Override
                                   public void onCancelled(DatabaseError databaseError) {

                                   }
                               });

                           }

                        }
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
            }
        };

        mLayoutManager = new LinearLayoutManager(this);
        postRecycler.setHasFixedSize(false);
        postRecycler.setLayoutManager(mLayoutManager);
        postRecycler.setAdapter(mAdapter);

    }

    public void onGalleryListener(View v){


    }
    public void onSentMessage(View v){


    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        authListener();
        mAuth.addAuthStateListener(mAuthListener);
        ValueEventListener listen = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisTrip = dataSnapshot.getValue(Trip.class);
                name.setText(thisTrip.getName());
                location.setText(thisTrip.getLocation());
                String creator =thisTrip.getCreator();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ValueEventListener postListen = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tripReference.addValueEventListener(listen);
        postReferences.addListenerForSingleValueEvent(postListen);
        tripListener = listen;
        postListener = postListen;
        ValueEventListener deleteListen = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snaps : dataSnapshot.getChildren()) {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        deleteReference.addValueEventListener(deleteListen);
        deleteListener = deleteListen;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(tripListener != null){
            tripReference.removeEventListener(tripListener);
        }
        if(postListener != null){
            postReferences.removeEventListener(postListener);
        }
        if(deleteListener !=null){
            deleteReference.removeEventListener(deleteListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                                    Toast.makeText(ChatRoom.this, "Sign out was successful", Toast.LENGTH_LONG).show();
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


}
