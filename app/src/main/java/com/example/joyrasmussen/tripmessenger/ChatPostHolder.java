package com.example.joyrasmussen.tripmessenger;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * Created by joyrasmussen on 4/16/17.
 */

public class ChatPostHolder extends RecyclerView.ViewHolder{
    ImageView image;
    TextView user, time, post;
    LinearLayout imageLayout;

    public ChatPostHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.chatRoomImage);
        imageLayout = (LinearLayout) itemView.findViewById(R.id.imageChatHolder);
        user = (TextView) itemView.findViewById(R.id.posterNameChat);
        time = (TextView) itemView.findViewById(R.id.uploadTime);
        post = (TextView) itemView.findViewById(R.id.postChatRoom);
    }
    public void setTime( long date) throws ParseException {
        time.setText(parseDate(date));

    }
    public void setUser(String userName){
        user.setText(userName);
    }
    public void setPost(String postText){
        if(postText != null && postText.equals("")){
            post.setVisibility(View.GONE);
        }else{
            post.setText(postText);

        }
    }
    public void setImage(String url){

        if(url != null && !url.equals("")) {
            Picasso.with(image.getContext()).load(url).into(image);

        }else{
            imageLayout.setVisibility(View.GONE);
        }

    }

    public static String parseDate(long date) throws ParseException {
        if(date != 0) {
            java.util.Date d = new java.util.Date(date);

            return getHowLongAgoDescription(d);
        }else{
            return "";
        }


    }
    public static String getHowLongAgoDescription(Date entered) {
        Locale locale = Locale.getDefault();
        PrettyTime p = new PrettyTime(locale);
        return p.format(entered);
    }

}
