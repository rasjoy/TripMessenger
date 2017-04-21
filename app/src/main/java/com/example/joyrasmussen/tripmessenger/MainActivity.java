package com.example.joyrasmussen.tripmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.FragmentBase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements SignInFragment.OnFragmentInteractionListener,ViewTripFragment.OnFragmentInteractionListener, UserFragment.OnFragmentInteractionListener, UserRetrival, TripRetrival {
    FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    DatabaseReference userReference;
    DatabaseReference tripReference;
    DatabaseReference mDatabase;
    String usersViewing;
    String tripID;
     static final int SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        userReference = mDatabase.child("users");
        tripReference = mDatabase.child("trips");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d( "onAuthStateChanged: ", "signed in");
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(user.getUid())){
                                Log.d( "onDataChange: ", "user is in database");
                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.main_activity, new UserFragment(), "user")
                                        .addToBackStack(null).commit();


                            }else {
                                Toast.makeText(MainActivity.this, "Please complete your profile", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(MainActivity.this, EditProfile.class);
                                startActivity(i);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                        //start edit profile automatically
                } else {
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.main_activity, new SignInFragment(), "first" ).commit();
                }
            }
        };
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOutMainMenu:
                AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Sign out was successful", Toast.LENGTH_LONG).show();
                                } else {

                                }
                            }
                        });
                getSupportFragmentManager().popBackStack();

                return true;
            case R.id.mangageFriendsMain:
                Intent mf = new Intent(this, ManageFriends.class);
                startActivity(mf);
                return true;
            case R.id.editProfileMain:
                Intent i = new Intent(this, EditProfile.class);
                startActivity(i);
                return true;
            default:
                return true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(mAuthListener);
        userOnChangeListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
    }

    public void tripsChangeListener(){


    }
    public void userOnChangeListener(){
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    @Override
    public void startTripFragment(String id) {
        ViewTripFragment fragment = new ViewTripFragment();
        Bundle bundle = new Bundle();
        bundle.putString("TripID", id);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity, fragment, "trip")
                .addToBackStack(null).commit();
    }


    @Override
    public void startCLickedUserFragment(String id) {
        UserFragment fragment = new UserFragment();
        Bundle bundle = new Bundle();
        bundle.putString("UserID", id);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_activity, fragment, "user")
                .addToBackStack(null).commit();
    }





}
