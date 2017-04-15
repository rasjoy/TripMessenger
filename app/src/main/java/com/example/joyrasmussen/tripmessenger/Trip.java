package com.example.joyrasmussen.tripmessenger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by joyrasmussen on 4/15/17.
 */

public class Trip {
    long id;
    String name;
    String location;
    String photo;
    String creator;
    ArrayList<String> members;
    HashMap<String, Message> posts;

    public Trip(long id, String name, String location, String photo, String creator) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.photo = photo;
        this.creator = creator;
        posts = new HashMap<>();
        members = new ArrayList<>();
        members.add(creator);
    }
    

}
