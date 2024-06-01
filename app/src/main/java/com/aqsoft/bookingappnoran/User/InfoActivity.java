package com.aqsoft.bookingappnoran.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.aqsoft.bookingappnoran.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {
    private String uid = FirebaseAuth.getInstance().getUid();
    private DatabaseReference Myref = FirebaseDatabase.getInstance().getReference("TaiKhoan");
    private EditText nameacc,emailacc,phoneacc,dcacc,gtacc;
    private Button imageViewloginacc;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        anhxa();
        getthongtin();
        suathongtin();
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void suathongtin() {
        imageViewloginacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> data = new HashMap<>();
                data.put("name", String.valueOf(nameacc.getText()) );
                data.put("email", String.valueOf(emailacc.getText()) );
                data.put("sodienthoai", String.valueOf(phoneacc.getText()) );
                data.put("diachi", String.valueOf(dcacc.getText()) );
                data.put("gioitinh", String.valueOf(gtacc.getText()) );

                Myref.child(uid).updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(InfoActivity.this, "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(InfoActivity.this, "Thay đổi thông tin thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }

    private void getthongtin() {


        Myref.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String phone = snapshot.child("sodienthoai").getValue(String.class);
                String diachi = snapshot.child("diachi").getValue(String.class);
                String gioitinh = snapshot.child("gioitinh").getValue(String.class);
                nameacc.setText(name);
                emailacc.setText(email);
                phoneacc.setText(phone);
                dcacc.setText(diachi);
                gtacc.setText(gioitinh);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void anhxa() {
        nameacc = findViewById(R.id.nameacc);
        emailacc = findViewById(R.id.emailacc);
        phoneacc = findViewById(R.id.phoneacc);
        dcacc = findViewById(R.id.dcacc);
        gtacc = findViewById(R.id.gtacc);
        imageViewloginacc = findViewById(R.id.imageViewloginacc);
        toolbar = findViewById(R.id.toolbar);
    }
}