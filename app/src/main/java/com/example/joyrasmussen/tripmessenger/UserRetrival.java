package com.example.joyrasmussen.tripmessenger;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by joyrasmussen on 4/17/17.
 */

public interface UserRetrival {
    public String returnTripID();
    public String returnUserID();
    public FirebaseUser returnFUser();
    public void startTripFragment(String id);
    String getUserID();
    public void startCLickedUserFragment(String id);
}
