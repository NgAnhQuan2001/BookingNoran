package com.aqsoft.bookingappnoran.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.User.InfoActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateInfoActivity extends AppCompatActivity {
    private DatabaseReference Myref = FirebaseDatabase.getInstance().getReference("TaiKhoan");
    private EditText nameacc,emailacc,phoneacc,dcacc,gtacc,matkhau;
    private Button imageViewloginacc;
    private Toolbar toolbar;
    private TextView quyenhan;
    private Spinner spinnerUserType;
    private String uid;
    private String selectedItem;
    private ImageView delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

        anhxa();
        getintent();
        spinner();
        getthongtin();
        suathongtin();
        deleteks();
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void deleteks() {
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateInfoActivity.this);
                builder.setTitle("Xóa tài khoản");
                builder.setMessage("Bạn có chắc chắn muốn xóa tài khoản này không?");
                builder.setPositiveButton("Có", (dialogInterface, i) -> {
                    Myref.child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("KhachSan");
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            if(dataSnapshot.child("uid").getValue(String.class).equals(uid)){
                                                dataSnapshot.getRef().removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                Toast.makeText(UpdateInfoActivity.this, "Xóa tài khoản thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(UpdateInfoActivity.this, "Xóa tài khoản thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                });
                builder.setNegativeButton("Không", (dialogInterface, i) -> {
                });
                builder.show();
            }
        });
    }

    private void getintent() {
        Intent intent = getIntent();
        uid = intent.getStringExtra("Uid");
    }

    private void spinner() {
        spinnerUserType = findViewById(R.id.spinnerUserType);
        List<String> userTypes = new ArrayList<>();
        userTypes.add("Người dùng");
        userTypes.add("Đối tác");
        userTypes.add("Admin");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);

        spinnerUserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedItem = userTypes.get(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
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
                if(selectedItem.equals("Người dùng"))
                    data.put("role", "user");
                else if(selectedItem.equals("Đối tác"))
                    data.put("role", "doitac");
                else if(selectedItem.equals("Admin"))
                    data.put("role", "admin");
                else
                    data.put("role", "user");

                Myref.child(uid).updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(UpdateInfoActivity.this, "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(UpdateInfoActivity.this, "Thay đổi thông tin thất bại", Toast.LENGTH_SHORT).show();
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
                String quyenhan2 = snapshot.child("role").getValue(String.class);
                if(quyenhan2.equals("user"))
                    spinnerUserType.setSelection(0);
                else if(quyenhan2.equals("doitac"))
                    spinnerUserType.setSelection(1);
                else if(quyenhan2.equals("admin"))
                    spinnerUserType.setSelection(2);
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
        matkhau = findViewById(R.id.matkhau);
        phoneacc = findViewById(R.id.phoneacc);
        dcacc = findViewById(R.id.dcacc);
        gtacc = findViewById(R.id.gtacc);
        imageViewloginacc = findViewById(R.id.imageViewloginacc);
        toolbar = findViewById(R.id.toolbar);
    }
}