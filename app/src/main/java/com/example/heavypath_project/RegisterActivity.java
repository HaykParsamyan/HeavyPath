package com.example.heavypath_project;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private TextView usernameErrorTextView; // TextView to display errors
    private Button registerButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize UI components
        usernameEditText = findViewById(R.id.username);
        usernameErrorTextView = findViewById(R.id.username_error); // TextView for inline errors
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
            usernameErrorTextView.setVisibility(View.GONE);
            passwordEditText.setError("Passwords do not match");
            return;
        }

        // Check if username is unique
        checkUsernameUniqueness(username, email, password);
    }

    private void checkUsernameUniqueness(String username, String email, String password) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            // Username already exists
                            usernameErrorTextView.setText("Username is already taken. Please choose another one.");
                            usernameErrorTextView.setVisibility(View.VISIBLE);
                        } else {
                            // Username is unique
                            usernameErrorTextView.setVisibility(View.GONE); // Hide error if username is valid
                            createFirebaseUser(email, password, username);
                        }
                    } else {
                        Log.e(TAG, "Error checking username uniqueness: " + task.getException().getMessage());
                        usernameErrorTextView.setText("Error checking username. Please try again.");
                        usernameErrorTextView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void createFirebaseUser(String email, String password, String username) {
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
                usernameErrorTextView.setText("Registration failed: " + task.getException().getMessage());
                usernameErrorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void sendEmailVerification(FirebaseUser firebaseUser) {
        firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                usernameErrorTextView.setVisibility(View.GONE);
                Toast.makeText(this, "Verification email sent to " + firebaseUser.getEmail(), Toast.LENGTH_LONG).show();
            } else {
                Log.e(TAG, "Error sending email verification: " + task.getException().getMessage());
                usernameErrorTextView.setText("Failed to send verification email.");
                usernameErrorTextView.setVisibility(View.VISIBLE);
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

        // Save user data to Firestore at /users/{userId}
        db.collection("users").document(userId).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                initializeAnnouncementSubcollection(userId); // Initialize the announcement subcollection
            } else {
                Log.e(TAG, "Error saving user data: " + task.getException().getMessage());
                usernameErrorTextView.setText("Error saving user data: " + task.getException().getMessage());
                usernameErrorTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initializeAnnouncementSubcollection(String userId) {
        // Create an empty subcollection named "announcement"
        Map<String, Object> emptyAnnouncement = new HashMap<>();
        emptyAnnouncement.put("initialized", true); // Optional initialization flag

        db.collection("users")
                .document(userId)
                .collection("announcement")
                .document("initial")
                .set(emptyAnnouncement)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        usernameErrorTextView.setVisibility(View.GONE);
                        Toast.makeText(this, "User registered successfully! Announcement section initialized.", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e(TAG, "Error initializing announcement subcollection: " + task.getException().getMessage());
                        usernameErrorTextView.setText("Error initializing announcements: " + task.getException().getMessage());
                        usernameErrorTextView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private String hashPassword(String password) {
        // Hash the password (example using Base64; replace with a secure hash algorithm like BCrypt in production)
        return Base64.encodeToString(password.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT).trim();
    }
}

