package com.example.heavypath_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_button);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Register button click listener
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(username)) {
            usernameEditText.setError("Username is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Email is required");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Please confirm your password");
            return;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "User created successfully");
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                if (firebaseUser != null) {
                    String userId = firebaseUser.getUid();
                    sendEmailVerification(firebaseUser); // Send email verification
                    saveUserToFirestore(userId, username, email, hashPassword(password));
                }
            } else {
                Log.e(TAG, "Registration failed: " + task.getException().getMessage());
                Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendEmailVerification(FirebaseUser firebaseUser) {
        firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "Error sending email verification: " + task.getException().getMessage());
                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserToFirestore(String userId, String username, String email, String hashedPassword) {
        // Create user details map
        Map<String, Object> user = new HashMap<>();
        user.put("userId", userId); // Store UID
        user.put("username", username);
        user.put("email", email);
        user.put("password", hashedPassword); // Store hashed password

        db.collection("users").document(userId).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "User registered successfully! Please verify your email.", Toast.LENGTH_SHORT).show();
                fetchUsername(userId); // Fetch the username after saving
            } else {
                Log.e(TAG, "Error saving user data: " + task.getException().getMessage());
                Toast.makeText(this, "Error saving user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String hashPassword(String password) {
        // Hash the password (example using Base64; replace with a secure hash algorithm like BCrypt in production)
        return Base64.encodeToString(password.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT).trim();
    }

    private void fetchUsername(String userId) {
        db.collection("users").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String username = document.getString("username");
                    Toast.makeText(this, "Welcome, " + (username != null ? username : "User") + "!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "User document does not exist");
                    Toast.makeText(this, "Failed to retrieve username.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Error fetching username: " + task.getException().getMessage());
                Toast.makeText(this, "Error fetching username: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
