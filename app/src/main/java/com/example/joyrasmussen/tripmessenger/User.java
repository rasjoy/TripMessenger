package com.example.joyrasmussen.tripmessenger;


import java.util.List;

public class User {

    String firstName, lastName, gender, imageURL;
    List<String> friends; //Current friends
    List<String> approval; //Friends you need to approve
    List<String> pending; //Friends that have to approve you

    public User(){
        firstName = lastName = gender = imageURL = "";
    }

    public User(String firstName, String lastName, String gender, String imageURL) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.imageURL = imageURL;
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
}
