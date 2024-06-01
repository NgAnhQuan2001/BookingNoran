package com.aqsoft.bookingappnoran.Admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.aqsoft.bookingappnoran.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ThemKhachSanActivity extends AppCompatActivity {
    private EditText tenKhachSanEditText;
    private EditText soSaoEditText;
    private EditText diaChiEditText;
    private EditText soDienThoaiEditText;
    private EditText giaTienEditText;
    private EditText giuongEditText;
    private EditText phongTamEditText;
    private EditText moTaNganEditText;
    private EditText moTaEditText;
    private EditText soLuongPhongEditText;
    private DatabaseReference mDatabase;
    private Button them;
    private String key;
    private static final int PICK_IMAGE_REQUEST_1 = 1;
    private static final int PICK_IMAGE_REQUEST_2 = 2;

    private Button btnChooseImage;
    private ImageView imageView;
    private Uri filePath1, filePath2;

    private Button btnChooseImage2;
    private Toolbar toolbar;
    private ImageView imageView2;
    private Uri filePath;

    private FirebaseStorage storage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_khach_san);
        anhxa();
        themks();

        btnChooseImage = findViewById(R.id.btnChooseImage);
        imageView = findViewById(R.id.imageView);
        btnChooseImage2 = findViewById(R.id.btnChooseImage2);
        imageView2 = findViewById(R.id.imageView2);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        btnChooseImage.setOnClickListener(v -> chooseImage());
        btnChooseImage2.setOnClickListener(v -> chooseImage2());
        toolbar = findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> finish());


    }

    private void chooseImage2() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh thứ hai"), PICK_IMAGE_REQUEST_2);
    }

    private void themks() {
        them = findViewById(R.id.them);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = mDatabase.child("KhachSan").push();
        key = myRef.getKey();
        them.setOnClickListener(v -> {
            if (filePath1 != null && filePath2 != null) {
                uploadImages();
                Map<String, Object> newData = new HashMap<>();
                newData.put("Name", tenKhachSanEditText.getText().toString());
                newData.put("Address", diaChiEditText.getText().toString());
                newData.put("Phone", soDienThoaiEditText.getText().toString());
                newData.put("Rating", soSaoEditText.getText().toString());
                newData.put("Key", key);
                newData.put("uid", uid);
                Map<String, Object> newLoaiPhong = new HashMap<>();
                newLoaiPhong.put("Giatien", giaTienEditText.getText().toString());
                newLoaiPhong.put("Bed", giuongEditText.getText().toString());
                newLoaiPhong.put("Bath", phongTamEditText.getText().toString());
                newLoaiPhong.put("Wifi", "Có");
                newLoaiPhong.put("Mota", moTaEditText.getText().toString());
                newLoaiPhong.put("Motangan", moTaNganEditText.getText().toString());
                myRef.setValue(newData);
                myRef.child("LoaiPhong").child("Basic").setValue(newLoaiPhong);
                Integer soLuongPhong = Integer.parseInt(soLuongPhongEditText.getText().toString());
                for (int j = 0; j < soLuongPhong; j++) {
                    myRef.child("LoaiPhong").child("Basic").child("Số lượng").child("Phòng " + j).child("book").setValue("chuadat");
                }
            } else {
                Toast.makeText(ThemKhachSanActivity.this, "Vui lòng chọn cả hai ảnh trước", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(ThemKhachSanActivity.this, "Thêm khách sạn thành công", Toast.LENGTH_SHORT).show();
            finish();


        });
    }

    private void anhxa() {
        tenKhachSanEditText = findViewById(R.id.tenKhachSan);
        soSaoEditText = findViewById(R.id.soSao);
        diaChiEditText = findViewById(R.id.diaChi);
        soDienThoaiEditText = findViewById(R.id.soDienThoai);
        giaTienEditText = findViewById(R.id.giaTien);
        giuongEditText = findViewById(R.id.giuong);
        phongTamEditText = findViewById(R.id.phongTam);
        moTaNganEditText = findViewById(R.id.moTaNgan);
        moTaEditText = findViewById(R.id.moTa);
        soLuongPhongEditText = findViewById(R.id.soLuongPhong);
        imageView2 = findViewById(R.id.imageView2);

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh"), PICK_IMAGE_REQUEST_1);
    }

    private void uploadImages() {
        if (filePath1 != null) {
            StorageReference ref1 = storageReference.child("images/" + UUID.randomUUID().toString());
            ref1.putFile(filePath1)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref1.getDownloadUrl().addOnSuccessListener(uri -> {
                            mDatabase.child("KhachSan").child(key).child("Image").setValue(uri.toString()).addOnSuccessListener(aVoid -> {
                                if (filePath2 == null) {
                                    // Nếu chỉ có ảnh thứ nhất được chọn, hoàn tất quá trình ở đây
                                    Toast.makeText(ThemKhachSanActivity.this, "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(ThemKhachSanActivity.this, "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show());
        }

        if (filePath2 != null) {
            StorageReference ref2 = storageReference.child("images/" + UUID.randomUUID().toString());
            ref2.putFile(filePath2)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref2.getDownloadUrl().addOnSuccessListener(uri -> {
                            mDatabase.child("KhachSan").child(key).child("LoaiPhong").child("Basic").child("Image").setValue(uri.toString()).addOnSuccessListener(aVoid ->
                                    Toast.makeText(ThemKhachSanActivity.this, "Tải ảnh lên thành công", Toast.LENGTH_SHORT).show()
                            );
                        });
                    })
                    .addOnFailureListener(e -> Toast.makeText(ThemKhachSanActivity.this, "Tải ảnh lên thất bại", Toast.LENGTH_SHORT).show());
        }

        if (filePath1 == null && filePath2 == null) {
            Toast.makeText(ThemKhachSanActivity.this, "Vui lòng chọn ảnh trước khi tải lên", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST_1) {
                filePath1 = data.getData();
                imageView.setImageURI(filePath1);
                imageView.setVisibility(View.VISIBLE);
            } else if (requestCode == PICK_IMAGE_REQUEST_2) {
                filePath2 = data.getData();
                imageView2.setImageURI(filePath2);
                imageView2.setVisibility(View.VISIBLE);
            }
        }
    }
}