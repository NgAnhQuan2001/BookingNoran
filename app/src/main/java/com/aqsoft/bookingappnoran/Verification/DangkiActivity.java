package com.aqsoft.bookingappnoran.Verification;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.User.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DangkiActivity extends AppCompatActivity {
    private TextView dangnhapngay;
    private FirebaseAuth auth;
    private DatabaseReference Myref = FirebaseDatabase.getInstance().getReference("TaiKhoan");

    private Button btndangky;
    private EditText edtsodienthoai,matkhau,matkhau2,edtemail,edtten;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangki);
        auth = FirebaseAuth.getInstance();
        dangnhapngay = findViewById(R.id.dangnhapngay);
        dangnhapngay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DangkiActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btndangky = findViewById(R.id.btndangky);
        edtten = findViewById(R.id.edtten);
        edtsodienthoai = findViewById(R.id.edtsodienthoai);
        matkhau = findViewById(R.id.matkhau);
        edtemail = findViewById(R.id.edtemail);
        matkhau2 = findViewById(R.id.matkhau2);

        btndangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sodienthoai = edtsodienthoai.getText().toString();
                String password = matkhau.getText().toString();
                String emaill = edtemail.getText().toString();
                String password2 = matkhau2.getText().toString();
                if (sodienthoai.isEmpty() || password.isEmpty() || password2.isEmpty() || edtemail.getText().toString().isEmpty()) {
                    edtsodienthoai.setError("Không được để trống");
                    matkhau.setError("Không được để trống");
                    matkhau2.setError("Không được để trống");
                    edtemail.setError("Không được để trống");
                } else if (!password.equals(password2)) {
                    matkhau.setError("Mật khẩu không trùng khớp");
                    matkhau2.setError("Mật khẩu không trùng khớp");
                } else {
                    auth.createUserWithEmailAndPassword(emaill, password).addOnCompleteListener(DangkiActivity.this, task -> {
                        if (task.isSuccessful()) {
                            String uid = FirebaseAuth.getInstance().getUid();
                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            Map<String, Object>  data = new HashMap<>();
                            data.put("uid",uid);
                            data.put("email",email);
                            data.put("name",edtten.getText().toString());
                            data.put("sodienthoai",sodienthoai);
                            data.put("role","user");
                            Myref.child(uid).setValue(data);
                            Toast.makeText(DangkiActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DangkiActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            edtsodienthoai.setError("Email đã tồn tại");
                        }
                    });
                   
                }
            }
        });


    }
}