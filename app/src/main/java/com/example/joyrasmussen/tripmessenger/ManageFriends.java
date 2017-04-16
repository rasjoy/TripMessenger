package com.example.joyrasmussen.tripmessenger;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
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

    ListView listView;

    TextView friendsTab, approveTab, pendingTab;

    FirebaseListAdapter<User> adapter;

    String currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);

        currentView = "friends";

        friendsTab = (TextView) findViewById(R.id.friendsTab);
        pendingTab = (TextView) findViewById(R.id.pendingTab);
        approveTab = (TextView) findViewById(R.id.approveTab);

        listView = (ListView) findViewById(R.id.friendsListView);

        friendName = (EditText) findViewById(R.id.addFriendET);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        user = FirebaseAuth.getInstance().getCurrentUser();
        userObject = new User();
        userID = user.getUid();
        userReference = mDatabase.child("users");

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getUid())) {
                    userObject = dataSnapshot.child(userID).getValue(User.class);

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        View.OnClickListener tabClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView v = (TextView) view;
                changeTabs(v.getText().toString());
            }
        };
        friendsTab.setOnClickListener(tabClick);
        approveTab.setOnClickListener(tabClick);
        pendingTab.setOnClickListener(tabClick);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");

        adapter = new FirebaseListAdapter<User>(this, User.class, android.R.layout.simple_list_item_1, ref) {
            @Override
            protected void populateView(View view, User user, int position) {

                ((TextView)view.findViewById(android.R.id.text1)).setText(user.getFullName());

            }
        };

        listView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapter.cleanup();
    }


    public void add(View v) {

        String name = friendName.getText().toString().replace(" ", "_");

        Query q = userReference.orderByChild("fullName").equalTo(name);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {

                    //put this user's ID in their friend's approval stuff
                    mDatabase.child("approval").child(thisSnapshot.getKey()).child(userID).setValue("true");

                    //put the friends ID in this user's pending stuff
                    mDatabase.child("pending").child(userID).child(thisSnapshot.getKey()).setValue("true");

                    Toast.makeText(ManageFriends.this, "Friend request sent", Toast.LENGTH_SHORT).show();
                    friendName.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void search(View v){

        Intent i = new Intent(this, FindFriends.class);
        startActivity(i);
    }

    public void changeTabs(String choice){

        switch (choice) {
            case "Friends":
                currentView = "Friends";
                friendsTab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.authui_colorAccent));
                pendingTab.setTextColor(Color.BLACK);
                approveTab.setTextColor(Color.BLACK);

                break;
            case "Pending":
                currentView = "Pending";
                pendingTab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.authui_colorAccent));
                friendsTab.setTextColor(Color.BLACK);
                approveTab.setTextColor(Color.BLACK);

                break;
            case "Approve":
                currentView = "Approve";
                approveTab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.authui_colorAccent));
                friendsTab.setTextColor(Color.BLACK);
                pendingTab.setTextColor(Color.BLACK);

                break;
        }
    }
}
