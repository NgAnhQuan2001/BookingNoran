package com.aqsoft.bookingappnoran;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.aqsoft.bookingappnoran.Admin.ThemKhachSanActivity;
import com.aqsoft.bookingappnoran.Partner.PartnerNotificationActivity;
import com.aqsoft.bookingappnoran.Verification.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PartnerActivity extends AppCompatActivity {
    private CardView dangxuat;
    private TextView name;

    private CardView qlks,thongbao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner);
        qlks = findViewById(R.id.qlks);
        dangxuat = findViewById(R.id.dangxuat);
        name = findViewById(R.id.name);
        getname();
        thongbao = findViewById(R.id.thongbao);
        thongbao.setOnClickListener(v -> {
            Intent intent = new Intent(PartnerActivity.this, PartnerNotificationActivity.class);
            startActivity(intent);
        });
        dangxuat.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(PartnerActivity.this);
            builder.setTitle("Đăng xuất");
            builder.setMessage("Bạn có chắc chắn muốn đăng xuất không?");
            builder.setPositiveButton("Có", (dialogInterface, i) -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(PartnerActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            });
            builder.setNegativeButton("Không", (dialogInterface, i) -> {
            });
            builder.show();
        });

        qlks.setOnClickListener(v -> {
            AlertDialog.Builder addProductBuilder = new AlertDialog.Builder(PartnerActivity.this);
            addProductBuilder.setTitle("Chọn hành động");
            String[] addOptions = {"Danh sách khách sạn", "Thêm khách sạn"};
            addProductBuilder.setItems(addOptions, (dialogInterface, i) -> {
                Intent intent;
                switch (i) {
                    case 0:
                        // Ds ks
                        intent = new Intent(PartnerActivity.this, ListHotelActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        // Thêm khách sạn
                        intent = new Intent(PartnerActivity.this, ThemKhachSanActivity.class);
                        startActivity(intent);
                        break;
                }
            });
            addProductBuilder.show();
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
