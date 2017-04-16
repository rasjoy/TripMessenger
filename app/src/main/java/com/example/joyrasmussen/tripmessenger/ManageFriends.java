package com.example.joyrasmussen.tripmessenger;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ManageFriends extends AppCompatActivity {

    ArrayList<String> friends;
    ArrayList<String> pending;
    ArrayList<String> approval;

    EditText friendName;
    User userObject;
    String userID;

    private DatabaseReference mDatabase, userReference;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);

        friendName = (EditText) findViewById(R.id.addFriendET);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userObject = new User();
        userID = user.getUid();
        userReference = mDatabase.child("users");

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getUid())) {
                    userObject = dataSnapshot.child(userID).getValue(User.class);

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void add(View v) {

        String name = friendName.getText().toString().replace(" ", "_");

        Query q = userReference.orderByChild("fullName").equalTo(name);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {

                    List<String> list = new ArrayList<String>();
                    list.add(userID);

                    mDatabase.child(thisSnapshot.getKey()).child("approval").push().setValue(list);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//


    }
}
