package com.aqsoft.bookingappnoran.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aqsoft.bookingappnoran.Adapter.DanhgiaAdapter;
import com.aqsoft.bookingappnoran.Adapter.KhachsanAdapter;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.model.Danhgiamodel;
import com.aqsoft.bookingappnoran.model.Khachsan;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private CardView back;
    private String Key;
    private Integer soluongphongg =0;
    private String Room;
    private String giamoingay;
    private TextView soluongphong;
    private ImageView image;
    private TextView nameks,dcks,bedTxt,bathTxt,wifiTxt,mota,tongtien;


    private Button pay;

    private RecyclerView recyclerview_danhgia;

    private DanhgiaAdapter adapter;

    private List<Danhgiamodel> dataList;

    private DatabaseReference Myref = FirebaseDatabase.getInstance().getReference().child("KhachSan");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        anhxa();
        getintent();
        getthongtinphong1();
        getthongtinphong2();
        getsoluongphong();
        getdulieudanhgia();
        back.setOnClickListener(v -> {
            finish();
        });
        pay.setOnClickListener(v -> {
            Intent intent = new Intent(DetailActivity.this, BookRoomActivity.class);
            intent.putExtra("Key",Key);
            intent.putExtra("Room",Room);
            intent.putExtra("Price",giamoingay);
            startActivity(intent);
        });


    }

    private void getdulieudanhgia() {
        dataList = new ArrayList<>();
        adapter = new DanhgiaAdapter(dataList);
        recyclerview_danhgia.setAdapter(adapter);
        recyclerview_danhgia.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        Myref.child(Key).child("LoaiPhong").child(Room).child("Rating").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("Comment").getValue(String.class) == null){
                    String comment = "Chưa có đánh giá";
                } else {
                    String comment = snapshot.child("Comment").getValue(String.class);
                    Long ratingg = snapshot.child("Rating").getValue(Long.class);
                    String rating = String.valueOf(ratingg);
                    String uid = snapshot.child("Uid").getValue(String.class);
                    dataList.add(dataList.size(), new Danhgiamodel(uid,comment,rating));
                    adapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getthongtinphong2() {
        Myref.child(Key).child("LoaiPhong").child(Room).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String bed = snapshot.child("Bed").getValue(String.class);
                String bath = snapshot.child("Bath").getValue(String.class);
                String wifi = snapshot.child("Wifi").getValue(String.class);
                String mota1 = snapshot.child("Mota").getValue(String.class);
                giamoingay = snapshot.child("Giatien").getValue(String.class);
                String Image = snapshot.child("Image").getValue(String.class);
                int desiredWidth = 1200;
                int desiredHeight = 600;
                bedTxt.setText(bed);
                bathTxt.setText(bath);
                wifiTxt.setText(wifi);
                mota.setText(mota1);
                Glide.with(DetailActivity.this).load(Image).override(desiredWidth, desiredHeight).centerCrop().into(image);
                int number = Integer.parseInt(giamoingay);
                NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                formatter.setMinimumFractionDigits(0);
                String formattedNumber = formatter.format(number) + " VNĐ";
                tongtien.setText(formattedNumber +"/1 đêm");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getthongtinphong1() {
        Myref.child(Key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nameks1 = snapshot.child("Name").getValue(String.class);
                String dcks1 = snapshot.child("Address").getValue(String.class);
                nameks.setText(nameks1);
                dcks.setText(dcks1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getintent() {
        Intent intent = getIntent();
        Key = intent.getStringExtra("Key");
        Room = intent.getStringExtra("Room");

    }
    private void getsoluongphong(){
        Myref.child(Key).child("LoaiPhong").child(Room).child("Số lượng").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                soluongphongg = 0; // Khởi tạo lại để tránh dữ liệu cũ
                for (DataSnapshot data : snapshot.getChildren()){
                    if(data.child("book").getValue(String.class).equals("chuadat")){
                        soluongphongg++;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Cập nhật UI ở đây để đảm bảo nó được thực hiện trên luồng UI
                        soluongphong.setText("Phòng còn lại: " + soluongphongg);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi ở đây
            }
        });
    }


    private void anhxa() {
        back = findViewById(R.id.back);
        nameks = findViewById(R.id.nameks);
        dcks = findViewById(R.id.dcks);
        image = findViewById(R.id.image);
        bedTxt = findViewById(R.id.bedTxt);
        bathTxt = findViewById(R.id.bathTxt);
        wifiTxt = findViewById(R.id.wifiTxt);
        mota = findViewById(R.id.mota);
        pay = findViewById(R.id.pay);
        tongtien = findViewById(R.id.tongtien);
        soluongphong = findViewById(R.id.soluongphong);
        recyclerview_danhgia = findViewById(R.id.recyclerview_danhgia);
    }

}