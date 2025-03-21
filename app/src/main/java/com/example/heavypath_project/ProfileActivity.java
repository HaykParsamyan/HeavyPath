package com.example.heavypath_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

    // Constants for image capture and selection
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;

    // Path for the captured photo
    private String currentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        profileImage = findViewById(R.id.profile_image);
        usernameTextView = findViewById(R.id.username);
        notificationSettings = findViewById(R.id.notification_settings);
        languageSettings = findViewById(R.id.language);
        securitySettings = findViewById(R.id.security);
        aboutCreators = findViewById(R.id.about_creators);

        // Set username from intent extras
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            usernameTextView.setText(username);
        } else {
            usernameTextView.setText("Guest"); // Fallback if username is null
        }

        // Set click listeners for UI components
        profileImage.setOnClickListener(v -> showImagePickerDialog());
        notificationSettings.setOnClickListener(v -> openNotificationSettings());
        languageSettings.setOnClickListener(v -> openLanguageSettings());
        securitySettings.setOnClickListener(v -> openSecuritySettings());
        aboutCreators.setOnClickListener(v -> openAboutCreators());
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
                dialog.dismiss(); // Cancel
            }
        });
        builder.show();
    }

    // Handle taking a photo from the camera
    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create file for storing the captured image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error while creating the image file", Toast.LENGTH_SHORT).show();
                return;
            }
            // Launch camera intent
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
        File image = File.createTempFile(
                "JPEG_" + timeStamp + "_", // Prefix
                ".jpg", // Suffix
                storageDir // Directory
        );
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
                // Handle captured photo
                File file = new File(currentPhotoPath);
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    profileImage.setImageURI(uri);
                } else {
                    Toast.makeText(this, "Error accessing captured image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PICK_IMAGE) {
                // Handle selected photo from gallery
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

    // Open notification settings (placeholder)
    private void openNotificationSettings() {
        Toast.makeText(this, "Notification settings clicked", Toast.LENGTH_SHORT).show();
    }

    // Open language settings (placeholder)
    private void openLanguageSettings() {
        Toast.makeText(this, "Language settings clicked", Toast.LENGTH_SHORT).show();
    }

    // Open security settings (placeholder)
    private void openSecuritySettings() {
        Toast.makeText(this, "Security settings clicked", Toast.LENGTH_SHORT).show();
    }

    // Open about creators (placeholder)
    private void openAboutCreators() {
        Toast.makeText(this, "About creators clicked", Toast.LENGTH_SHORT).show();
    }
}
