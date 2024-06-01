package com.aqsoft.bookingappnoran.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.aqsoft.bookingappnoran.R;

import com.aqsoft.bookingappnoran.User.SettingActivity;
import com.aqsoft.bookingappnoran.model.Thongbaomodel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ThongBaoAdapter extends RecyclerView.Adapter<ThongBaoAdapter.MyViewHolder> {
    private static List<Thongbaomodel> mData;

    public ThongBaoAdapter(List<Thongbaomodel> data) {
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
        private TextView khachsan,loaiphong,ngaytraphong,ngaynhanphong,tongtien,huyphong,trangthai;
        private Button danhgia;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            khachsan = itemView.findViewById(R.id.khachsan);
            loaiphong = itemView.findViewById(R.id.loaiphong);
            ngaytraphong = itemView.findViewById(R.id.ngaytraphong);
            ngaynhanphong = itemView.findViewById(R.id.ngaynhanphong);
            tongtien = itemView.findViewById(R.id.tongtien);
            huyphong = itemView.findViewById(R.id.huyphong);
            trangthai = itemView.findViewById(R.id.trangthai);
            danhgia = itemView.findViewById(R.id.danhgia);


        }

        public void bindData(Thongbaomodel data) {



            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


            danhgia.setOnClickListener(v -> {
                showFeedbackDialog(v.getContext(), data.getKeyks(),data.getRoom());
            });


            if (data.getTrangThai().equals("Đã xác minh")) {
                trangthai.setText("Trạng thái: Đã nhận phòng");
                huyphong.setVisibility(View.GONE);
                danhgia.setVisibility(View.VISIBLE);

            }



            else if(data.getTrangThai().equals("Hủy phòng")){
                trangthai.setText("Trạng thái: Hủy phòng");



            } else {
                trangthai.setText("Trạng thái: Đang chờ nhận phòng");
                huyphong.setVisibility(View.VISIBLE);
                danhgia.setVisibility(View.GONE);
            }


            huyphong.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                builder.setTitle("Hủy phòng");
                builder.setMessage("Bạn có chắc chắn muốn hủy phòng?");
                builder.setPositiveButton("Có", (dialog, which) -> {
                    trangthai.setText("Trạng thái: Hủy phòng");
                    huyphong.setVisibility(View.GONE);
                    danhgia.setVisibility(View.GONE);
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Lichsu").child(uid).child(data.getID());
                    reference.child("TrangThai").setValue("Hủy phòng");
                    Toast.makeText(itemView.getContext(), "Hủy phòng thành công", Toast.LENGTH_SHORT).show();


                });
                builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
                builder.show();



                    });
            khachsan.setText("Tên khách sạn: " +data.getNameKS());
            loaiphong.setText("Loại phòng: "+data.getRoom());
            ngaytraphong.setText("Ngày trả phòng: " +data.getDate2());
            ngaynhanphong.setText("Ngày nhận phòng: " +data.getDate1());
            int number = Integer.parseInt(data.getTongtien());
            NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            formatter.setMinimumFractionDigits(0);
            String formattedNumber = formatter.format(number) + " VNĐ";
            tongtien.setText("Tổng tiền: " +formattedNumber);


        }
        private void showFeedbackDialog(Context context,String Key,String Room) {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.dialograting);
            RatingBar ratingBar = dialog.findViewById(R.id.ratingBar);
            EditText comment = dialog.findViewById(R.id.comment);
            Button next_btn = dialog.findViewById(R.id.next_btn);
            next_btn.setOnClickListener(v -> {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("KhachSan").child(Key).child("LoaiPhong").child(Room).child("Rating");
                reference.child("Rating").setValue(ratingBar.getRating());
                reference.child("Uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child("Comment").setValue(comment.getText().toString());
                dialog.dismiss();
                Toast.makeText(context, "Đánh giá thành công", Toast.LENGTH_SHORT).show();
            });
            dialog.show();


        }

    }

}
