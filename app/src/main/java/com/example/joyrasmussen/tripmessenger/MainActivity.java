package com.example.joyrasmussen.tripmessenger;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ui.FragmentBase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
HW 09 Part A
Group 34
Robert Holt & Joy Rasmussen
 */
public class MainActivity extends AppCompatActivity implements ManageFriends.OnFragmentInteractionListener,SignInFragment.OnFragmentInteractionListener,ViewTripFragment.OnFragmentInteractionListener, UserFragment.OnFragmentInteractionListener, UserRetrival, TripRetrival, GoogleApiClient.OnConnectionFailedListener {
    FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    DatabaseReference userReference;
    static GoogleApiClient mGoogleApiClient;
    DatabaseReference tripReference;
    DatabaseReference mDatabase;
    String usersViewing;
    String tripID;
    GoogleSignInOptions gso;
     static final int SIGN_IN = 123;
    static final int PLACE_PICKER_REQUEST =12;

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

                    // getSupportFragmentManager().popBackStack("sign", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                    signinFragment();

                }
            }
        };
        auth.addAuthStateListener(mAuthListener);
        userOnChangeListener();
        set();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signOutTripViewMenu:
            case R.id.signOutMainMenu:
            signout();
              //  signinFragment();
                 //   mAuthListener.onAuthStateChanged(auth);
                /* AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    signinFragment();
                                    Toast.makeText(MainActivity.this, "Sign out was successful", Toast.LENGTH_LONG).show();
                                } else {

                                }
                            }
                        });*/


                return true;
            case R.id.edtiTripView:
                return false;
            case R.id.goToChatMenuTrip:
                return false;
            case R.id.tripAddPlace:
                return false;
            case R.id.viewRoute:
                return false;
            case R.id.mangageFriendsMain:
//                Intent mf = new Intent(this, ManageFriends.class);
//                startActivity(mf);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_activity, new ManageFriends(), "user")
                        .addToBackStack(null).commit();
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
    protected void onResumeFragments() {


        super.onResumeFragments();
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuthListener != null) {
            auth.removeAuthStateListener(mAuthListener);
        }
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

    private void signinFragment() {
        if (getSupportFragmentManager().findFragmentByTag("first") == null && getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Log.d("signinFragment: ", "yo");

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_activity, new SignInFragment(), "first").addToBackStack("sign").commit();
        }
    }


    @Override
    public GoogleApiClient getMyAPI() {

        return  mGoogleApiClient;
    }
    public void set(){
        if(mGoogleApiClient == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            // [END config_signin]

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */,
                            this /* OnConnectionFailedListener */)
                    .addApi(Auth.CREDENTIALS_API).addApi(Auth.GOOGLE_SIGN_IN_API, gso)

                    .build();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //Log.d( "onBackPressed: ", getSupportFragmentManager().getBackStackEntryCount() + "" );
        if (auth.getCurrentUser() == null) {
            finish();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            finish();
        }
    }
        public void signout(){
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        auth.signOut();
        Toast.makeText(this, "Sign out was successful", Toast.LENGTH_SHORT).show();

    }



    }

