package com.kingori.traveldeals;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DealAdapter {

    // This class is used to describe how to bind the data to a single row
    public class DealViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
        }

        public void bind(TravelDeal deal) {
            tvTitle.setText(deal.getTitle());
        }
    }
}


/* The adapter is used to send data from the data source to the RecyclerView. It does this through an
object called ViewHolder. The ViewHolder describes an item of the RecyclerView. In this app, it will
be a single row of data containing deal information, and also contains information about its place within
the RecyclerView. Adapter and ViewHolder work together. The Adapter should subclass the ViewHolder to
load the views, and then bind data to the views. ViewHolders get cached to make scrolling smoother,
because the findViewById method gets called only when an item is created, and not each time its updated.
 */