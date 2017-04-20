package com.example.joyrasmussen.tripmessenger;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
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



    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mUserRetrival = (UserRetrival) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userID = mUserRetrival.returnUserID();
        addTripButton = (Button) getView().findViewById(R.id.newTripFragments);
        name = (TextView) getView().findViewById(R.id.nameFragment);
        image = (ImageView) getView().findViewById(R.id.profilePictureFrag);
        recyclerView = (RecyclerView) getView().findViewById(R.id.fragmentRecylerView);
        user = mUserRetrival.returnFUser();

       populateDatabase();

        populateUserTrips();

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
        if(!imageURL.equals("") || imageURL != null) {
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
                final DatabaseReference savedRef = getRef(position);
                final String key = savedRef.getKey();


                if(user.getUid().equals( model.getCreator())){
                    viewHolder.setColor();
                    viewHolder.youOwn();
                    Log.d( "populateViewHolder: ", "it sees that you have a trip");

                }
                viewHolder.setName(model.getName());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ViewTripActivity.class);
                        intent.putExtra("tripID", model.getId());
                        startActivity(intent);
                    }
                });
                if(user.getUid().equals(userID)){
                    viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            tripsRef.child(key).removeValue();

                            return false;
                        }
                    });

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
        Log.d( "onActivityCreated: ", userID);
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                viewUser = dataSnapshot.getValue(User.class);
                Log.d( "onDataChange: ", viewUser.toString());
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
