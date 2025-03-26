package com.example.heavypath_project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private static final int CAMERA_PERMISSION_CODE = 100;

    private Button plusButton;
    private Button chatButton;
    private Button profileButton;
    private Uri imageUri;
    private AlertDialog postDialog;

    private RecyclerView recyclerView;
    private AnnouncementAdapter adapter;
    private List<Announcement> announcementList;
    private List<User> userList; // For search results

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize buttons
        plusButton = findViewById(R.id.plus_button);
        chatButton = findViewById(R.id.chat_button);
        profileButton = findViewById(R.id.profile_button);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewAnnouncements);
        announcementList = new ArrayList<>();
        userList = new ArrayList<>();
        adapter = new AnnouncementAdapter(this, announcementList); // Default adapter for announcements
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set button click listeners
        plusButton.setOnClickListener(v -> openPostAnnouncementDialog());
        chatButton.setOnClickListener(v -> startActivity(new Intent(MainHomeActivity.this, ChatActivity.class)));
        profileButton.setOnClickListener(v -> startActivity(new Intent(MainHomeActivity.this, ProfileActivity.class)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the search_menu from res/menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu); // Use the menu located in res/menu

        // Find the search item and configure the SearchView
        MenuItem searchItem = menu.findItem(R.id.action_search); // Ensure the ID matches your XML
        if (searchItem != null) {
            SearchView searchView = (SearchView) searchItem.getActionView();
            if (searchView != null) { // Prevent NullPointerException
                searchView.setQueryHint("Search by username"); // Display hint in the search bar
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (!TextUtils.isEmpty(query)) {
                            searchUsersByUsername(query); // Perform Firestore query
                        }
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (TextUtils.isEmpty(newText)) {
                            userList.clear(); // Clear search results
                            restoreAnnouncements(); // Restore announcements
                        }
                        return true;
                    }
                });
            } else {
                Toast.makeText(this, "Error initializing SearchView", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Search menu item not found!", Toast.LENGTH_SHORT).show();
        }

        return true;
    }


    private void searchUsersByUsername(String username) {
        db.collection("users")
                .whereEqualTo("username", username) // Query Firestore by username
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            userList.clear(); // Clear previous search results
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                User user = document.toObject(User.class);
                                userList.add(user); // Add users to the list
                            }
                            updateRecyclerViewWithUsers(); // Update RecyclerView
                        } else {
                            Toast.makeText(this, "No users found with username: " + username, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Error searching users: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateRecyclerViewWithUsers() {
        // Replace announcement RecyclerView with search results
        UserAdapter userAdapter = new UserAdapter(this, userList); // Adapter for user results
        recyclerView.setAdapter(userAdapter);
        userAdapter.notifyDataSetChanged();
    }

    private void restoreAnnouncements() {
        adapter = new AnnouncementAdapter(this, announcementList); // Default adapter for announcements
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
