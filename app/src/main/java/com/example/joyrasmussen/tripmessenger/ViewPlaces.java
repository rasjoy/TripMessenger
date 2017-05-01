package com.example.joyrasmussen.tripmessenger;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewPlaces extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,  GoogleMap.OnInfoWindowLongClickListener{

    private GoogleMap mMap;
    private ArrayList<Marker> myPlaces;
    private ArrayList<TripPlace> placesArray;
    Trip trip;
    private String tripID;
    private DatabaseReference tripPlaceReference, mDatabase;
    private ValueEventListener tripPlaceListener;
    LatLngBounds.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_places);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        tripID = getIntent().getStringExtra("tripID");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("trips").child(tripID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                trip = dataSnapshot.getValue(Trip.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        placesArray = new ArrayList<>();
        myPlaces = new ArrayList<>();
        builder  = new LatLngBounds.Builder();
        tripPlaceReference = mDatabase.child("tripPlaces").child(tripID);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        tripPlaceListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    mMap.clear();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        TripPlace place = snap.getValue(TripPlace.class);
                        placesArray.add(place);
                        myPlaces.add(returnMarker(place));
                        builder.include(new LatLng(place.getLat(), place.getLongitude()));
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));
                }else{
                    if(trip != null){
                        builder.include(new LatLng(trip.getLat(), trip.getLongitude()));
                       builder.include(new LatLng(trip.getLat(), trip.getLongitude()+ .1));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tripPlaceReference.addValueEventListener(tripPlaceListener);
        super.onStart();
    }

    @Override
    protected void onPause() {
        if(tripPlaceListener != null){
            tripPlaceReference.removeEventListener(tripPlaceListener);
        }
        super.onPause();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowLongClickListener(this);
        Toast.makeText(this, "To delete a place, press and hold its info box.", Toast.LENGTH_SHORT).show();

        // Add a marker in Sydney and move the camera




    }


    public Marker returnMarker(TripPlace tripPlace){
        Marker marker =  mMap.addMarker( new MarkerOptions()
                .position(new LatLng(tripPlace.getLat(), tripPlace.getLongitude()))
                .title(tripPlace.getName()));
        marker.setTag(tripPlace);
        return marker;

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d( "onMarkerClick: ", "Clicking marker");
        return false;
    }

    @Override
    public void onInfoWindowLongClick(final Marker marker) {

        final TripPlace place = (TripPlace) marker.getTag();
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure you want to remove " + place.getName() + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tripPlaceReference.child(place.getId()).removeValue();
                        Toast.makeText(ViewPlaces.this, place.getName()+  " was successfully removed", Toast.LENGTH_SHORT).show();
                        placesArray.remove(place);
                        myPlaces.remove(marker);
                        marker.remove();

                    }
                })
                .setNegativeButton("No", null);
        dialog.show();



    }
}
