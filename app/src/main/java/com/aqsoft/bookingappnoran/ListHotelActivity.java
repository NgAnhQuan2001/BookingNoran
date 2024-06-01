package com.aqsoft.bookingappnoran;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toolbar;

import com.aqsoft.bookingappnoran.Adapter.PartnerListHotelAdapter;
import com.aqsoft.bookingappnoran.model.Khachsan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListHotelActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Khachsan> hotelList;
    private PartnerListHotelAdapter adapter;
    private DatabaseReference databaseReference;
    private Toolbar toolbar;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_hotel);
        recyclerView = findViewById(R.id.recyclerView);
        hotelList = new ArrayList<>();
        adapter = new PartnerListHotelAdapter( hotelList);
        recyclerView.setAdapter(adapter);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        uid = firebaseAuth.getCurrentUser().getUid();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> {
            finish();
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("KhachSan");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hotelList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Khachsan khachsan = dataSnapshot.getValue(Khachsan.class);
                    if (khachsan.getUid().equals(uid)) {
                        hotelList.add(khachsan);
                    }
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}