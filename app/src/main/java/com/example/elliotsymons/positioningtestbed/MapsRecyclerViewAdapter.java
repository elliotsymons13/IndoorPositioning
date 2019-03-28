package com.example.elliotsymons.positioningtestbed;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.elliotsymons.positioningtestbed.MapManagement.Map;
import com.example.elliotsymons.positioningtestbed.MapManagement.MapData;

import java.util.List;

public class MapsRecyclerViewAdapter extends RecyclerView.Adapter<MapsRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "MapsRecyclerViewAdapter";

    private List<MapData> data;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    MapsRecyclerViewAdapter(Context context, List<MapData> data) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MapData item = data.get(position);
        holder.nameTextView.setText(item.getName());
        holder.filepathTextView.setText(item.getMapURI());
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTextView, filepathTextView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_mapName);
            filepathTextView = itemView.findViewById(R.id.tv_mapLocation);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    MapData getItem(int id) {
        return data.get(id);
    }

    List<MapData> getList() {
        return data;
    }

    void setList(List<MapData> list) {
        this.data = list;
    }

    public void addItem(MapData map) {
        data.add(map);
        Log.d(TAG, "addItem: Added new map to adapter list");
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}