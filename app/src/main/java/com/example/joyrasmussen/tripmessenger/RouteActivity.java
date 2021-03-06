package com.example.joyrasmussen.tripmessenger;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.joyrasmussen.tripmessenger.MainActivity.mGoogleApiClient;

public class RouteActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    DatabaseReference dbRef;
    final String key = "AIzaSyAHZi6Y43oMrw_Q7D7j9h2T-XcrmMU4Z60";

    LocationManager locationManager;
    String provider;
    Location location;

    Double lat;
    Double lng;

    ArrayList<LatLng> markerPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        location = null;
        markerPoints = new ArrayList<>();


        try {
            location = locationManager.getLastKnownLocation(provider);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (location != null) {
            Log.i("Location:", "Found");

            lat = location.getLatitude();
            lng = location.getLongitude();


        } else {
            Log.i("Location:", "Not found");
        }

//        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&key=" + key;
//        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=Boston,MA&destination=Boston,MA&waypoints=Charlestown,MA|Lexington,MA&key=" + key;


        final ArrayList<String> places = new ArrayList<>();
        String tripID = getIntent().getStringExtra("tripID");
        dbRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tripRef = dbRef.child("tripPlaces").child(tripID);

        tripRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (final DataSnapshot thisSnapshot : dataSnapshot.getChildren()) {

                    places.add(thisSnapshot.child("lat").getValue().toString() + "," + thisSnapshot.child("longitude").getValue().toString());

                    LatLng c = new LatLng(Double.parseDouble(thisSnapshot.child("lat").getValue().toString()),Double.parseDouble(thisSnapshot.child("longitude").getValue().toString()));

                    markerPoints.add(c);
                }

                if (places.size() < 2) {
                    Toast.makeText(RouteActivity.this, "You need at least two locations!", Toast.LENGTH_SHORT).show();
                } else {
                    makeURL(places);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    public void makeURL(ArrayList<String> places) {

        String baseURL = "https://maps.googleapis.com/maps/api/directions/json?origin=";
        StringBuilder result = new StringBuilder();
        result.append(baseURL);

        ArrayList<String> idPlaces = new ArrayList();

        for(String p : places){
            idPlaces.add(p);
//             idPlaces.add("place_id:" + p);
        }

        if (places.size() == 1) {
            result.append(lat + "," + lng + "&destination=" + idPlaces.get(0));
        } else {

            result.append(lat + "," + lng + "&destination=" + lat + "," + lng+ "&waypoints=");
            places.remove(0);

            for (String p : idPlaces) {

                if (idPlaces.indexOf(p) == idPlaces.size() - 1) {
                    result.append(p);
                } else {
                    result.append(p + "|");
                }

            }

        }

        result.append("&key=" + key);

        Log.i("url: ", result.toString());
        DownloadTask task = new DownloadTask(this);
        task.execute(result.toString());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    public void drawPolyline(String l, Double NElat, Double NElng, Double SWlat, Double SWlng) {

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title("Start"));

        for(LatLng n : markerPoints){
            mMap.addMarker(new MarkerOptions()
                    .position(n));
        }

        List<LatLng> points = decodePoly(l);

        LatLngBounds bounds = new LatLngBounds(
                new LatLng(SWlat, SWlng), new LatLng(NElat, NElng));

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));

        for (int i = 0; i < points.size() - 1; i++) {
            LatLng src = points.get(i);
            LatLng dest = points.get(i + 1);


            Polyline line = mMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(src.latitude, src.longitude),
                            new LatLng(dest.latitude, dest.longitude)
                    ).width(8).color(Color.BLUE).geodesic(true)
            );
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
            poly.add(p);
        }
        return poly;
    }
}
