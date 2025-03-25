package com.example.heavypath_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    // UI components
    private ImageView profileImage;
    private TextView usernameTextView;
    private TextView notificationSettings;
    private TextView languageSettings;
    private TextView securitySettings;
    private TextView aboutCreators;
    private Button logoutButton;

    // Firebase components
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Constants for image capture and selection
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;

    // Path for the captured photo
    private String currentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Enable the Up ("<") button in the Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize UI components
        profileImage = findViewById(R.id.profile_image);
        usernameTextView = findViewById(R.id.username);
        notificationSettings = findViewById(R.id.notification_settings);
        languageSettings = findViewById(R.id.language);
        securitySettings = findViewById(R.id.security);
        aboutCreators = findViewById(R.id.about_creators);
        logoutButton = findViewById(R.id.button_logout);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Fetch username from Firestore
        fetchUsername();

        // Set click listeners for UI components
        profileImage.setOnClickListener(v -> showImagePickerDialog());
        notificationSettings.setOnClickListener(v -> openNotificationSettings());
        languageSettings.setOnClickListener(v -> openLanguageSettings());
        securitySettings.setOnClickListener(v -> openSettingsActivity());
        aboutCreators.setOnClickListener(v -> openAboutCreators());

        // Log Out functionality
        logoutButton.setOnClickListener(v -> logout());
    }

    private void fetchUsername() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            db.collection("users").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("username");
                                usernameTextView.setText(username != null ? username : "Guest");
                            } else {
                                usernameTextView.setText("Guest");
                                Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            usernameTextView.setText("Guest");
                            Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            usernameTextView.setText("Guest");
        }
    }

    // Handle Up ("<") button click to navigate back to MainHomeActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(ProfileActivity.this, MainHomeActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Open SettingsActivity for security settings
    private void openSettingsActivity() {
        Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    // Show options to choose or capture an image
    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, (dialog, which) -> {
            if ("Take Photo".equals(options[which])) {
                takePhotoFromCamera();
            } else if ("Choose from Gallery".equals(options[which])) {
                choosePhotoFromGallery();
            } else {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    // Handle taking a photo from the camera
    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error while creating the image file", Toast.LENGTH_SHORT).show();
                return;
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.heavypath_project.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
        }
    }

    // Create a file to store captured image
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile("JPEG_" + timeStamp + "_", ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    // Handle choosing a photo from the gallery
    private void choosePhotoFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, PICK_IMAGE);
    }

    // Handle result from camera and gallery intents
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                File file = new File(currentPhotoPath);
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    profileImage.setImageURI(uri);
                } else {
                    Toast.makeText(this, "Error accessing captured image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PICK_IMAGE) {
                if (data != null && data.getData() != null) {
                    Uri selectedImage = data.getData();
                    profileImage.setImageURI(selectedImage);
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Action canceled", Toast.LENGTH_SHORT).show();
        }
    }

    // Log Out functionality
    private void logout() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Placeholder methods for other settings
    private void openNotificationSettings() {
        Toast.makeText(this, "Notification settings clicked", Toast.LENGTH_SHORT).show();
    }

    private void openLanguageSettings() {
        Toast.makeText(this, "Language settings clicked", Toast.LENGTH_SHORT).show();
    }

    private void openAboutCreators() {
        Toast.makeText(this, "About creators clicked", Toast.LENGTH_SHORT).show();
    }
}
