package com.example.joyrasmussen.tripmessenger;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class ViewTripFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
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
    UserRetrival mTripRetrival;



    public ViewTripFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_trip, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
        mTripRetrival = (UserRetrival) context;
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tripID = mTripRetrival.returnTripID();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        auth = FirebaseAuth.getInstance();

        location = (TextView) getView().findViewById(R.id.locationTripView);
        name = (TextView) getView().findViewById(R.id.tripNameViewTrip);
        editTrip = (Button) getView().findViewById(R.id.editTripButton);
        enterChatroom = (Button) getView().findViewById(R.id.enterChatTripView);
        members = (RecyclerView) getView().findViewById(R.id.tripMemberRecycler);
        owner = (TextView) getView().findViewById(R.id.ownerETtripView);
        image = (ImageView) getView().findViewById(R.id.tripViewImage);

        tripReference = mDatabase.child("trips").child(tripID);
        membersReference = mDatabase.child("tripMembers").child(tripID);
        userReference = mDatabase.child("users");
        getView().findViewById(R.id.elements).setVisibility(View.VISIBLE);


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
                    if(thisTrip.getCreator().equals(model.getId())){
                        viewHolder.setOwner();
                    }
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewUser = model.getId();
                            mTripRetrival.startCLickedUserFragment(viewUser);


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
        members.setHasFixedSize(false);
        members.setLayoutManager(mLayoutManager);
        members.setAdapter(mAdapter);

    }

    @Override
    public void onStart() {
        super.onStart();
        getView().findViewById(R.id.elements).setVisibility(View.VISIBLE);
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
                    editTrip.setVisibility(View.GONE);
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

    public interface OnFragmentInteractionListener{

    }

    private void populateEverything(){


        populateImage(thisTrip.getPhoto());


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
    public void onPause() {
        super.onPause();
        if(memberListener != null){
            membersReference.removeEventListener(memberListener);
        }
        if(tripListener != null){
            tripReference.removeEventListener(tripListener);
        }
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
                    ((MainActivity) getActivity()).getSupportFragmentManager().popBackStack();
                }
            }
        };

    }


}
