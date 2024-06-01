package com.aqsoft.bookingappnoran.Partner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.aqsoft.bookingappnoran.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateHotelActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button btnChooseImage,them;
    private Toolbar toolbar;
    private EditText tenKhachSan,soSao,diaChi,soDienThoai;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("KhachSan");
    private String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_hotel);
        anhxa();
        getintent();
        getthongtin();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> {
            finish();
        });
        them.setOnClickListener(v -> {
            reference.child(key).child("Name").setValue(tenKhachSan.getText().toString());
            reference.child(key).child("Rating").setValue(soSao.getText().toString());
            reference.child(key).child("Address").setValue(diaChi.getText().toString());
            reference.child(key).child("Phone").setValue(soDienThoai.getText().toString());
            Toast.makeText(UpdateHotelActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            finish();
        });

    }

    private void getthongtin() {
        reference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    tenKhachSan.setText(snapshot.child("Name").getValue().toString());
                    soSao.setText(snapshot.child("Rating").getValue().toString());
                    diaChi.setText(snapshot.child("Address").getValue().toString());
                    soDienThoai.setText(snapshot.child("Phone").getValue().toString());
                    Glide.with(UpdateHotelActivity.this).load(snapshot.child("Image").getValue().toString()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getintent() {
        Intent intent = getIntent();
         key = intent.getStringExtra("Key");
    }

    private void anhxa() {
        imageView = findViewById(R.id.imageView);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        them = findViewById(R.id.them);
        tenKhachSan = findViewById(R.id.tenKhachSan);
        soSao = findViewById(R.id.soSao);
        diaChi = findViewById(R.id.diaChi);
        soDienThoai = findViewById(R.id.soDienThoai);
    }
}