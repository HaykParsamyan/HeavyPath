package com.example.heavypath_project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    private Button plusButton;
    private Button chatButton;
    private Button profileButton;
    private Uri imageUri;
    private ImageButton buttonUploadImage;
    private ImageButton buttonCaptureImage;
    private AlertDialog postDialog;

    private RecyclerView recyclerView;
    private AnnouncementAdapter adapter;
    private List<Announcement> announcementList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        // Initialize buttons
        plusButton = findViewById(R.id.plus_button);
        chatButton = findViewById(R.id.chat_button);
        profileButton = findViewById(R.id.profile_button);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAnnouncements);
        announcementList = new ArrayList<>();
        adapter = new AnnouncementAdapter(this, announcementList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set click listeners
        plusButton.setOnClickListener(v -> openPostAnnouncementDialog());
        chatButton.setOnClickListener(v -> {
            // Navigate to ChatActivity
            Intent intent = new Intent(MainHomeActivity.this, ChatActivity.class);
            startActivity(intent);
        });
        profileButton.setOnClickListener(v -> {
            // Navigate to ProfileActivity
            Intent intent = new Intent(MainHomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
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

        // Add new announcement
        Announcement announcement = new Announcement(imageUri, title, carModel, rentingPrice, description);
        announcementList.add(announcement);
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Announcement posted", Toast.LENGTH_SHORT).show();

        if (postDialog != null) {
            postDialog.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null && data.getExtras() != null) {
                imageUri = data.getData();
            }
        }
    }
}
