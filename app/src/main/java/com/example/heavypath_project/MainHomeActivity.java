package com.example.heavypath_project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainHomeActivity extends AppCompatActivity {

    private static final String TAG = "MainHomeActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private Button plusButton, chatButton, profileButton;
    private Uri imageUri;
    private AlertDialog postDialog;

    private RecyclerView recyclerViewSearchResults, recyclerViewAnnouncements;
    private UserAdapter userAdapter;
    private AnnouncementAdapter announcementAdapter;
    private List<User> userList; // For search results
    private List<Announcement> announcementList; // For announcements

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize buttons
        plusButton = findViewById(R.id.plus_button);
        chatButton = findViewById(R.id.chat_button);
        profileButton = findViewById(R.id.profile_button);

        // Initialize RecyclerView for search results
        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        recyclerViewSearchResults.setAdapter(userAdapter);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));

        // Initialize RecyclerView for announcements
        recyclerViewAnnouncements = findViewById(R.id.recyclerViewAnnouncements);
        announcementList = new ArrayList<>();
        announcementAdapter = new AnnouncementAdapter(this, announcementList);
        recyclerViewAnnouncements.setAdapter(announcementAdapter);
        recyclerViewAnnouncements.setLayoutManager(new LinearLayoutManager(this));

        // Configure SearchView for username search
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setQueryHint("Search by username");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    searchUsersByUsername(query); // Query Firestore for username
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    userList.clear(); // Clear search results
                    userAdapter.notifyDataSetChanged(); // Update RecyclerView
                }
                return true;
            }
        });

        // Button click listeners
        plusButton.setOnClickListener(v -> openPostAnnouncementDialog());
        chatButton.setOnClickListener(v -> startActivity(new Intent(MainHomeActivity.this, ChatActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(MainHomeActivity.this, ProfileActivity.class)));

        // Load announcements from Firestore
        loadAnnouncements();
    }

    // Search Firestore by username
    private void searchUsersByUsername(String username) {
        db.collection("users")
                .whereEqualTo("username", username) // Match 'username' field in Firestore
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        userList.clear(); // Clear previous search results
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            User user = document.toObject(User.class); // Convert document to User object
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged(); // Update RecyclerView with new results
                    } else {
                        Toast.makeText(this, "Error searching users: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Load announcements from Firestore
    private void loadAnnouncements() {
        db.collection("announcements")
                .orderBy("timestamp") // Sort by timestamp
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        announcementList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Announcement announcement = document.toObject(Announcement.class);
                            announcementList.add(announcement);
                        }
                        announcementAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                    } else {
                        Toast.makeText(this, "Error loading announcements: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Open dialog to post a new announcement
    private void openPostAnnouncementDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_post_announcement, null);
        builder.setView(dialogView);

        final EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        final EditText editTextCarModel = dialogView.findViewById(R.id.editTextCarModel);
        final EditText editTextRentingPrice = dialogView.findViewById(R.id.editTextRentingPrice);
        final EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        ImageButton buttonUploadImage = dialogView.findViewById(R.id.buttonUploadImage);
        ImageButton buttonCaptureImage = dialogView.findViewById(R.id.buttonCaptureImage);
        Button buttonPost = dialogView.findViewById(R.id.buttonPost);

        buttonUploadImage.setOnClickListener(v -> openFileChooser());
        buttonCaptureImage.setOnClickListener(v -> captureImage());

        buttonPost.setOnClickListener(v -> postAnnouncement(editTextTitle, editTextCarModel, editTextRentingPrice, editTextDescription));

        postDialog = builder.create();
        postDialog.show();
    }

    // File chooser for image upload
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Capture image using camera
    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Post an announcement with validation
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

        Announcement announcement = new Announcement(imageUri.toString(), title, carModel, rentingPrice, description);
        db.collection("announcements")
                .add(announcement)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Announcement posted successfully", Toast.LENGTH_SHORT).show();
                        loadAnnouncements(); // Refresh RecyclerView
                    } else {
                        Toast.makeText(this, "Error posting announcement: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        if (postDialog != null) {
            postDialog.dismiss();
        }
    }

    // Handle results from intents (e.g., image picker or camera capture)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // Image picked from gallery
                imageUri = data.getData();
                Toast.makeText(this, "Image selected successfully!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null && data.getExtras() != null) {
                // Image captured with camera
                if (data.getData() != null) {
                    imageUri = data.getData();
                } else if (data.getExtras().get("data") != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imageUri = getImageUriFromBitmap(photo); // Convert Bitmap to Uri
                    Toast.makeText(this, "Image captured successfully!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Action cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    // Convert Bitmap to Uri
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "CapturedImage", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open camera
                captureImage();
            } else {
                Toast.makeText(this, "Camera permission is required to capture images.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



