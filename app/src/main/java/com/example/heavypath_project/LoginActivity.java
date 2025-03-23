package com.example.heavypath_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.content.Intent;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;
    private TextView forgotPasswordTextView; // New TextView for "Forgot Password"

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize UI components
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        forgotPasswordTextView = findViewById(R.id.forgot_password); // Forgot Password

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Login button click listener
        loginButton.setOnClickListener(v -> loginUser());

        // Register button click listener
        registerButton.setOnClickListener(v -> {
            // Navigate to RegisterActivity
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Forgot password click listener
        forgotPasswordTextView.setOnClickListener(v -> sendPasswordResetEmail());
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate email and password input fields
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }

        // Authenticate user with Firebase
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Login successful
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                // Navigate to MainHomeActivity
                Intent intent = new Intent(LoginActivity.this, MainHomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Login failed
                String errorMessage = task.getException().getMessage();
                Log.e(TAG, "Login failed: " + errorMessage);
                Toast.makeText(LoginActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendPasswordResetEmail() {
        String email = emailEditText.getText().toString().trim();

        // Validate email input
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Enter your email to reset your password");
            return;
        }

        // Firebase Password Reset Email
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(LoginActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
            } else {
                String errorMessage = task.getException().getMessage();
                Log.e(TAG, "Failed to send password reset email: " + errorMessage);
                Toast.makeText(LoginActivity.this, "Failed to send reset email: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }
}
