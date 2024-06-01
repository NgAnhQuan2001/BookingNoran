package com.aqsoft.bookingappnoran.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.aqsoft.bookingappnoran.ListHotelActivity;
import com.aqsoft.bookingappnoran.Partner.PartnerListRoomActivity;
import com.aqsoft.bookingappnoran.Partner.UpdateHotelActivity;
import com.aqsoft.bookingappnoran.User.SelectRoomActivity;
import com.aqsoft.bookingappnoran.model.Khachsan;
import com.aqsoft.bookingappnoran.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PartnerListHotelAdapter extends RecyclerView.Adapter<PartnerListHotelAdapter.HotelViewHolder> {

    private static List<Khachsan> hotelList;

    public PartnerListHotelAdapter(List<Khachsan> hotelList) {
        this.hotelList = hotelList;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listks, parent, false);
        return new HotelViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HotelViewHolder holder, int position) {
        holder.bindData(hotelList.get(position));
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    class HotelViewHolder extends RecyclerView.ViewHolder {
        public ImageView hotelImage;
        private ImageView dots;
        public TextView hotelName,quyks;
        public TextView ngayhoatdong;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            hotelImage = itemView.findViewById(R.id.hotel_image);
            hotelName = itemView.findViewById(R.id.hotel_name);
            ngayhoatdong = itemView.findViewById(R.id.ngayhoatdong);
            quyks = itemView.findViewById(R.id.quyks);
            dots = itemView.findViewById(R.id.dots);
            dots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Khachsan clickedHotel = hotelList.get(position);
                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        builder.setTitle("Lựa chọn hành động");
                        String[] options = {"Sửa", "Xóa"};
                        builder.setItems(options, (dialog, which) -> {
                            if (which == 1) {

                                AlertDialog.Builder builder10 = new AlertDialog.Builder(itemView.getContext());
                                builder10.setTitle("Xác nhận xóa");
                                builder10.setMessage("Bạn có chắc chắn muốn xóa khách sạn này?");
                                builder10.setPositiveButton("Có", (dialog1, which1) -> {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhachSan").child(clickedHotel.getKey());
                                    databaseReference.removeValue().addOnSuccessListener(aVoid -> {
                                        hotelList.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, hotelList.size());
                                    });
                                });
                                builder10.setNegativeButton("Không", (dialog12, which12) -> {
                                });
                                builder10.show();

                            } else if (which == 0) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(itemView.getContext());
                                builder1.setTitle("Lựa chọn hành động");
                                String[] options1 = {"Sửa thông tin khách sạn", "Sửa thông tin phòng"};
                                builder1.setItems(options1, (dialog1, which1) -> {
                                    if (which1 == 0) {
                                        Intent intent = new Intent(itemView.getContext(), UpdateHotelActivity.class);
                                        intent.putExtra("Key", clickedHotel.getKey());
                                        itemView.getContext().startActivity(intent);
                                    } else if (which1 == 1) {
                                        Intent intent = new Intent(itemView.getContext(), PartnerListRoomActivity.class);
                                        intent.putExtra("Key", clickedHotel.getKey());
                                        itemView.getContext().startActivity(intent);
                                    }
                                    else if (which1 == 2) {
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(itemView.getContext());
                                        builder2.setTitle("Xác nhận làm rỗng phòng");
                                        builder2.setMessage("Bạn có chắc chắn muốn làm rỗng phòng?");
                                        builder2.setPositiveButton("Có", (dialog2, which2) -> {
                                            lamrongphong();
                                        });
                                        builder2.setNegativeButton("Không", (dialog22, which22) -> {
                                        });
                                        builder2.show();
                                    }
                                });
                                builder1.show();
                            }
                            else if (which == 2) {
                                // Thêm phòng mới, logic chưa được thực hiện
                            }
                        });
                        builder.show();
                    }
                }
            });
        }
        public void bindData(Khachsan data) {
            Glide.with(hotelImage.getContext()).load(data.getImage()).into(hotelImage);
            hotelName.setText(data.getName());
            if (data.getQuy() != null) {
                int number = Integer.parseInt(data.getQuy());
                NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                formatter.setMinimumFractionDigits(0);
                String formattedNumber = formatter.format(number) + " VNĐ";
                quyks.setText("Quỹ khách sạn: " + formattedNumber);
            }
        }
    }

    private void lamrongphong() {
    }

}

