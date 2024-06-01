package com.aqsoft.bookingappnoran.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.User.SelectRoomActivity;
import com.aqsoft.bookingappnoran.model.Khachsan;
import com.bumptech.glide.Glide;

import java.util.List;

public class KhachsanAdapter extends RecyclerView.Adapter<KhachsanAdapter.MyViewHolder> {
    private static List<Khachsan> mData;

    public KhachsanAdapter(List<Khachsan> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_khachsan, parent, false);
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
        private ImageView imageView;
        private TextView text_name;
        private TextView text_rating_value;
        private TextView text_address;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            text_name = itemView.findViewById(R.id.text_name);
            text_rating_value = itemView.findViewById(R.id.text_rating_value);
            text_address = itemView.findViewById(R.id.text_address);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    Khachsan clickedHotel = mData.get(position);

                    Intent intent = new Intent(itemView.getContext(), SelectRoomActivity.class);
                    intent.putExtra("Key", clickedHotel.getKey());
                    itemView.getContext().startActivity(intent);
                }
            });
        }

        public void bindData(Khachsan data) {
            int desiredWidth = 500;
            int desiredHeight = 700;


            Glide.with(itemView.getContext()).load(data.getImage()).override(desiredWidth, desiredHeight).centerCrop().into(imageView);
            text_name.setText(data.getName());
            text_rating_value.setText(" " + data.getRating());
            text_address.setText(data.getAddress());
        }
    }
    public void filterList(List<Khachsan> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}

