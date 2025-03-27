package com.example.heavypath_project;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostAnnouncementDialog extends AppCompatDialogFragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;

    private ImageView imagePreview;
    private EditText editTextTitle, editTextCarModel, editTextRentingPrice, editTextDescription;
    private ImageButton buttonCaptureImage, buttonUploadImage;
    private Button buttonPost;

    private Uri selectedImageUri;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;
    private FirebaseAuth auth;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_post_announcement, null);

        builder.setView(view);

        // Initialize Firebase services
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI components
        initializeUI(view);

        return builder.create();
    }

    private void initializeUI(View view) {
        imagePreview = view.findViewById(R.id.image_preview);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextCarModel = view.findViewById(R.id.editTextCarModel);
        editTextRentingPrice = view.findViewById(R.id.editTextRentingPrice);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonCaptureImage = view.findViewById(R.id.buttonCaptureImage);
        buttonUploadImage = view.findViewById(R.id.buttonUploadImage);
        buttonPost = view.findViewById(R.id.buttonPost);

        // Capture photo using the camera
        buttonCaptureImage.setOnClickListener(v -> capturePhoto());

        // Select image from the gallery
        buttonUploadImage.setOnClickListener(v -> selectPhotoFromGallery());

        // Post the announcement
        buttonPost.setOnClickListener(v -> validateAndPostAnnouncement());
    }

    private void capturePhoto() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(requireActivity(), "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPhotoFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickIntent, PICK_IMAGE);
    }

    private void validateAndPostAnnouncement() {
        String title = editTextTitle.getText().toString().trim();
        String carModel = editTextCarModel.getText().toString().trim();
        String rentingPrice = editTextRentingPrice.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (selectedImageUri == null) {
            Toast.makeText(requireActivity(), "Please select or capture an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || carModel.isEmpty() || rentingPrice.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireActivity(), "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Proceed with uploading the image
        uploadImageToFirebase(title, carModel, rentingPrice, description);
    }

    private void uploadImageToFirebase(String title, String carModel, String rentingPrice, String description) {
        String uniqueId = UUID.randomUUID().toString(); // Generate unique ID for the image
        StorageReference storageReference = storage.getReference("announcement_images/" + uniqueId);

        storageReference.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            String imageUrl = downloadUri.toString();
                            saveAnnouncementToFirestore(title, carModel, rentingPrice, description, imageUrl);
                        })
                )
                .addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveAnnouncementToFirestore(String title, String carModel, String rentingPrice, String description, String imageUrl) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireActivity(), "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = currentUser.getEmail(); // Get user's email

        Map<String, Object> announcement = new HashMap<>();
        announcement.put("title", title);
        announcement.put("carModel", carModel);
        announcement.put("rentingPrice", rentingPrice);
        announcement.put("description", description);
        announcement.put("imageUrl", imageUrl);
        announcement.put("email", email);

        // Save data to Firestore in 'announcement/Photos'
        firestore.collection("announcement")
                .document("Photos")
                .set(announcement)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireActivity(), "Announcement posted successfully!", Toast.LENGTH_SHORT).show();
                    dismiss(); // Close the dialog
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireActivity(), "Failed to save announcement: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == requireActivity().RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imagePreview.setImageBitmap(photo);
            } else if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                    imagePreview.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Toast.makeText(requireActivity(), "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
