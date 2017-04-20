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

    public Trip(String id) {
        this.id = id;
    }

    public Trip() {

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
