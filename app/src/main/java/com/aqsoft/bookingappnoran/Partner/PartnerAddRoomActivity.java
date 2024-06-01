package com.aqsoft.bookingappnoran.Partner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.aqsoft.bookingappnoran.Admin.ThemKhachSanActivity;
import com.aqsoft.bookingappnoran.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class PartnerAddRoomActivity extends AppCompatActivity {
    private EditText tenPhong;
    private EditText giaTien;
    private Button btnChooseImage2;
    private Uri filePath1, filePath2;
    private static final int PICK_IMAGE_REQUEST =1;
    private String Key;
    private ImageView imageView2;
    private EditText giuong;
    private EditText phongTam;
    private EditText moTaNgan;
    private EditText moTa;
    private EditText soLuongPhong;
    private Button them;

    private FirebaseStorage storage;
    private String tenphong;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_add_room);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        anhxa();
        getintent();
        btnChooseImage2.setOnClickListener(v -> {
            btnChooseImage();
        });

        them.setOnClickListener(v -> {
            uploadImages();
            tenphong = tenPhong.getText().toString();
            String giatien = giaTien.getText().toString();
            String giuong1 = giuong.getText().toString();
            String phongtam = phongTam.getText().toString();
            String motangan = moTaNgan.getText().toString();
            String mota = moTa.getText().toString();
            String soluongphong = soLuongPhong.getText().toString();
            if (tenphong.isEmpty() || giatien.isEmpty() || giuong1.isEmpty() || phongtam.isEmpty() || motangan.isEmpty() || mota.isEmpty() || soluongphong.isEmpty()) {
                Toast.makeText(this, "Không được để trống thông tin ", Toast.LENGTH_SHORT).show();
            }else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("Bath", phongtam);
                map.put("Bed", giuong1);
                map.put("Giatien", giatien);
                map.put("Mota", mota);
                map.put("Motangan", motangan);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("KhachSan").child(Key).child("LoaiPhong").child(tenphong);
                databaseReference.setValue(map);
                Integer soLuongPhong = Integer.parseInt(soluongphong.toString());
                for(int j = 0; j <soLuongPhong; j++){
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("KhachSan").child(Key).child("LoaiPhong").child(tenphong).child("Số lượng").child("Phòng " + j);
                    databaseReference1.child("book").setValue("chuadat");
                }
                Toast.makeText(this, "Thêm phòng thành công", Toast.LENGTH_SHORT).show();
                finish();



            }
        });


    }

    private void btnChooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                filePath1 = data.getData();
                imageView2.setImageURI(filePath1);
            }
        }
    }

    private void getintent() {
        Intent intent = getIntent();
         Key = intent.getStringExtra("Key");
    }

    private void anhxa() {
        tenPhong = findViewById(R.id.tenphong);
        giaTien = findViewById(R.id.giaTien);
        giuong = findViewById(R.id.giuong);
        phongTam = findViewById(R.id.phongTam);
        moTaNgan = findViewById(R.id.moTaNgan);
        moTa = findViewById(R.id.moTa);
        soLuongPhong = findViewById(R.id.soLuongPhong);
        them = findViewById(R.id.them);
        btnChooseImage2 = findViewById(R.id.btnChooseImage2);
        imageView2 = findViewById(R.id.imageView2);
    }
    private void uploadImages() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        if (filePath1 != null) {
            StorageReference ref1 = storageReference.child("images/" + UUID.randomUUID().toString());
            ref1.putFile(filePath1)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref1.getDownloadUrl().addOnSuccessListener(uri -> {
                            mDatabase.child("KhachSan").child(Key).child("LoaiPhong").child(tenphong).child("Image").setValue(uri.toString()).addOnSuccessListener(aVoid -> {
                                    Toast.makeText(PartnerAddRoomActivity.this, "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                            });
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(PartnerAddRoomActivity.this, "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show());
        }


        if (filePath1 == null) {
            Toast.makeText(PartnerAddRoomActivity.this, "Vui lòng chọn ảnh trước khi tải lên", Toast.LENGTH_SHORT).show();
        }
    }
}