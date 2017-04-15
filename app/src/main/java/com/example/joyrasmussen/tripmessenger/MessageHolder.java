package com.example.joyrasmussen.tripmessenger;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by joyrasmussen on 4/15/17.
 */

public class MessageHolder extends RecyclerView.ViewHolder {
    public MessageHolder(View itemView) {
        super(itemView);
    }
    public interface ClickListener{
        public void onItemClick(View view, int position);
    }

}
