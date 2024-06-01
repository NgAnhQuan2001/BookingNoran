package com.aqsoft.bookingappnoran.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.aqsoft.bookingappnoran.R;

public class AppInformationActivity extends AppCompatActivity {
    private ImageView btt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_information);
        btt = findViewById(R.id.btt);
        btt.setOnClickListener(v -> {
            finish();
        });
    }
}