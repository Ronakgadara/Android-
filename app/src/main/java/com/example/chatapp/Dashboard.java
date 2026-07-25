package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class Dashboard extends AppCompatActivity {

    CardView profileCard, settingsCard,chatCard;
    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        profileCard = findViewById(R.id.cardProfile);
        settingsCard = findViewById(R.id.cardSettings);
        chatCard = findViewById(R.id.cardChat);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        profileCard.setOnClickListener(view -> {
            startActivity(new Intent(Dashboard.this, Profile.class));
        });

        settingsCard.setOnClickListener(view -> {
            startActivity(new Intent(Dashboard.this, Settings.class));
        });

        chatCard.setOnClickListener(view -> {
            startActivity(new Intent(Dashboard.this, ChatUserList.class));
        });

        if (user == null)
        {
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
            finish();
        }
    }
}