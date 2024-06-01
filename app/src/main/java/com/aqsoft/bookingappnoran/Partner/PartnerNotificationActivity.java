package com.aqsoft.bookingappnoran.Partner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toolbar;

import com.aqsoft.bookingappnoran.Adapter.PartnerThongBaoAdapter;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.model.Partnerthongbaomodel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PartnerNotificationActivity extends AppCompatActivity {
    private List<Partnerthongbaomodel> mData;
    private PartnerThongBaoAdapter adapter;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Hoadon");
    private List<String> targetUids = new ArrayList<>();
    private String uid = FirebaseAuth.getInstance().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_notification);
        initView();
        getUidsOfUser();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> finish());

        mData = new ArrayList<>();
        adapter = new PartnerThongBaoAdapter(mData);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getUidsOfUser() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("KhachSan");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                targetUids.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uidks = dataSnapshot.child("uid").getValue(String.class);
                    if(uidks != null && uidks.equals(uid)) {
                        String key = dataSnapshot.child("Key").getValue(String.class);
                        if (key != null) {
                            targetUids.addAll(Arrays.asList(key.split(",")));
                            Log.d("TAG", "onDataChange: " + targetUids);
                        }
                    }
                }
                initData(); // Gọi initData() ở đây
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle potential errors
            }
        });
    }

    private void initData() {
        mData.clear();
        for (String targetUid : targetUids) {
            reference.child(targetUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Partnerthongbaomodel model = dataSnapshot.getValue(Partnerthongbaomodel.class);
                        mData.add(model);
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle potential errors
                }
            });
        }
    }
    }

