package com.aqsoft.bookingappnoran.User;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.aqsoft.bookingappnoran.Adapter.KhachsanAdapter;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.Admin.ThemKhachSanActivity;
import com.aqsoft.bookingappnoran.model.Khachsan;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private KhachsanAdapter adapter;
    private List<Khachsan> dataList;
    private DatabaseReference mdatabase;
    private SearchView searchView;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private BottomNavigationView bottomNavigationView;
    private Intent intent;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        anhxa();
        bottomnavigation();
        hienthirecyclerview();
        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });


    }
    private void filter(String text) {
        List<Khachsan> filteredList = new ArrayList<>();

        for (Khachsan item : dataList) {
            if (item.getAddress().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        adapter.filterList(filteredList);
    }

    private void hienthirecyclerview() {
        dataList = new ArrayList<>();
        adapter = new KhachsanAdapter(dataList);
        recyclerView.setAdapter(adapter);
        DatabaseReference khachSanRef = mdatabase.child("KhachSan");
        khachSanRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot khachSanSnapshot : dataSnapshot.getChildren()) {
                    String name = khachSanSnapshot.child("Name").getValue(String.class);
                    String rating = khachSanSnapshot.child("Rating").getValue(String.class);
                    String key = khachSanSnapshot.child("Key").getValue(String.class);
                    String image = khachSanSnapshot.child("Image").getValue(String.class);
                    String Address = khachSanSnapshot.child("Address").getValue(String.class);
                    dataList.add(dataList.size(), new Khachsan(name, Address, "", rating, key,image,"",""));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mdatabase.child("TaiKhoan").child(uid).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    return;
                }
                String name2 = dataSnapshot.getValue(String.class);
                name.setText("Xin chào, \n" + name2);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void bottomnavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.person) {
                intent = new Intent(MainActivity.this, InfoActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.notification) {
                intent = new Intent(MainActivity.this, NotificationActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.settings) {
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
            }
            return true;
        });
    }

    private void anhxa() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        recyclerView = findViewById(R.id.recyclerView);
        mdatabase = FirebaseDatabase.getInstance().getReference();
        name = findViewById(R.id.name);

    }
}
