package com.example.heavypath_project;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private TextView changePasswordOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize UI components
        changePasswordOption = findViewById(R.id.change_password_option);

        // Open change password dialog on click
        changePasswordOption.setOnClickListener(v -> openChangePasswordDialog());
    }

    private void openChangePasswordDialog() {
        ChangePasswordDialog dialog = new ChangePasswordDialog();
        dialog.show(getSupportFragmentManager(), "ChangePasswordDialog");
    }
}
