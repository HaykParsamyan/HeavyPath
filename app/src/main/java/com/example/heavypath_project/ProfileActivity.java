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

    private ImageView profileImage;
    private TextView usernameTextView;
    private TextView notificationSettings;
    private TextView languageSettings;
    private TextView securitySettings;
    private TextView aboutCreators;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;
    private String currentPhotoPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = findViewById(R.id.profile_image);
        usernameTextView = findViewById(R.id.username);
        notificationSettings = findViewById(R.id.notification_settings);
        languageSettings = findViewById(R.id.language);
        securitySettings = findViewById(R.id.security);
        aboutCreators = findViewById(R.id.about_creators);

        // Set username
        String username = getIntent().getStringExtra("USERNAME");
        if (username != null) {
            usernameTextView.setText(username);
        }

        // Set onClickListeners for settings
        profileImage.setOnClickListener(v -> showImagePickerDialog());
        notificationSettings.setOnClickListener(v -> openNotificationSettings());
        languageSettings.setOnClickListener(v -> openLanguageSettings());
        securitySettings.setOnClickListener(v -> openSecuritySettings());
        aboutCreators.setOnClickListener(v -> openAboutCreators());
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, (dialog, which) -> {
            switch (options[which]) {
                case "Take Photo":
                    takePhotoFromCamera();
                    break;
                case "Choose from Gallery":
                    choosePhotoFromGallery();
                    break;
                case "Cancel":
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private void takePhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error occurred while creating the file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.heavypath_project.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                "JPEG_" + timeStamp + "_",
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void choosePhotoFromGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            File file = new File(currentPhotoPath);
            Uri uri = Uri.fromFile(file);
            profileImage.setImageURI(uri);
        } else if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            profileImage.setImageURI(selectedImage);
        }
    }

    private void openNotificationSettings() {
        // Add code to open notification settings
        Toast.makeText(this, "Notification settings clicked", Toast.LENGTH_SHORT).show();
    }

    private void openLanguageSettings() {
        // Add code to open language settings
        Toast.makeText(this, "Language settings clicked", Toast.LENGTH_SHORT).show();
    }

    private void openSecuritySettings() {
        // Add code to open security settings
        Toast.makeText(this, "Security settings clicked", Toast.LENGTH_SHORT).show();
    }

    private void openAboutCreators() {
        // Add code to open about creators screen
        Toast.makeText(this, "About creators clicked", Toast.LENGTH_SHORT).show();
    }
}
