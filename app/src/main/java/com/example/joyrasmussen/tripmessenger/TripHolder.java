package com.example.joyrasmussen.tripmessenger;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by joyrasmussen on 4/19/17.
 */

public class TripHolder extends RecyclerView.ViewHolder {
   TextView name, owner;
    ImageView image;
    RelativeLayout tripLayout;

    public TripHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.tripImageList);
        name = (TextView) itemView.findViewById(R.id.trip_name);
        owner = (TextView) itemView.findViewById(R.id.ownerNameTripList);
        tripLayout = (RelativeLayout) itemView.findViewById(R.id.tripListLayout);
    }

    public void setName(String n){
        name.setText(n);
    }

    public void setColor(){
        tripLayout.setBackgroundResource(R.color.mBlue);

    }
    public void setImage(String url){
        if(!url.equals("") || url != null)
        Picasso.with(image.getContext()).load(url).into(image);
    }

    public void setOwner(String first, String last){
        owner.setText(first + " " + last);
    }
    public void youOwn(){
        owner.setText("You!");
    }




}
