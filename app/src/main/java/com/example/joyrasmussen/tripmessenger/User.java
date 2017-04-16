package com.example.joyrasmussen.tripmessenger;


import java.util.ArrayList;
import java.util.List;

public class User {

    String id, firstName, lastName, gender, imageURL, fullName;
    List<String> friends; //Current friends
    List<String> approval; //Friends you need to approve
    List<String> pending; //Friends that have to approve you

    public User(){
        firstName = lastName = gender = imageURL = fullName = "";
    }

    public User(String firstName, String lastName, String gender, String imageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.imageURL = imageURL;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<String> getApproval() {
        return approval;
    }

    public void setApproval(List<String> approval) {
        this.approval = approval;
    }

    public List<String> getPending() {
        return pending;
    }

    public void setPending(List<String> pending) {
        this.pending = pending;
    }
}
