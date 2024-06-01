package com.aqsoft.bookingappnoran.User;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.aqsoft.bookingappnoran.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    private ImageView bbm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        bbm = findViewById(R.id.bbm);
        bbm.setOnClickListener(v -> {
            finish();
        });
    }
}