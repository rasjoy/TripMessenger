package com.example.joyrasmussen.tripmessenger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ManageFriends extends Fragment {

    UserRetrival mUserRetrival;

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
    ArrayList<String> approvePeople;
    ArrayList<String> friends;

    ArrayAdapter adapter;
    String currentView;

    HashMap<String, String> peopleToIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_manage_friends, container, false);
    }

    public ManageFriends() {
        // Required empty public constructor
    }
    public interface OnFragmentInteractionListener{

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mUserRetrival = (UserRetrival) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        pendingPeople = new ArrayList<String>();
        approvePeople = new ArrayList<String>();
        friends = new ArrayList<String>();
        peopleToIds = new HashMap<String, String>();

        friendsTab = (TextView) getView().findViewById(R.id.friendsTab);
        pendingTab = (TextView) getView().findViewById(R.id.pendingTab);
        approveTab = (TextView) getView().findViewById(R.id.approveTab);

        listView = (ListView) getView().findViewById(R.id.friendsListView);

        friendName = (EditText) getView().findViewById(R.id.addFriendET);

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String friendID;

                if(currentView.equals("Friends")) {
                    friendID = peopleToIds.get(friends.get(i));
                } else if (currentView.equals("Pending")){
                    friendID = peopleToIds.get(pendingPeople.get(i));
                } else {
                    friendID = peopleToIds.get(approvePeople.get(i));
                }


                mUserRetrival.startCLickedUserFragment(friendID);

            }
        });

        currentView = "Friends";
        setFriends();


    }

    public void setFriends() {

        if (currentView.equals("Friends")) {
            adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, friends);
            adapter.setNotifyOnChange(true);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l) {

                    new AlertDialog.Builder(getContext())
                            .setMessage("Delete friend?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    String id = peopleToIds.get(friends.get(index));
                                    dbRef.child("friends").child(userID).child(id).removeValue();
                                    dbRef.child("friends").child(id).child(userID).removeValue();

                                    friends.remove(index);
                                    adapter.notifyDataSetChanged();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }).create().show();

                    return true;
                }
            });
        }

        final DatabaseReference approveRef = dbRef.child("friends").child(userID);

        approveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {
                    userReference.child(thisSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds) {

                            User userObject = ds.getValue(User.class);
                            String name = userObject.getFullName().replace(" ", "");
                            if (!friends.contains(name)) {
                                friends.add(name);
                                adapter.notifyDataSetChanged();
                            }

                            if (!peopleToIds.containsKey(name)) {
                                peopleToIds.put(name, thisSnapshot.getKey());
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
            adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, approvePeople);
            adapter.setNotifyOnChange(true);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int index, long l) {

                new AlertDialog.Builder(getContext())
                        .setMessage("Accept friend request?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                String friendID = peopleToIds.get(approvePeople.get(index));
                                dbRef.child("friends").child(userID).child(friendID).setValue("true");
                                dbRef.child("friends").child(friendID).child(userID).setValue("true");

                                dbRef.child("approval").child(userID).child(friendID).removeValue();

                                dbRef.child("pending").child(friendID).child(userID).removeValue();

                                approvePeople.remove(index);
                                adapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        }).create().show();

                return true; //Necessary
            }
        });

        final DatabaseReference approveRef = dbRef.child("approval").child(userID);

        approveRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {


                    userReference.child(thisSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds) {

                            User userObject = ds.getValue(User.class);
                            String name = userObject.getFullName().replace(" ", "");
                            if (!approvePeople.contains(name)) {
                                approvePeople.add(name);
                                adapter.notifyDataSetChanged();
                            }
                            if (!peopleToIds.containsKey(name)) {
                                peopleToIds.put(name, thisSnapshot.getKey());
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
            adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, pendingPeople);
            adapter.setNotifyOnChange(true);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    return true; //Necessary
                }
            });
        }

        DatabaseReference pendingRef = dbRef.child("pending").child(userID);

        pendingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {

                    userReference.child(thisSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot ds) {
                            User userObject = ds.getValue(User.class);
                            String name = userObject.getFullName().replace(" ", "");
                            if (!pendingPeople.contains(name)) {
                                pendingPeople.add(name);
                                adapter.notifyDataSetChanged();
                            }
                            if (!peopleToIds.containsKey(name)) {
                                peopleToIds.put(name, thisSnapshot.getKey());
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

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }

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

                    Toast.makeText(getContext(), "Friend request sent", Toast.LENGTH_SHORT).show();
                    friendName.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void search(View v) {

        Intent i = new Intent(getContext(), FindFriends.class);
        startActivity(i);
    }

    public void changeTabs(String choice) {

        switch (choice) {
            case "Friends":
                currentView = "Friends";
                setFriends();
                friendsTab.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                pendingTab.setTextColor(Color.BLACK);
                approveTab.setTextColor(Color.BLACK);

                break;
            case "Pending":
                currentView = "Pending";
                setPending();
                pendingTab.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                friendsTab.setTextColor(Color.BLACK);
                approveTab.setTextColor(Color.BLACK);

                break;
            case "Approve":
                currentView = "Approve";
                setApprove();
                approveTab.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                friendsTab.setTextColor(Color.BLACK);
                pendingTab.setTextColor(Color.BLACK);

                break;
        }
    }
}
