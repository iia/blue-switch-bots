package com.iia.blueswitchbots;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BotsRecyclerAdapter extends RecyclerView.Adapter<BotsRecyclerAdapter.ViewHolder> {
    private ArrayList<Bot> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    BotsRecyclerAdapter(Context context, ArrayList<Bot> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Components of the item view to be updated or manipulated.
        TextView name;
        TextView description;

        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_view_name);
            description = itemView.findViewById(R.id.text_view_address);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.bots_list_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bot bot = mData.get(position);
        holder.name.setText(bot.getmName());
        holder.description.setText(bot.getmAddress());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
