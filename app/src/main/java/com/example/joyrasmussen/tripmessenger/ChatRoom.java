package com.example.joyrasmussen.tripmessenger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatRoom extends AppCompatActivity {
    private String chatID;
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference tripReference;
    DatabaseReference postReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        chatID = getIntent().getStringExtra("ChatID");
        tripReference = mDatabase.child(chatID);
        postReferences = tripReference.child("posts");




    }

}
