package com.aqsoft.bookingappnoran.Partner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.aqsoft.bookingappnoran.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PartnerUpdateInfoRoomActivity extends AppCompatActivity {
    private String Key,Room;
    private ImageView imageView2,delete;
    private TextView loaiphong;
    private Button them;
    private Toolbar toolbar;
    private EditText giaTien,phongTam,giuong,moTaNgan,moTa, soLuongPhong;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("KhachSan");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_update_info_room);
        anhxa();


        toolbar.setOnClickListener(v -> {
            finish();
        });
        getintent();
        getthongtin();
        them.setOnClickListener(v -> {
            String GiaTien = giaTien.getText().toString();
            String PhongTam = phongTam.getText().toString();
            String Giuong = giuong.getText().toString();
            String MoTaNgan = moTaNgan.getText().toString();
            String MoTa = moTa.getText().toString();
            mDatabase.child(Key).child("LoaiPhong").child(Room).child("Giatien").setValue(GiaTien);
            mDatabase.child(Key).child("LoaiPhong").child(Room).child("Bath").setValue(PhongTam);
            mDatabase.child(Key).child("LoaiPhong").child(Room).child("Bed").setValue(Giuong);
            mDatabase.child(Key).child("LoaiPhong").child(Room).child("Motangan").setValue(MoTaNgan);
            mDatabase.child(Key).child("LoaiPhong").child(Room).child("Mota").setValue(MoTa);
            Toast.makeText(PartnerUpdateInfoRoomActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            finish();
        });
        delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(PartnerUpdateInfoRoomActivity.this);
            builder.setTitle("Xác nhận");
            builder.setMessage("Bạn có chắc chắn muốn xóa loại phòng này không?");
            builder.setPositiveButton("Có", (dialog, which) -> {
                mDatabase.child(Key).child("LoaiPhong").child(Room).removeValue();
                Toast.makeText(PartnerUpdateInfoRoomActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                finish();
            });
            builder.setNegativeButton("Không", (dialog, which) -> {
                dialog.dismiss();
            });
            builder.show();
        });

    }

    private void anhxa() {
        imageView2 = findViewById(R.id.imageView2);
        delete = findViewById(R.id.delete);
        loaiphong = findViewById(R.id.loaiphong);
        giaTien = findViewById(R.id.giaTien);
        phongTam = findViewById(R.id.phongTam);
        giuong = findViewById(R.id.giuong);
        moTaNgan = findViewById(R.id.moTaNgan);
        moTa = findViewById(R.id.moTa);
        soLuongPhong = findViewById(R.id.soLuongPhong);
        them = findViewById(R.id.them);
        toolbar = findViewById(R.id.toolbar);
    }

    private void getthongtin() {
        mDatabase.child(Key).child("LoaiPhong").child(Room).addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String GiaTien = snapshot.child("Giatien").getValue().toString();
                    String PhongTam = snapshot.child("Bath").getValue().toString();
                    String Giuong = snapshot.child("Bed").getValue().toString();
                    String MoTaNgan = snapshot.child("Motangan").getValue().toString();
                    String MoTa = snapshot.child("Mota").getValue().toString();
                    String HinhAnh = snapshot.child("Image").getValue().toString();
                    loaiphong.setText("Loại phòng "+Room);
                    giaTien.setText(GiaTien);
                    phongTam.setText(PhongTam);
                    giuong.setText(Giuong);
                    moTaNgan.setText(MoTaNgan);
                    moTa.setText(MoTa);
                    Glide.with(PartnerUpdateInfoRoomActivity.this).load(HinhAnh).into(imageView2);
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {

            }
        });


    }

    private void getintent() {
        Key = getIntent().getStringExtra("Key");
        Room = getIntent().getStringExtra("Room");
    }
}