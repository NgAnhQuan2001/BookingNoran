package com.aqsoft.bookingappnoran.Verification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.aqsoft.bookingappnoran.Admin.AdminActivity;
import com.aqsoft.bookingappnoran.PartnerActivity;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.User.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private TextView dangkingay;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TaiKhoan");
    private FirebaseAuth auth;
    private Button btnLogin;
    private EditText edtPhone,edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dangkingay = findViewById(R.id.dangkingay);
        dangkingay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, DangkiActivity.class);
                startActivity(intent);
            }
        });
        auth = FirebaseAuth.getInstance();
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = edtPhone.getText().toString();
                String password = edtPassword.getText().toString();
                if (phone.isEmpty() || password.isEmpty()) {
                    edtPhone.setError("Không được để trống");
                    edtPassword.setError("Không được để trống");
                } else {
                    auth.signInWithEmailAndPassword(phone, password).addOnCompleteListener(LoginActivity.this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            String uid = auth.getCurrentUser().getUid();
                            reference.child(uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    String role = snapshot.child("role").getValue().toString();
                                    if (role.equals("admin")) {
                                        Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (role.equals("doitac")) {
                                        Intent intent = new Intent(LoginActivity.this, PartnerActivity.class);
                                        startActivity(intent);
                                        finish();

                                    } else if (role.equals("user")){
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        } else {
                            edtPhone.setError("Số điện thoại hoặc mật khẩu không đúng");
                            edtPassword.setError("Số điện thoại hoặc mật khẩu không đúng");
                        }
                    });
                }
            }
        });




    }
}