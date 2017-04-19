package com.example.joyrasmussen.tripmessenger;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
    String userID;
    UserRetrival mUserRetrival;
    User currentUser;
    User viewUser;
    FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mUserRef;
    DatabaseReference tripsRef;
    DatabaseReference mDatabase;
    FirebaseUser user;
    TextView name;
    ImageView image;
    RecyclerView recyclerView;


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

        name = (TextView) getView().findViewById(R.id.nameFragment);
        image = (ImageView) getView().findViewById(R.id.profilePictureFrag);
        recyclerView = (RecyclerView) getView().findViewById(R.id.fragmentRecylerView);
        user = mUserRetrival.returnFUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserRef = mDatabase.child("users").child(userID);
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

    }
    public void populateUserTrips(){


    }

}
