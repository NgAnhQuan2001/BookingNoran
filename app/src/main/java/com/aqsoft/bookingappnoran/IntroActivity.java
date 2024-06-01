package com.aqsoft.bookingappnoran;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.aqsoft.bookingappnoran.Verification.LoginActivity;

public class IntroActivity extends AppCompatActivity {
    private TextView startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        startBtn = findViewById(R.id.startBtn);
        startBtn.setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });


    }
}