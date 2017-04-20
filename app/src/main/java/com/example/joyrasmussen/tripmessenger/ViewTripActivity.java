package com.example.joyrasmussen.tripmessenger;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.fitness.data.Value;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTripActivity extends AppCompatActivity implements UserFragment.OnFragmentInteractionListener,  UserRetrival{
    FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    LinearLayoutManager mLayoutManager;
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
    ValueEventListener memberListener;
    List<Object> whoIsAMember = new ArrayList<>();
    boolean isOwner;
    DatabaseReference membersReference;
    FirebaseRecyclerAdapter<User, UserPopulateHolder> mAdapter;
    Query query;
    String viewUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_trip);
        tripID = getIntent().getStringExtra("tripID");
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();

        location = (TextView) findViewById(R.id.locationTripView);
        name = (TextView) findViewById(R.id.tripNameViewTrip);
        editTrip = (Button) findViewById(R.id.editTripButton);
        enterChatroom = (Button) findViewById(R.id.enterChatTripView);
        members = (RecyclerView) findViewById(R.id.tripMemberRecycler);
        owner = (TextView) findViewById(R.id.ownerETtripView);
        image = (ImageView) findViewById(R.id.tripViewImage);

        tripReference = mDatabase.child("trips").child(tripID);
        membersReference = mDatabase.child("tripMembers").child(tripID);
        userReference = mDatabase.child("users");
        findViewById(R.id.elements).setVisibility(View.VISIBLE);



    }
    private void populateEverything(){


        populateImage(thisTrip.getPhoto());




    }
    public void populateList(){
        query = userReference.orderByKey();
        mAdapter = new FirebaseRecyclerAdapter<User, UserPopulateHolder>(User.class, R.layout.trip_members, UserPopulateHolder.class, query) {
            @Override
            protected void populateViewHolder(UserPopulateHolder viewHolder, final User model, int position) {

                Log.d( "populateViewHolder: ", whoIsAMember.toString() + "\n " + model.getId());
                if(whoIsAMember.contains(model.getId())) {
                    Log.d("whoIsMember ", model.toString());
                    viewHolder.layout.setVisibility(View.VISIBLE);
                    viewHolder.isPart(model.getFirstName(), model.getLastName(), model.getImageURL());
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewUser = model.getId();
                            findViewById(R.id.elements).setVisibility(View.INVISIBLE);
                            getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.viewTrip, new UserFragment(), "user")
                                        .addToBackStack(null).commit();

                        }
                    });



                }else{
                    RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)viewHolder.itemView.getLayoutParams();
                    param.width = 0;
                    param.height = 0;
                    viewHolder.itemView.setLayoutParams(param);


                }
            }
        };
        mLayoutManager = new LinearLayoutManager(this);
        members.setHasFixedSize(false);
        members.setLayoutManager(mLayoutManager);
        members.setAdapter(mAdapter);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

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
        findViewById(R.id.elements).setVisibility(View.VISIBLE);
        authListener();
        auth.addAuthStateListener(mAuthListener);
        super.onStart();

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        userReference.addValueEventListener(userListener);
        ValueEventListener listen= new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                thisTrip = dataSnapshot.getValue(Trip.class);

                location.setText(thisTrip.getLocation());
                name.setText(thisTrip.getName());
                final String creator =thisTrip.getCreator();
                if(creator.equals(user.getUid())){
                    owner.setText("You");
                    isOwner = true;
                }else{

                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User creater = dataSnapshot.child(creator).getValue(User.class);
                            owner.setText(creater.getFirstName() + " " + creater.getLastName());
                            isOwner= false;
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
               if(!whoIsAMember.isEmpty()){
                   populateEverything();
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tripReference.addValueEventListener(listen);
        tripListener = listen;
        ValueEventListener listenMembers = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    Map<String, Boolean> td = (HashMap<String,Boolean>) dataSnapshot.getValue();
               // Log.d("members", td.toString());
                   if(td != null){
                       whoIsAMember = Arrays.asList(td.keySet().toArray());
                       Log.d("Memebers", whoIsAMember.toString());
                       populateList();
                   }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        membersReference.addValueEventListener(listenMembers);
        memberListener = listenMembers;



    }

    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.elements).setVisibility(View.VISIBLE);
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

    private void populateImage(String url){
        if(url != null && !url.equals("")) {

            Picasso.Builder builder = new Picasso.Builder(image.getContext());
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


    @Override
    protected void onStop() {
        super.onStop();
        if(memberListener != null){
            membersReference.removeEventListener(memberListener);
        }
        if(tripListener != null){
            tripReference.removeEventListener(tripListener);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(memberListener != null){
            membersReference.removeEventListener(memberListener);
        }
        if(tripListener != null){
            tripReference.removeEventListener(tripListener);
        }
    }

    public void onChatActivity(View v){
        Intent intent = new Intent(ViewTripActivity.this, ChatRoom.class);
        intent.putExtra("chatID", tripID);
        startActivity(intent);
   }

   public void onEditActivity(View v){
       Intent intent = new Intent(ViewTripActivity.this, EditTripActivity.class);
       intent.putExtra("tripID", tripID);
       startActivity(intent);
   }
    public String returnUserID(){
        return viewUser;

    }
    public FirebaseUser returnFUser(){
        return user;

    }

    public void tryToGetMembersagain(){
        membersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Boolean> td = (HashMap<String,Boolean>) dataSnapshot.getValue();
                // Log.d("members", td.toString());
                if(td != null) {
                    whoIsAMember = Arrays.asList(td.keySet().toArray());
                    Log.d("Memebers", whoIsAMember.toString());

                }

                }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        findViewById(R.id.elements).setVisibility(View.VISIBLE);
    }
}
