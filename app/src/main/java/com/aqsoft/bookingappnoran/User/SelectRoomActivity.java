package com.aqsoft.bookingappnoran.User;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;
import com.aqsoft.bookingappnoran.Adapter.LoaiPhongAdapter;
import com.aqsoft.bookingappnoran.R;
import com.aqsoft.bookingappnoran.model.LoaiPhongmodel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class SelectRoomActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DatabaseReference mdatabase;
    private RecyclerView recyclerView;
    private LoaiPhongAdapter adapter;
    private String Key;
    private List<LoaiPhongmodel> dataList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_room);
        Intent intent = getIntent();
        Key = intent.getStringExtra("Key");
        anhxa();
        getKey();
        back();


    }

    private void back() {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getKey() {
        dataList = new ArrayList<>();
        adapter = new LoaiPhongAdapter(dataList);
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


    private void anhxa() {
        recyclerView = findViewById(R.id.recyclerView);
        toolbar = findViewById(R.id.toolbar);
    }
}