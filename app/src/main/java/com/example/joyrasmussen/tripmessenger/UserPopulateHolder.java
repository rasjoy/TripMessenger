package com.example.joyrasmussen.tripmessenger;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
/*
HW 09 Part A
Group 34
Robert Holt & Joy Rasmussen
 */

public class UserPopulateHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView name, owner;
    RelativeLayout layout;

    public UserPopulateHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.userNameMembers);
        image = (ImageView) itemView.findViewById(R.id.userImageMembers);
        layout = (RelativeLayout) itemView.findViewById(R.id.tripMemberLayout);
        owner = (TextView) itemView.findViewById(R.id.tripOwnerMembers);
    }
    public void setOwner(){
            owner.setVisibility(View.VISIBLE);
    }

    public void setName(String first, String last){
        name.setText(first + " " + last);

    }
    public void isPart(String first, String last, String url){
        setName(first, last);
        setImage(url);


    }

    public void setImage(String url){

        if(url != null && !url.equals("")) {

            Picasso.with(image.getContext()).load(url).into(image);
        }
        else{
            image.setVisibility(View.GONE);
        }
    }
}
