package com.example.joyrasmussen.tripmessenger;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joyrasmussen on 4/30/17.
 */

public class GetLocation extends AsyncTask<String, Void, List< Address>>{
    EditTripActivity mTripActivity;


    public GetLocation(EditTripActivity mTripActivity) {
        this.mTripActivity = mTripActivity;
    }

    @Override
    protected void onPostExecute(List<Address> addresses) {
        if(!addresses.isEmpty()) {
            mTripActivity.afterLocationRetrieved(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
        }else{
            mTripActivity.invalidTrip();

        }
        super.onPostExecute(addresses);
    }

    @Override
    protected List<Address> doInBackground(String... params) {
        Geocoder coder = new Geocoder(mTripActivity.getApplicationContext());
        List<Address> myAddresses = null;
        try {
            myAddresses = coder.getFromLocationName(params[0],3 );

        } catch (IOException e) {
            e.printStackTrace();
        }
        return myAddresses;
    }
}
