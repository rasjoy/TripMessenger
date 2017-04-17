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

    ArrayList<String> pending;
    ArrayList<String> approval;

    EditText friendName;
    User userObject;
    String userID;

    private DatabaseReference userReference;
    FirebaseUser user;
    DatabaseReference dbRef;

    ListView listView;

    TextView friendsTab, approveTab, pendingTab;

    //    FirebaseListAdapter<User> adapter;
    ArrayList<String> pendingPeople;
    ArrayList<String> pendingPeopleIDs;
    ArrayList<String> approvePeople;
    ArrayList<String> approvePeopleIDs;
    ArrayList<String> friends;
    ArrayList<String> friendIDs;

    ArrayAdapter adapter;
    String currentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);

        pendingPeople = new ArrayList<String>();
        pendingPeopleIDs = new ArrayList<String>();
        approvePeople = new ArrayList<String>();
        approvePeopleIDs = new ArrayList<String>();
        friends = new ArrayList<String>();
        friendIDs = new ArrayList<String>();

        friendsTab = (TextView) findViewById(R.id.friendsTab);
        pendingTab = (TextView) findViewById(R.id.pendingTab);
        approveTab = (TextView) findViewById(R.id.approveTab);

        listView = (ListView) findViewById(R.id.friendsListView);

        friendName = (EditText) findViewById(R.id.addFriendET);

        dbRef = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userObject = new User();
        userID = user.getUid();
        userReference = dbRef.child("users");

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


        currentView = "Friends";
        setFriends();

    }

    public void setFriends(){

        if (currentView.equals("Friends")) {
            adapter = new ArrayAdapter<String>(ManageFriends.this, android.R.layout.simple_list_item_1, friends);
            adapter.setNotifyOnChange(true);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        final DatabaseReference approveRef = dbRef.child("friends").child(userID);

        approveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {

                    if (!friendIDs.contains(thisSnapshot.getKey())) {
                        friendIDs.add(thisSnapshot.getKey());
                    }

                    userReference.child(thisSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds) {

                            User userObject = ds.getValue(User.class);
                            String name = userObject.getFullName().replace(" ", "");
                            if (!friends.contains(name)) {
                                friends.add(name);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setApprove() {

        if (currentView.equals("Approve")) {
            adapter = new ArrayAdapter<String>(ManageFriends.this, android.R.layout.simple_list_item_1, approvePeople);
            adapter.setNotifyOnChange(true);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        final DatabaseReference approveRef = dbRef.child("approval").child(userID);

        approveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {

                    if (!approvePeopleIDs.contains(thisSnapshot.getKey())) {
                        approvePeopleIDs.add(thisSnapshot.getKey());
                    }

                    userReference.child(thisSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds) {

                            User userObject = ds.getValue(User.class);
                            String name = userObject.getFullName().replace(" ", "");
                            if (!approvePeople.contains(name)) {
                                approvePeople.add(name);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setPending() {

        if (currentView.equals("Pending")) {
            adapter = new ArrayAdapter<String>(ManageFriends.this, android.R.layout.simple_list_item_1, pendingPeople);
            adapter.setNotifyOnChange(true);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        DatabaseReference pendingRef = dbRef.child("pending").child(userID);

        pendingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {

                    if (!pendingPeopleIDs.contains(thisSnapshot.getKey())) {
                        pendingPeopleIDs.add(thisSnapshot.getKey());
                    }

                    userReference.child(thisSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds) {
                            User userObject = ds.getValue(User.class);
                            String name = userObject.getFullName().replace(" ", "");
                            if (!pendingPeople.contains(name)) {
                                pendingPeople.add(name);
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void add(View v) {

        String name = friendName.getText().toString().replace(" ", "_");

        Query q = userReference.orderByChild("fullName").equalTo(name);

        q.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {

                    //put this user's ID in their friend's approval stuff
                    dbRef.child("approval").child(thisSnapshot.getKey()).child(userID).setValue("true");

                    //put the friends ID in this user's pending stuff
                    dbRef.child("pending").child(userID).child(thisSnapshot.getKey()).setValue("true");

                    Toast.makeText(ManageFriends.this, "Friend request sent", Toast.LENGTH_SHORT).show();
                    friendName.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void search(View v) {

        Intent i = new Intent(this, FindFriends.class);
        startActivity(i);
    }

    public void changeTabs(String choice) {

        switch (choice) {
            case "Friends":
                currentView = "Friends";
                setFriends();
                friendsTab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                pendingTab.setTextColor(Color.BLACK);
                approveTab.setTextColor(Color.BLACK);

                break;
            case "Pending":
                currentView = "Pending";
                setPending();
                pendingTab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                friendsTab.setTextColor(Color.BLACK);
                approveTab.setTextColor(Color.BLACK);

                break;
            case "Approve":
                currentView = "Approve";
                setApprove();
                approveTab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                friendsTab.setTextColor(Color.BLACK);
                pendingTab.setTextColor(Color.BLACK);

                break;
        }
    }
}
