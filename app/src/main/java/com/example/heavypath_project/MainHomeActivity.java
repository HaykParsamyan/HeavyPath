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

public class MainHomeActivity extends AppCompatActivity implements AnnouncementRefresher {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private Button plusButton, chatButton, profileButton;
    private Uri imageUri;
    private AlertDialog postDialog;

    private RecyclerView recyclerViewSearchResults, recyclerViewAnnouncements;
    private UserAdapter userAdapter;
    private AnnouncementAdapter announcementAdapter;
    private List<User> userList;
    private List<Announcement> announcementList;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        db = FirebaseFirestore.getInstance();

        plusButton = findViewById(R.id.plus_button);
        chatButton = findViewById(R.id.chat_button);
        profileButton = findViewById(R.id.profile_button);

        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(this, userList);
        recyclerViewSearchResults.setAdapter(userAdapter);
        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewAnnouncements = findViewById(R.id.recyclerViewAnnouncements);
        announcementList = new ArrayList<>();
        announcementAdapter = new AnnouncementAdapter(this, announcementList);
        recyclerViewAnnouncements.setAdapter(announcementAdapter);
        recyclerViewAnnouncements.setLayoutManager(new LinearLayoutManager(this));

        SearchView searchView = findViewById(R.id.search_view);
        searchView.setQueryHint("Search by username");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    searchUsersByUsername(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    userList.clear();
                    userAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

        plusButton.setOnClickListener(v -> openPostAnnouncementDialog());
        chatButton.setOnClickListener(v -> startActivity(new Intent(MainHomeActivity.this, ChatActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(MainHomeActivity.this, ProfileActivity.class)));

        loadAnnouncements();
    }

    private void searchUsersByUsername(String username) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        userList.clear();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            User user = document.toObject(User.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error searching user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void loadAnnouncements() {
        db.collection("announcements")
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        announcementList.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            Announcement announcement = document.toObject(Announcement.class);
                            announcementList.add(announcement);
                        }
                        announcementAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error loading announcements: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
        ImageButton buttonUploadImage = dialogView.findViewById(R.id.buttonUploadImage);
        ImageButton buttonCaptureImage = dialogView.findViewById(R.id.buttonCaptureImage);
        Button buttonPost = dialogView.findViewById(R.id.buttonPost);

        buttonUploadImage.setOnClickListener(v -> openFileChooser());
        buttonCaptureImage.setOnClickListener(v -> captureImage());

        buttonPost.setOnClickListener(v -> postAnnouncement(editTextTitle, editTextCarModel, editTextRentingPrice, editTextDescription));

        postDialog = builder.create();
        postDialog.show();
    }

    private void fetchAnnouncements() {
        db.collection("announcements")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.e("Firebase", "Error fetching announcements", e);
                        return;
                    }

                    announcementList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        announcementList.add(doc.toObject(Announcement.class));
                    }
                    announcementAdapter.notifyDataSetChanged();
                });
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
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show();
            }
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

        // Create announcement with timestamp
        Announcement announcement = new Announcement(
                imageUri.toString(),
                title,
                carModel,
                rentingPrice,
                description,
                System.currentTimeMillis()
        );

        // Add the new announcement to the list directly
        announcementList.add(announcement);

        // Notify the adapter to refresh the RecyclerView
        announcementAdapter.notifyItemInserted(announcementList.size() - 1); // Add new item at the end

        // Now post the announcement to Firestore
        db.collection("announcements")
                .add(announcement)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Announcement posted successfully", Toast.LENGTH_SHORT).show();
                        // You don't need to reload the announcements from Firestore anymore, since it's already in the list
                        if (postDialog != null) postDialog.dismiss();
                    } else {
                        Toast.makeText(this, "Error posting: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                Toast.makeText(this, "Image selected successfully!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null && data.getExtras() != null) {
                if (data.getData() != null) {
                    imageUri = data.getData();
                } else if (data.getExtras().get("data") != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imageUri = getImageUriFromBitmap(photo);
                    Toast.makeText(this, "Image captured successfully!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to retrieve image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Action cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "CapturedImage", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage();
            } else {
                Toast.makeText(this, "Camera permission is required to capture images.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
