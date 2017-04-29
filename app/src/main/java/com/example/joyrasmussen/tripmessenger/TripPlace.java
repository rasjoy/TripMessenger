package com.example.joyrasmussen.tripmessenger;

/**
 * Created by joyrasmussen on 4/29/17.
 */

public class TripPlace {
    private String id, name;
    private double lat, longitude;

    public TripPlace() {
    }

    public TripPlace(String id, String name, double lat, double longitude) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
