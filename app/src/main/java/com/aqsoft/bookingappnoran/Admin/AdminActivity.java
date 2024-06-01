package com.aqsoft.bookingappnoran.Admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.Verification.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminActivity extends AppCompatActivity {
    private CardView qltaikhoan,thongbao;
    private TextView name;
    private CardView dangxuat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        thongbao = findViewById(R.id.thongbao);
        name = findViewById(R.id.name);
        getname();
        thongbao.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, AdminNotificationActivity.class);
            startActivity(intent);
        });
        qltaikhoan = findViewById(R.id.qltaikhoan);
        qltaikhoan.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, ListAccActivity.class);
            startActivity(intent);
        });
        dangxuat = findViewById(R.id.dangxuat);
        dangxuat.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
            builder.setTitle("Đăng xuất");
            builder.setMessage("Bạn có chắc chắn muốn đăng xuất?");
            builder.setPositiveButton("Có", (dialog, which) -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            });
            builder.setNegativeButton("Không", (dialog, which) -> dialog.dismiss());
            builder.show();
        });
    }

    private void getname() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TaiKhoan").child(uid);
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(task.getResult().child("name").getValue()!=null) {
                    name.setText("Xin chào, \n"+task.getResult().child("name").getValue().toString());
                }
            }
        });
    }
}