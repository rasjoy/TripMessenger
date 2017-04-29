package com.example.joyrasmussen.tripmessenger;

/**
 * Created by joyrasmussen on 4/29/17.
 */

public class TripPlace {
    private String id, name;
    private long lat, longitude;

    public TripPlace() {
    }

    public TripPlace(String id, String name, long lat, long longitude) {
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

    public long getLat() {
        return lat;
    }

    public void setLat(long lat) {
        this.lat = lat;
    }

    public long getLongitude() {
        return longitude;
    }

    public void setLongitude(long longitude) {
        this.longitude = longitude;
    }
}
