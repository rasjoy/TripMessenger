package com.example.joyrasmussen.tripmessenger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class ManageFriends extends AppCompatActivity {

    ArrayList<String> friends;
    ArrayList<String> pending;
    ArrayList<String> approval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_friends);
    }
}
