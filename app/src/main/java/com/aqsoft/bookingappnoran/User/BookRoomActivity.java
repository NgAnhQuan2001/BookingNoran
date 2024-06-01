package com.aqsoft.bookingappnoran.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aqsoft.bookingappnoran.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookRoomActivity extends AppCompatActivity {
    Calendar myCalendar;
    private String Key;
    private String Room;
    private TextView tv_tenloaiks_chonphong;

    private AppCompatButton btnsaver, btnhuyp;
    private String priceroom,nameks;
    private EditText edtname, tvdate12, tvdate22,edtAdditionalInput;
    private TextView tvTenKSChonPhong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_room);
        myCalendar = Calendar.getInstance();
        anhxa();
        getintent();
        tv_tenloaiks_chonphong.setText(Room);
        getthongtinks();
        xulyngay();
        btnhuyp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnsaver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtname.getText().toString();
                String date1 = tvdate12.getText().toString();
                String date2 = tvdate22.getText().toString();
                String additionalInput = edtAdditionalInput.getText().toString();
                if (name.isEmpty() || date1.isEmpty() || date2.isEmpty()) {
                    Toast.makeText(BookRoomActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(BookRoomActivity.this, ConfirmBookActivity.class);
                    intent.putExtra("Soluong", name);
                    intent.putExtra("Date1", date1);
                    intent.putExtra("Date2", date2);
                    intent.putExtra("AdditionalInput", additionalInput);
                    intent.putExtra("Key", Key);
                    intent.putExtra("Room", Room);
                    intent.putExtra("Price", priceroom);
                    intent.putExtra("NameKS", nameks);
                    startActivity(intent);
                }
            }
        });







    }

    private void xulyngay() {
        DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                // ctrl + space
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar, tvdate12);
            }
        };

        tvdate12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(BookRoomActivity.this, date1, myCalendar.get(Calendar.YEAR), myCalendar
                        .get(Calendar.MONTH), myCalendar
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                // ctrl + space
                Calendar selectedDate = Calendar.getInstance();
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar , tvdate22);

                if (selectedDate.before(myCalendar) || selectedDate.equals(myCalendar)) {
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel(myCalendar, tvdate22);
                } else {
                    Toast.makeText(BookRoomActivity.this, "Ngày không hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        };

        tvdate22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(BookRoomActivity.this, date2, myCalendar.get(Calendar.YEAR), myCalendar
                        .get(Calendar.MONTH), myCalendar
                        .get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void getthongtinks() {
        DatabaseReference Myref = FirebaseDatabase.getInstance().getReference().child("KhachSan");
        Myref.child(Key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nameks = snapshot.child("Name").getValue(String.class);
                tvTenKSChonPhong.setText(nameks);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getintent() {
        Key = getIntent().getStringExtra("Key");
        Room = getIntent().getStringExtra("Room");
        priceroom = getIntent().getStringExtra("Price");
    }

    private void anhxa() {
        edtAdditionalInput = findViewById(R.id.edtAdditionalInput);
        tvdate12 = findViewById(R.id.tvdate1);
        tvdate22 = findViewById(R.id.tvdate2);
        btnsaver = findViewById(R.id.btn_svchonphong);
        btnhuyp = findViewById(R.id.btn_huychonphong);
        edtname = findViewById(R.id.edtname);

        tvTenKSChonPhong = findViewById(R.id.tv_tenks_chonphong);
        tv_tenloaiks_chonphong = findViewById(R.id.tv_tenloaiks_chonphong);




    }
    private void updateLabel(Calendar myCalendar, EditText tvdate12){
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        tvdate12.setText(sdf.format(myCalendar.getTime()));
    }
}