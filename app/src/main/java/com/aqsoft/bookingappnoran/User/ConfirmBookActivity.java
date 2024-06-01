package com.aqsoft.bookingappnoran.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.ZaloPay.AppInfo;
import com.aqsoft.bookingappnoran.ZaloPay.CreateOrder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ConfirmBookActivity extends AppCompatActivity {
    private String Soluong, Date1, Date2, Luuy, Key, Room, priceroom, nameks;
    private String tien;
    Integer i = 0;

    RadioButton thanhtoansau, zalopay;
    private boolean BookSuccess = false;
    private String uid = FirebaseAuth.getInstance().getUid();
    private TextView ngayden, ngaydi, loaiphong, soluongkhachhang, tenkhachsan, luuy, tongtien;
    private Button btn_huyhoadon, btn_svhoadon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_book);
        anhxa();
        getintent();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        ZaloPaySDK.init(AppInfo.APP_ID, Environment.SANDBOX);

        btn_huyhoadon.setOnClickListener(v -> finish());
        btn_svhoadon.setOnClickListener(v -> {
            if (thanhtoansau.isChecked()) {
                checkphong();
                taohoadon();
            } else if (zalopay.isChecked()) {
                Naptien();
            }

        });


    }

    private void checkphong()
    {
        String userID = FirebaseAuth.getInstance().getUid();
        DatabaseReference Myref = FirebaseDatabase.getInstance().getReference("KhachSan").child(Key).child("LoaiPhong").child(Room);
        Myref.child("Số lượng").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean foundRoom = false; // Biến để kiểm tra có phòng nào còn trống không
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if ("chuadat".equals(dataSnapshot.child("book").getValue(String.class))) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("book", "dadat");
                        map.put("Date1", Date1);
                        map.put("Date2", Date2);
                        map.put("Soluong", Soluong);
                        map.put("Luuy", Luuy);
                        map.put("Useruid", userID);
                        map.put("Tongtien", tien);
                        dataSnapshot.getRef().updateChildren(map);
                        Toast.makeText(ConfirmBookActivity.this, "Đặt phòng thành công", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ConfirmBookActivity.this, MainActivity.class));
                        finish();
                        foundRoom = true; // Đánh dấu đã tìm thấy và đặt phòng thành công
                        break; // Thoát khỏi vòng lặp sau khi đã cập nhật thành công
                    }
                }
                if (!foundRoom) {
                    // Nếu không tìm thấy phòng nào để đặt
                    Toast.makeText(ConfirmBookActivity.this, "Đặt phòng thất bại, không còn phòng trống", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConfirmBookActivity.this, "Lỗi khi truy vấn dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
    ;

    private void taohoadon() {
        Random rand = new Random();
        int randomNumberr = rand.nextInt(99999) + 1; // Tạo số ngẫu nhiên từ 1 đến 99999
        String randomNumber = String.valueOf(randomNumberr);
        String userID = FirebaseAuth.getInstance().getUid();
        DatabaseReference MyrefLichsu = FirebaseDatabase.getInstance().getReference("Lichsu").child(userID).child(randomNumber);
        DatabaseReference MyrefHoadon = FirebaseDatabase.getInstance().getReference("Hoadon").child(Key).push();
        DatabaseReference MyrefQuy = FirebaseDatabase.getInstance().getReference("KhachSan").child(Key);
        MyrefLichsu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = new HashMap<>();
                map.put("NameKS", nameks);
                map.put("Room", Room);
                map.put("Date1", Date1);
                map.put("Date2", Date2);
                map.put("TrangThai", "0");
                map.put("Soluong", Soluong);
                map.put("Luuy", Luuy);
                map.put("Keyks", Key);
                map.put("ID", randomNumber);
                map.put("Tongtien", tien);
                MyrefLichsu.setValue(map);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        MyrefHoadon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> map = new HashMap<>();
                map.put("NameKS", nameks);
                map.put("Room", Room);
                map.put("Date1", Date1);
                map.put("Date2", Date2);
                map.put("TrangThai", "0");
                map.put("Soluong", Soluong);
                map.put("Luuy", Luuy);
                map.put("ID", randomNumber);
                map.put("Useruid", userID);
                map.put("Tongtien", tien);
                map.put("Keyks", Key);
                MyrefHoadon.setValue(map);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        MyrefQuy.child("quy").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if(task.getResult().exists()){
                    String quy = task.getResult().getValue().toString();
                    int quyks = Integer.parseInt(quy) + Integer.parseInt(tien);
                    MyrefQuy.child("quy").setValue(String.valueOf(quyks));
                }
                else {
                    MyrefQuy.child("quy").setValue(tien);

                }
            }
        });
    }


    private void anhxa() {
        ngayden = findViewById(R.id.ngayden);
        ngaydi = findViewById(R.id.ngaydi);
        loaiphong = findViewById(R.id.loaiphong);
        soluongkhachhang = findViewById(R.id.soluongkhachhang);
        tenkhachsan = findViewById(R.id.tenkhachsan);
        luuy = findViewById(R.id.luuy);
        tongtien = findViewById(R.id.tongtien);
        btn_huyhoadon = findViewById(R.id.btn_huyhoadon);
        btn_svhoadon = findViewById(R.id.btn_svhoadon);
        thanhtoansau = findViewById(R.id.thanhtoansau);
        zalopay = findViewById(R.id.zalopay);

    }

    private void getintent() {
        Intent intent = getIntent();
        if (intent != null) {
            Soluong = intent.getStringExtra("Soluong");
            Date1 = intent.getStringExtra("Date1");
            Date2 = intent.getStringExtra("Date2");
            Luuy = intent.getStringExtra("AdditionalInput");
            Key = intent.getStringExtra("Key");
            Room = intent.getStringExtra("Room");
            priceroom = intent.getStringExtra("Price");
            nameks = intent.getStringExtra("NameKS");

            ngayden.setText(Date1);
            ngaydi.setText(Date2);
            loaiphong.setText(Room);
            soluongkhachhang.setText(Soluong);
            luuy.setText(Luuy);
            tenkhachsan.setText(nameks);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date date1 = dateFormat.parse(Date1);
                Date date2 = dateFormat.parse(Date2);
                long diff = date2.getTime() - date1.getTime();
                long diffDays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                tien = String.valueOf(diffDays * Integer.parseInt(priceroom));
                //doi sang tien viet
                DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
                tongtien.setText(decimalFormat.format(Integer.parseInt(tien)) + " VND");
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

    }
    private void Naptien() {
        String amount = tien;
        CreateOrder orderApi = new CreateOrder();
        try {
            JSONObject data = orderApi.createOrder(amount);
            String code = data.getString("returncode");

            if (code.equals("1")) {
                String tokenn = data.getString("zptranstoken");

                ZaloPaySDK.getInstance().payOrder(ConfirmBookActivity.this, tokenn, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(final String transactionId, final String transToken, final String appTransID) {
                        checkphong();
                        taohoadon();
                        Toast.makeText(ConfirmBookActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onPaymentCanceled(String zpTransToken, String appTransID) {
                        Toast.makeText(ConfirmBookActivity.this, "Thanh toán bị hủy", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String zpTransToken, String appTransID) {
                        Toast.makeText(ConfirmBookActivity.this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}