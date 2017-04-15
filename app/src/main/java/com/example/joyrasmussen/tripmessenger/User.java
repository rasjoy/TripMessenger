package com.example.joyrasmussen.tripmessenger;


public class User {

    String firstName, lastName, gender, imageURL;

    public User(){

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
