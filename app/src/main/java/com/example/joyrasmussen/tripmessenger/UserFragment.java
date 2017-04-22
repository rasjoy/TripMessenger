package com.example.joyrasmussen.tripmessenger;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
    MenuItem editProfItem;
    String userID;
    UserRetrival mUserRetrival;
   RecyclerView.LayoutManager mLayoutManager;
    Button addTripButton;
   // User currentUser;
    User viewUser;
    FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mUserRef;
    DatabaseReference tripsRef;
    DatabaseReference mDatabase;
    DatabaseReference usersMemberTrips;
    DatabaseReference usersOwnerTrips;
    DatabaseReference tripUserRef;
    FirebaseUser user;
    TextView name;
    ImageView image;
    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Trip, TripHolder> fireAdapter;
    Query query;
    ArrayList<String> usersTrips;




    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
       // Log.d("testingOrder", "onCreateView:");
        return inflater.inflate(R.layout.fragment_user, container, false);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
      //  Log.d("testingOrder", "Onattached:");
        try{
            mUserRetrival = (UserRetrival) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }


    }
    private void authListener(){

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user == null) {
                   // Log.d( "onAuthStateChanged: ", "signed in");
                    ((MainActivity) getActivity()).getSupportFragmentManager().popBackStack();

                    //start edit profile automatically
                }
            }
        };

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // Log.d("testingOrder","onActivityCreated: ");
        Bundle bundle = getArguments();
        auth = FirebaseAuth.getInstance();
        authListener();
        auth.addAuthStateListener(mAuthListener);
        user = auth.getCurrentUser();
        if(user == null){
            ((MainActivity) getActivity()).getSupportFragmentManager().popBackStack();
        }
        if(bundle != null){
           userID = bundle.getString("UserID");
       }else{
           userID = user.getUid();

       }
        usersTrips = new ArrayList<>();


        addTripButton = (Button) getView().findViewById(R.id.newTripFragments);
        name = (TextView) getView().findViewById(R.id.nameFragment);
        image = (ImageView) getView().findViewById(R.id.profilePictureFrag);
        recyclerView = (RecyclerView) getView().findViewById(R.id.fragmentRecylerView);



       populateDatabase();



    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       // Log.d("testingOrder", "onCreateOptionsMenu: ");

        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        editProfItem = menu.findItem(R.id.editProfileMain);

        if(!userID.equals(user.getUid())){
            editProfItem.setVisible(false);

        }

    }

    @Override
    public void onResume() {
        super.onResume();


        populateUserTrips();
    }


    public interface OnFragmentInteractionListener{


    }


    public void populateUserProfile(){
        name.setText(viewUser.getFirstName() + " " + viewUser.getLastName());
        String imageURL = viewUser.getImageURL();
        if(!imageURL.equals("") && imageURL != null) {
            Picasso.with(getContext()).load(imageURL).into(image);
        }
        if(!userID.equals(user.getUid())){
            addTripButton.setVisibility(View.GONE);
        }else{
            createNewTripButtion();

        }
    }
    public void populateUserTrips(){
        query = tripsRef.orderByChild("creator");
        fireAdapter = new FirebaseRecyclerAdapter<Trip, TripHolder>(Trip.class, R.layout.trip_list, TripHolder.class, query ) {
            @Override
            protected void populateViewHolder(TripHolder viewHolder, final Trip model, int position) {
                //need to update to filter based on trips that the current user is memeber of
                //add in logic of for colorcoding mutual trips
              //  Log.d("userstrips", usersTrips.toString());
                if(usersTrips.contains(model.getId())){
                final DatabaseReference savedRef = getRef(position);
                final String key = savedRef.getKey();
                viewHolder.setImage(model.getPhoto());

                if(user.getUid().equals( model.getCreator()) && user.getUid().equals(userID)){
                    viewHolder.setColor();
                    viewHolder.youOwn();
                    viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            tripsRef.child(key).removeValue();

                            return false;
                        }
                    });
                   // Log.d( "populateViewHolder: ",model.getName());

                }else if(user.getUid().equals( model.getCreator())){
                    viewHolder.youOwn();
                }
                viewHolder.setName(model.getName());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            mUserRetrival.startTripFragment( model.getId());
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
        mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLongClickable(true);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(fireAdapter);




    }
    public void populateDatabase(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserRef = mDatabase.child("users").child(userID);
        tripUserRef = mDatabase.child("users");
       // Log.d( "onActivityCreated: ", userID);
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewUser = dataSnapshot.getValue(User.class);
               // Log.d( "onDataChange: ", viewUser.toString());
                populateUserProfile();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tripsRef = mDatabase.child("trips");
        tripsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        usersMemberTrips = mDatabase.child("tripMembers");
        ValueEventListener membersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for( DataSnapshot snaps : dataSnapshot.getChildren()){
                    if(snaps.child(userID).exists()){
                       // Log.d("Adding", snaps.getKey());
                        usersTrips.add(snaps.getKey());
                    }
                }
                populateUserTrips();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        usersMemberTrips.addValueEventListener(membersListener);

    }

    public void createNewTripButtion(){
        addTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditTripActivity.class);
                intent.putExtra("tripID", "");
                startActivity(intent);
            }
        });
    }



}
