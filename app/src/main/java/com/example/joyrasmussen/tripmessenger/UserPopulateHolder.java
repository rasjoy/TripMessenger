package com.example.joyrasmussen.tripmessenger;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by joyrasmussen on 4/16/17.
 */

public class UserPopulateHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView name;

    public UserPopulateHolder(View itemView) {
        super(itemView);

        name = (TextView) itemView.findViewById(R.id.userNameMembers);
        image = (ImageView) itemView.findViewById(R.id.userImageMembers);
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

            Picasso.Builder builder = new Picasso.Builder(image.getContext());
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                    Log.i("uri: ", uri + "");
                }
            });
            builder.build().load(url).into(image);
        }
    }
}
