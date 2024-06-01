package com.aqsoft.bookingappnoran.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import android.widget.Toolbar;

import com.aqsoft.bookingappnoran.Adapter.AccountAdapter;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.model.UserModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ListAccActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AccountAdapter adapter;
    private List<UserModel> dataList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_acc);
        recyclerView = findViewById(R.id.recyclerView);
        dataList = new ArrayList<>(); // Khởi tạo dataList
        adapter = new AccountAdapter(dataList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("TaiKhoan");
        ref.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                dataList.clear();
                // Xử lý dữ liệu từ Firebase và thêm vào dataList
                for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    dataList.add(user);
                }
                adapter.notifyDataSetChanged(); // Thông báo cho adapter biết dữ liệu đã thay đổi
            } else {
                // Xử lý khi không thành công (nếu cần)
            }
        });

        toolbar = findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> finish());
    }
}