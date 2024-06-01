package com.aqsoft.bookingappnoran.Partner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.util.Log;
import android.widget.Toolbar;

import com.aqsoft.bookingappnoran.Adapter.LoaiPhongAdapter;
import com.aqsoft.bookingappnoran.Adapter.PartnerLoaiPhongAdapter;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.model.LoaiPhongmodel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PartnerListRoomActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DatabaseReference mdatabase;
    private RecyclerView recyclerView;
    private PartnerLoaiPhongAdapter adapter;
    private String Key;
    private List<LoaiPhongmodel> dataList;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_list_room);
        anhxa();
        toolbar.setOnClickListener(v -> {
            finish();
        });
        Intent intent = getIntent();
        Key = intent.getStringExtra("Key");
        getKey();
        fab.setOnClickListener(v -> {
            Intent intent1 = new Intent(PartnerListRoomActivity.this, PartnerAddRoomActivity.class);
            intent1.putExtra("Key", Key);
            startActivity(intent1);
        });


    }

    private void anhxa() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.fab);

    }
    private void getKey() {
        dataList = new ArrayList<>();
        adapter = new PartnerLoaiPhongAdapter(dataList);
        recyclerView.setAdapter(adapter);

        mdatabase = FirebaseDatabase.getInstance().getReference().child("KhachSan").child(Key).child("LoaiPhong");
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot khachSanSnapshot : dataSnapshot.getChildren()) {
                    String name = khachSanSnapshot.getKey();
                    String motangan = khachSanSnapshot.child("Motangan").getValue(String.class);
                    String image = khachSanSnapshot.child("Image").getValue(String.class);
                    dataList.add(dataList.size(),new LoaiPhongmodel(name,motangan,"","","","","",Key,image));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FirebaseError", databaseError.getMessage());
            }
        });
    }
}