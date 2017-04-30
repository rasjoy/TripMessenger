package com.example.joyrasmussen.tripmessenger;

import java.util.ArrayList;

/*
HW 09 Part A
Group 34
Robert Holt & Joy Rasmussen
 */
public class Message {
    long  time;
    String text, usrId, id, imageURL;

    public Message() {
    }

    public Message(long time, String text, String usrId, String id, String imageURL) {
        this.time = time;
        this.text = text;
        this.usrId = usrId;
        this.id = id;
        this.imageURL = imageURL;

    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsrId() {
        return usrId;
    }

    public void setUsrId(String usrId) {
        this.usrId = usrId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}
