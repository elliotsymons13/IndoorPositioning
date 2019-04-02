package com.example.elliotsymons.positioningtestbed;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.elliotsymons.positioningtestbed.MapManagement.MapData;
import com.example.elliotsymons.positioningtestbed.MapManagement.MapManager;

import java.util.List;

public class MapsRecyclerViewAdapter extends RecyclerView.Adapter<MapsRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "MapsRecyclerViewAdapter";

    private List<MapData> data;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;
    private int selectedRow;
    MapManager mapManager;

    MapsRecyclerViewAdapter(Context context, List<MapData> data) {
        mapManager = MapManager.getInstance(context);
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        if (mapManager.getSelected() > data.size()) {
            selectedRow = RecyclerView.NO_POSITION;
            mapManager.setSelected(selectedRow);
        } else {
            selectedRow = mapManager.getSelected();
        }

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
        holder.contentView.setSelected(selectedRow == position);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return data.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTextView, filepathTextView;
        ConstraintLayout contentView;

        ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_mapName);
            filepathTextView = itemView.findViewById(R.id.tv_mapLocation);
            contentView = itemView.findViewById(R.id.contentView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) return;
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    MapData getItem(int id) {
        return data.get(id);
    }

    void setSelectedRow(int positionSelected) {
        if (positionSelected >= getItemCount())
            return;
        notifyItemChanged(selectedRow);
        selectedRow = positionSelected;
        mapManager.setSelected(selectedRow);
        notifyItemChanged(selectedRow);
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}