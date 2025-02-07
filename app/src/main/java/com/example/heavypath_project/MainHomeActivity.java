package com.example.heavypath_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainHomeActivity extends AppCompatActivity {

    private Button settingsButton;
    private Button buttonAtButton;
    private Button plusButton;
    private Button chatButton;
    private Button profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        // Initialize buttons
        settingsButton = findViewById(R.id.settings_button);
        buttonAtButton = findViewById(R.id.button_at);
        plusButton = findViewById(R.id.plus_button);
        chatButton = findViewById(R.id.chat_button);
        profileButton = findViewById(R.id.profile_button);

        // Set click listeners
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to SettingsActivity
                Intent intent = new Intent(MainHomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        buttonAtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ButtonAtActivity
                Intent intent = new Intent(MainHomeActivity.this, ButtonAtActivity.class);
                startActivity(intent);
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to PostAnnouncementActivity
                Intent intent = new Intent(MainHomeActivity.this, PostAnnouncementActivity.class);
                startActivity(intent);
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ChatActivity
                Intent intent = new Intent(MainHomeActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ProfileActivity
                Intent intent = new Intent(MainHomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
