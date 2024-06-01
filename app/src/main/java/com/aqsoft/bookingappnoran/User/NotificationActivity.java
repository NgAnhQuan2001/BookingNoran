package com.aqsoft.bookingappnoran.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toolbar;


import com.aqsoft.bookingappnoran.Adapter.ThongBaoAdapter;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.model.Thongbaomodel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {
    private RecyclerView recyclerview;
    private List<Thongbaomodel> data;
    private ThongBaoAdapter Adapter;
    private Toolbar toolbar;
    private DatabaseReference reference;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        recyclerview = findViewById(R.id.recyclerview);
        data = new ArrayList<>();
        Adapter = new ThongBaoAdapter(data);
        recyclerview.setAdapter(Adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        reference = FirebaseDatabase.getInstance().getReference().child("Lichsu").child(uid);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> {
            finish();
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                data.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Thongbaomodel thongbaomodel = dataSnapshot.getValue(Thongbaomodel.class);
                    data.add(thongbaomodel);
                }
                Adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







    }
}