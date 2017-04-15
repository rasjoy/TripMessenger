package com.example.joyrasmussen.tripmessenger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private static final int SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       auth = FirebaseAuth.getInstance();
        isLoggedIn();
    }


    public void isLoggedIn(){
        if (auth.getCurrentUser() != null) {
            // already signed in
        } else {
            // not signed in
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                            .build(),
                    SIGN_IN);
        }



    }
}
