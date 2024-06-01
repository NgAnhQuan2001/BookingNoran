package com.aqsoft.bookingappnoran.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aqsoft.bookingappnoran.User.DetailActivity;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.model.LoaiPhongmodel;
import com.bumptech.glide.Glide;

import java.util.List;

public class LoaiPhongAdapter extends RecyclerView.Adapter<LoaiPhongAdapter.MyViewHolder> {
    private static List<LoaiPhongmodel> mData;

    public LoaiPhongAdapter(List<LoaiPhongmodel> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loaiphong, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView room_name;
        private TextView room_mota;
        private ImageView room_image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            room_name = itemView.findViewById(R.id.room_name);
            room_mota = itemView.findViewById(R.id.room_mota);
            room_image = itemView.findViewById(R.id.room_image);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                LoaiPhongmodel clickedHotel = mData.get(position);
                Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                intent.putExtra("Key", String.valueOf(clickedHotel.getKey()));
                intent.putExtra("Room", String.valueOf(clickedHotel.getName()));
                itemView.getContext().startActivity(intent);
            });
        }

        public void bindData(LoaiPhongmodel data) {



            Glide.with(itemView.getContext()).load(data.getImage()).centerCrop().into(room_image);
            room_name.setText(data.getName());
            room_mota.setText(data.getMotangan());
        }
    }
}

