package com.example.joyrasmussen.tripmessenger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joyrasmussen on 4/15/17.
 */

public class Trip {
    String id;
    String name;
    String location;
    String photo;
    String creator;
    private double lat, longitude;

    public Trip(String id) {
        this.id = id;
    }

    public Trip() {

    }

    public Trip(String id, String name, String location, String photo, String creator, double lat, double longitude) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.photo = photo;
        this.creator = creator;
        this.lat = lat;
        this.longitude = longitude;
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

    public Trip(String id, String name, String location, String photo, String creator) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.photo = photo;
        this.creator = creator;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }


}
