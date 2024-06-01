package com.aqsoft.bookingappnoran.Adapter;

import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.User.SelectRoomActivity;
import com.aqsoft.bookingappnoran.model.Danhgiamodel;
import com.aqsoft.bookingappnoran.model.Khachsan;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;




public class DanhgiaAdapter extends RecyclerView.Adapter<DanhgiaAdapter.MyViewHolder> {
    private static List<Danhgiamodel> mData;

    public DanhgiaAdapter(List<Danhgiamodel> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_danhgia, parent, false);
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

        private TextView name;
        private TextView binhluan;
        private TextView sosao;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            binhluan = itemView.findViewById(R.id.binhluan);
            sosao = itemView.findViewById(R.id.sosao);


        }

        public void bindData(Danhgiamodel data) {
            name.setText(data.getUid());
            binhluan.setText(data.getComment());
            DatabaseReference ten = FirebaseDatabase.getInstance().getReference().child("TaiKhoan").child(data.getUid()).child("name");
            ten.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String ten1 = task.getResult().getValue().toString();
                    name.setText(ten1);
                    Long sosaoo = Long.parseLong(data.getRating());
                    sosao.setText(String.valueOf(sosaoo));
                }
            });

        }
    }
}


