package com.example.joyrasmussen.tripmessenger;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/*
HW 09 Part A
Group 34
Robert Holt & Joy Rasmussen
 */
public class MessageHolder extends RecyclerView.ViewHolder {
    public MessageHolder(View itemView) {
        super(itemView);
    }
    public interface ClickListener{
        public void onItemClick(View view, int position);
    }

}
