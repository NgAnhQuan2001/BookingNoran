package com.aqsoft.bookingappnoran.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.aqsoft.bookingappnoran.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminNotificationActivity extends AppCompatActivity {
    private ListView messagesListView;
    private ArrayList<String> messagesList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference MesRef;
    private ImageView backtbadm;

    private ArrayList<DataSnapshot> snapshotList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notification);

        backtbadm = findViewById(R.id.back_thongbao_adm);

        messagesListView = findViewById(R.id.messagesListView);
        messagesList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messagesList);
        messagesListView.setAdapter(adapter);

        snapshotList = new ArrayList<>();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        MesRef = database.getReference("Gopy");

        setupMessageListener();

        backtbadm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setupMessageListener() {
        MesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messagesList.clear();
                snapshotList.clear();
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    snapshotList.add(0, messageSnapshot);
                    String messageText = messageSnapshot.child("Message").getValue(String.class);
                    String senderUid = messageSnapshot.child("UserId").getValue(String.class);
                    String senderEmail = messageSnapshot.child("Email").getValue(String.class);

                    if (messageText != null) {
                        String displayText = "Đến từ Id: " + senderUid + "\nMessage: " + messageText + "\nEmail: " + senderEmail ;
                        messagesList.add(0, displayText);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminNotificationActivity.this, "Failed to load messages.", Toast.LENGTH_SHORT).show();
            }
        });

        messagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = messagesList.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminNotificationActivity.this);
                builder.setTitle("Chọn hành động");
                builder.setMessage("Bạn muốn làm gì với thông báo này?");
                builder.setPositiveButton("Chuyển đến danh sách tài khoản", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(AdminNotificationActivity.this, ListAccActivity.class));
                    }
                });

                builder.setNegativeButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataSnapshot selectedSnapshot = snapshotList.get(position);
                        selectedSnapshot.getRef().removeValue();
                        messagesList.remove(position);
                        snapshotList.remove(position);
                        adapter.notifyDataSetChanged();

                        Toast.makeText(AdminNotificationActivity.this, "Đã hủy bỏ", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.create().show();
            }
        });

    }

}