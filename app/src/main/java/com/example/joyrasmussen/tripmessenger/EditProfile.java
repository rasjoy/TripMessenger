package com.example.joyrasmussen.tripmessenger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class EditProfile extends AppCompatActivity {

    EditText fnameET, lnameET, genderET;
    ImageView image;

    String fname, lname, gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fnameET = (EditText) findViewById(R.id.fNameEditText);
        lnameET = (EditText) findViewById(R.id.lNameEditText);
        genderET = (EditText) findViewById(R.id.genderProfileET);
    }

    public void update(View v){

        fname = fnameET.getText().toString();
        lname = lnameET.getText().toString();
        gender = genderET.getText().toString();

    }


    public void cancel(View v){
        finish();
    }
}
