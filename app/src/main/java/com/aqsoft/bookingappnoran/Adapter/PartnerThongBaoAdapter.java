package com.aqsoft.bookingappnoran.Adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aqsoft.bookingappnoran.AI.OnDataReceivedCallback;
import com.aqsoft.bookingappnoran.AI.SimilarityClassifier;
import com.aqsoft.bookingappnoran.AI.TestActivity;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.model.Partnerthongbaomodel;
import com.aqsoft.bookingappnoran.model.Thongbaomodel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class PartnerThongBaoAdapter extends RecyclerView.Adapter<PartnerThongBaoAdapter.MyViewHolder> {
    private static List<Partnerthongbaomodel> mData;

    public PartnerThongBaoAdapter(List<Partnerthongbaomodel> data) {
        this.mData = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_thongbaopartner, parent, false);
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
        private TextView khachsan, loaiphong, ngaytraphong, ngaynhanphong, tongtien, nameks,email,phone;
        private ImageView imageView2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            khachsan = itemView.findViewById(R.id.khachsan);
            loaiphong = itemView.findViewById(R.id.loaiphong);
            ngaytraphong = itemView.findViewById(R.id.ngaytraphong);
            ngaynhanphong = itemView.findViewById(R.id.ngaynhanphong);
            tongtien = itemView.findViewById(R.id.tongtien);
            nameks = itemView.findViewById(R.id.nameks);
            imageView2 = itemView.findViewById(R.id.imageView2);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);



        }

        public void bindData(Partnerthongbaomodel data) {
            ngaytraphong.setText("Ngày trả phòng: " + data.getDate2());
            ngaynhanphong.setText("Ngày nhận phòng: " + data.getDate1());
            int number = Integer.parseInt(data.getTongtien());
            NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            formatter.setMinimumFractionDigits(0);
            String formattedNumber = formatter.format(number) + " VNĐ";
            tongtien.setText("Tổng tiền: " + formattedNumber);
            nameks.setText("Khách sạn: " + data.getNameKS());
            gettrangthai(data.getUseruid());
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TaiKhoan");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(data.getUseruid()).exists()) {
                        phone.setText("Số điện thoại: " + snapshot.child(data.getUseruid()).child("sodienthoai").getValue(String.class));
                        email.setText("Email: " + snapshot.child(data.getUseruid()).child("email").getValue(String.class));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XacminhAI(data.getUseruid(), data.getID());
                }
            });



        }

        private void XacminhAI(String useruid,String ID) {
            Intent intent = new Intent(itemView.getContext(), TestActivity.class);
            intent.putExtra("useruid", useruid);
            intent.putExtra("ID", ID);
            itemView.getContext().startActivity(intent);
        }


        private void gettrangthai(String uid) {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Thongtin");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(uid).exists()) {
                        imageView2.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

}

