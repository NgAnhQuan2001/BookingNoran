package com.aqsoft.bookingappnoran.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.aqsoft.bookingappnoran.Partner.PartnerUpdateInfoRoomActivity;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.User.DetailActivity;
import com.aqsoft.bookingappnoran.model.LoaiPhongmodel;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PartnerLoaiPhongAdapter extends RecyclerView.Adapter<PartnerLoaiPhongAdapter.MyViewHolder> {
    private static List<LoaiPhongmodel> mData;

    public PartnerLoaiPhongAdapter(List<LoaiPhongmodel> data) {
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
        private ImageView lamtrongphong;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            room_name = itemView.findViewById(R.id.room_name);
            room_mota = itemView.findViewById(R.id.room_mota);
            room_image = itemView.findViewById(R.id.room_image);
            lamtrongphong = itemView.findViewById(R.id.lamtrongphong);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                LoaiPhongmodel clickedHotel = mData.get(position);
                Intent intent = new Intent(itemView.getContext(), PartnerUpdateInfoRoomActivity.class);
                intent.putExtra("Key", String.valueOf(clickedHotel.getKey()));
                intent.putExtra("Room", String.valueOf(clickedHotel.getName()));
                itemView.getContext().startActivity(intent);
            });
        }

        public void bindData(LoaiPhongmodel data) {
            Glide.with(itemView.getContext()).load(data.getImage()).centerCrop().into(room_image);
            room_name.setText(data.getName());
            room_mota.setText(data.getMotangan());
            lamtrongphong.setVisibility(View.VISIBLE);
            lamtrongphong.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle("Làm trống phòng");
                builder.setMessage("Bạn có chắc chắn muốn làm trống loại phòng này không?");
                builder.setPositiveButton("Có", (dialog, which) -> {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("KhachSan").child(data.getKey()).child("LoaiPhong").child(data.getName());
                    ref.child("Số lượng").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot ds : snapshot.getChildren()){

                                try {
                                    // Chuỗi ngày tháng từ dữ liệu của bạn
                                    String Date2 = ds.child("Date2").getValue(String.class);

                                    // Định dạng ngày tháng năm
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                                    // Chuyển chuỗi ngày tháng thành Date
                                    Date dateFromData = formatter.parse(Date2);

                                    // Lấy ngày hiện tại
                                    Calendar calendar = Calendar.getInstance();
                                    Date currentDate = calendar.getTime();

                                    // So sánh
                                    if (currentDate.after(dateFromData)) {
                                        ds.getRef().removeValue();
                                        ds.getRef().child("book").setValue("chuadat");

                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                            Toast.makeText(v.getContext(), "Làm trống phòng thành công", Toast.LENGTH_SHORT).show();
                        }



                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                });
                builder.setNegativeButton("Không", (dialog, which) -> {
                    dialog.dismiss();
                });
                builder.create().show();

            });
        }
    }
}

