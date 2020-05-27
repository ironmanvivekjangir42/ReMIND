package com.example.remind;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recyclerViewAdapter extends RecyclerView.Adapter<recyclerViewAdapter.recyclerViewHolder> {
    private ArrayList<Data> mlist;
    private OnItemClickedListener mListener;

    public interface OnItemClickedListener{
        void onDeleteClick(int position);
        void onFavClick(int position);
        void onEditClick(int position);
    }

    public void setOnClickListener(OnItemClickedListener listener){
        mListener = listener;
    }


    public static class recyclerViewHolder extends RecyclerView.ViewHolder{
        public TextView itemCV;
        public TextView locationCV;
        public ImageView delete;
        public ImageView favorite;
        public ImageView edit;

        public recyclerViewHolder(@NonNull View itemView, OnItemClickedListener listener) {
            super(itemView);
            itemCV = itemView.findViewById(R.id.item_name_card_view);
            locationCV = itemView.findViewById(R.id.item_location_card_view);
            delete = itemView.findViewById(R.id.delete_card_view);
            favorite = itemView.findViewById(R.id.favorites_card_view);
            edit = itemView.findViewById(R.id.edit_card_view);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.onFavClick(position);
                        }
                    }
                }
            });
            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener!=null){
                        int position = getAdapterPosition();
                        if(position!= RecyclerView.NO_POSITION){
                            listener.onEditClick(position);
                        }
                    }
                }
            });
        }
    }

    public recyclerViewAdapter(ArrayList<Data> list){
        mlist = list;
    }

    @NonNull
    @Override
    public recyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        recyclerViewHolder rvh = new recyclerViewHolder(v, mListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull recyclerViewHolder holder, int position) {
        Data currentItem = mlist.get(position);
        holder.itemCV.setText(currentItem.getItem());
        holder.locationCV.setText(currentItem.getLocation());
        if(currentItem.getFav()){
            holder.favorite.setImageResource(R.drawable.ic_favorite);
        }
        else{
            holder.favorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }
}
