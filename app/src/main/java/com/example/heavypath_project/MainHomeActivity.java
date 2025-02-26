package com.example.heavypath_project;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainHomeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private Button settingsButton;
    private Button buttonAtButton;
    private Button plusButton;
    private Button chatButton;
    private Button profileButton;
    private Uri imageUri;
    private ImageButton buttonUploadImage;
    private ImageButton buttonCaptureImage;
    private AlertDialog postDialog;
    private RecyclerView recyclerViewAnnouncements;
    private AnnouncementAdapter announcementAdapter;
    private List<Announcement> announcementList = new ArrayList<>();

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
        settingsButton.setOnClickListener(v -> startActivity(new Intent(MainHomeActivity.this, SettingsActivity.class)));
        buttonAtButton.setOnClickListener(v -> startActivity(new Intent(MainHomeActivity.this, ButtonAtActivity.class)));
        plusButton.setOnClickListener(v -> openPostAnnouncementDialog());
        chatButton.setOnClickListener(v -> startActivity(new Intent(MainHomeActivity.this, ChatActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(MainHomeActivity.this, ProfileActivity.class)));

        // Initialize RecyclerView
        recyclerViewAnnouncements = findViewById(R.id.recyclerViewAnnouncements);
        recyclerViewAnnouncements.setLayoutManager(new LinearLayoutManager(this));
        announcementAdapter = new AnnouncementAdapter(announcementList);
        recyclerViewAnnouncements.setAdapter(announcementAdapter);
    }

    private void openPostAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_post_announcement, null);
        builder.setView(dialogView);

        final EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        final EditText editTextCarModel = dialogView.findViewById(R.id.editTextCarModel);
        final EditText editTextRentingPrice = dialogView.findViewById(R.id.editTextRentingPrice);
        final EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        buttonUploadImage = dialogView.findViewById(R.id.buttonUploadImage);
        buttonCaptureImage = dialogView.findViewById(R.id.buttonCaptureImage);
        Button buttonPost = dialogView.findViewById(R.id.buttonPost);

        buttonUploadImage.setOnClickListener(v -> openFileChooser());
        buttonCaptureImage.setOnClickListener(v -> captureImage());

        buttonPost.setOnClickListener(v -> postAnnouncement(editTextTitle, editTextCarModel, editTextRentingPrice, editTextDescription));

        postDialog = builder.create();
        postDialog.show();
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(this, "Camera permission is required to use camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            buttonUploadImage.setImageURI(imageUri);
        } else if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            buttonCaptureImage.setImageBitmap(imageBitmap);
            // You may want to save the bitmap or get the URI
        }
    }

    private void postAnnouncement(EditText editTextTitle, EditText editTextCarModel, EditText editTextRentingPrice, EditText editTextDescription) {
        String title = editTextTitle.getText().toString().trim();
        String carModel = editTextCarModel.getText().toString().trim();
        String rentingPrice = editTextRentingPrice.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            editTextTitle.setError("Title is required");
            return;
        }

        if (TextUtils.isEmpty(carModel)) {
            editTextCarModel.setError("Car model is required");
            return;
        }

        if (TextUtils.isEmpty(rentingPrice)) {
            editTextRentingPrice.setError("Renting price is required");
            return;
        }

        if (TextUtils.isEmpty(description)) {
            editTextDescription.setError("Description is required");
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please upload or capture an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new announcement and add it to the list
        Announcement announcement = new Announcement(title, carModel, rentingPrice, description, imageUri);
        announcementList.add(announcement);
        announcementAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Announcement posted", Toast.LENGTH_SHORT).show();

        // Dismiss the dialog
        if (postDialog != null) {
            postDialog.dismiss();
        }
    }
}
