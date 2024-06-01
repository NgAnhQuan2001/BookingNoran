package com.aqsoft.bookingappnoran.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.model.Thongbaomodel;

import java.util.List;

public class AdminThongBaoAdapter extends RecyclerView.Adapter<AdminThongBaoAdapter.MyViewHolder> {
    private static List<Thongbaomodel> mData;

    public AdminThongBaoAdapter(List<Thongbaomodel> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lichsu, parent, false);
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
        private TextView khachsan,loaiphong,ngaytraphong,ngaynhanphong,tongtien;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            khachsan = itemView.findViewById(R.id.khachsan);
            loaiphong = itemView.findViewById(R.id.loaiphong);
            ngaytraphong = itemView.findViewById(R.id.ngaytraphong);
            ngaynhanphong = itemView.findViewById(R.id.ngaynhanphong);
            tongtien = itemView.findViewById(R.id.tongtien);


        }

        public void bindData(Thongbaomodel data) {
            khachsan.setText("Tên khách sạn: " +data.getNameKS());
            loaiphong.setText("Loại phòng: "+data.getRoom());
            ngaytraphong.setText("Ngày trả phòng: " +data.getDate2());
            ngaynhanphong.setText("Ngày nhận phòng: " +data.getDate1());
            tongtien.setText("Tổng tiền: " +data.getTongtien());

        }
    }
}

