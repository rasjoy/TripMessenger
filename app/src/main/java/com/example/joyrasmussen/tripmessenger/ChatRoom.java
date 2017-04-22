package com.example.joyrasmussen.tripmessenger;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatRoom extends AppCompatActivity {
    private String chatID;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tripReference;
    DatabaseReference postReferences;
    DatabaseReference deleteReference, userReference;
    ValueEventListener tripListener, postListener, deleteListener, userListener;
    RecyclerView postRecycler;
    ArrayList<String> isInvisible;
    ImageButton postButton;
    EditText message;
    ImageView image, postImage;
    User currentUser;
    User postUser;
    StorageReference storageRef;
    TextView name, location;
    FirebaseRecyclerAdapter<Message, ChatPostHolder> mAdapter;
    Query query;
    private FirebaseAuth mAuth;
    FirebaseUser user;
    Trip thisTrip;
    Uri filePath;
    private FirebaseAuth.AuthStateListener mAuthListener;
    LinearLayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        chatID = getIntent().getStringExtra("chatID");
        isInvisible = new ArrayList<>();
        populate();

    }

    private void populate(){
        tripReference = mDatabase.child("trips").child(chatID);
        postReferences = mDatabase.child("posts").child(chatID);
        message = (EditText) findViewById(R.id.messageEditTextChat);
        message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0){
                    postButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        postButton = (ImageButton) findViewById(R.id.sendButtonChat);
        postButton.setEnabled(false);
        image = (ImageView) findViewById(R.id.tripImage);
        name = (TextView) findViewById(R.id.chatTripName);
        postImage = (ImageView) findViewById(R.id.imageToPost);
        postRecycler = (RecyclerView) findViewById(R.id.chatRoomRecycler);
        location = (TextView) findViewById(R.id.tripLocation);
        deleteReference = mDatabase.child("deleteChats").child(chatID);
        userReference = mDatabase.child("users");
        storageRef = FirebaseStorage.getInstance().getReference();


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
                if(!isInvisible.contains(model.getId())) {
                    viewHolder.setImage(model.getImageURL());
                    viewHolder.setPost(model.getText());
                    try {
                        viewHolder.setTime(model.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            deleteReference.child(model.getId()).child(user.getUid()).setValue(true);
                            mAdapter.notifyDataSetChanged();
                            return false;
                        }
                    });
                    if (model.getUsrId().equals(user.getUid())) {
                        viewHolder.setUser("You");
                        viewHolder.itemView.setBackgroundResource(R.color.mBlue);

                    } else {
                        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User thisUser = dataSnapshot.child(model.getUsrId()).getValue(User.class);
                                viewHolder.setUser(thisUser.getFirstName() + " " + thisUser.getLastName());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                }else{
                    RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)viewHolder.itemView.getLayoutParams();
                    param.width = 0;
                    param.height = 0;
                    viewHolder.itemView.setLayoutParams(param);

                }
            }
        };

        mLayoutManager = new LinearLayoutManager(this);
        postRecycler.setHasFixedSize(false);
        postRecycler.setLayoutManager(mLayoutManager);
        postRecycler.setLongClickable(true);
        postRecycler.setAdapter(mAdapter);

    }

    public void onGalleryListener(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 111);


    }
    public void onSentMessage(View v){

            final Message newMessage = new Message();
            newMessage.setId(UUID.randomUUID().toString());
            newMessage.setTime( System.currentTimeMillis());
            newMessage.setUsrId(user.getUid());
            newMessage.setText(message.getText().toString());



            if(filePath != null){

                StorageReference avatarRef = storageRef.child("postImages/" + newMessage.getId() + ".png");
                UploadTask uploadTask = avatarRef.putFile(filePath);

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads

                        Toast.makeText(ChatRoom.this, "Image upload failure", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            hidePostImage();

                        @SuppressWarnings("VisibleForTests") String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                        newMessage.setImageURL(downloadUrl);
                            postReferences.child(newMessage.getId()).setValue(newMessage);

                    }
                });

            }else{

                postReferences.child(newMessage.getId()).setValue(newMessage);
                message.setText("");

            }

    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Log.d("Onstart", user.getUid());
        authListener();
        mAuth.addAuthStateListener(mAuthListener);
        ValueEventListener listen = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisTrip = dataSnapshot.getValue(Trip.class);
                name.setText(thisTrip.getName());
                location.setText(thisTrip.getLocation());
                setTripImage(thisTrip.getPhoto());
                String creator =thisTrip.getCreator();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ValueEventListener postListen = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setmAdapter();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tripReference.addValueEventListener(listen);
        postReferences.addValueEventListener(postListen);
        tripListener = listen;
        postListener = postListen;
        ValueEventListener deleteListen = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snaps : dataSnapshot.getChildren()) {
                        if(snaps.child(user.getUid()).exists()){
                            isInvisible.add(snaps.getKey());
                        }
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            setPostImage(filePath.toString());
            postButton.setEnabled(true);
           // setImage(filePath.toString());
            Toast.makeText(this, "Don't forget to hit send to upload image", Toast.LENGTH_SHORT).show();

        }
    }
    private void setTripImage(String url){
        if(url != null && !url.equals("")) {
            Picasso.with(this).load(url).into(image);
        }
    }
    private void setPostImage(String url){
        if(url != null && !url.equals("")){
            postImage.setVisibility(View.VISIBLE);
            Picasso.with(this).load(url).into(postImage);
        }
    }
    private void hidePostImage(){
        postImage.setVisibility(View.GONE);
    }

}
