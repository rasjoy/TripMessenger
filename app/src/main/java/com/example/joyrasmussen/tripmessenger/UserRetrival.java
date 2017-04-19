package com.example.joyrasmussen.tripmessenger;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by joyrasmussen on 4/17/17.
 */

public interface UserRetrival {
    public String returnUserID();
    public FirebaseUser returnFUser();
}
