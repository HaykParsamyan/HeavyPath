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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostAnnouncementDialog extends AppCompatDialogFragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;

    private ImageView imagePreview;
    private EditText editTextTitle, editTextCarModel, editTextRentingPrice, editTextDescription;
    private ImageButton buttonUploadImage, buttonCaptureImage;
    private Button buttonPost;

    private Uri imageUri;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_post_announcement, null);

        builder.setView(view);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        initializeUI(view);

        return builder.create();
    }

    private void initializeUI(View view) {
        imagePreview = view.findViewById(R.id.image_preview);
        editTextTitle = view.findViewById(R.id.editTextTitle);
        editTextCarModel = view.findViewById(R.id.editTextCarModel);
        editTextRentingPrice = view.findViewById(R.id.editTextRentingPrice);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        buttonUploadImage = view.findViewById(R.id.buttonUploadImage);
        buttonCaptureImage = view.findViewById(R.id.buttonCaptureImage);
        buttonPost = view.findViewById(R.id.buttonPost);

        buttonUploadImage.setOnClickListener(v -> selectPhotoFromGallery());
        buttonCaptureImage.setOnClickListener(v -> captureImage());
        buttonPost.setOnClickListener(v -> validateAndPostAnnouncement());
    }

    private void selectPhotoFromGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickIntent, PICK_IMAGE_REQUEST);
    }

    private void captureImage() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(captureIntent, CAPTURE_IMAGE_REQUEST);
        } else {
            Toast.makeText(requireActivity(), "No camera app found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void validateAndPostAnnouncement() {
        String title = editTextTitle.getText().toString().trim();
        String carModel = editTextCarModel.getText().toString().trim();
        String rentingPrice = editTextRentingPrice.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(requireActivity(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(requireActivity(), "Please upload or capture an image.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (title.isEmpty() || carModel.isEmpty() || rentingPrice.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireActivity(), "All fields are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        uploadImageToFirebase(title, carModel, rentingPrice, description, userId);
    }

    private void uploadImageToFirebase(String title, String carModel, String rentingPrice, String description, String userId) {
        String uniqueId = UUID.randomUUID().toString();
        StorageReference storageReference = storage.getReference("announcement_images/" + uniqueId);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    saveAnnouncementToFirestore(downloadUri.toString(), title, carModel, rentingPrice, description, userId);
                }))
                .addOnFailureListener(e -> Toast.makeText(requireActivity(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void saveAnnouncementToFirestore(String imageUri, String title, String carModel, String rentingPrice, String description, String userId) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Map<String, Object> announcement = new HashMap<>();
        announcement.put("title", title);
        announcement.put("carModel", carModel);
        announcement.put("rentingPrice", rentingPrice);
        announcement.put("description", description);
        announcement.put("imageUri", imageUri);
        announcement.put("userId", userId);
        announcement.put("timestamp", Timestamp.now()); // ✅ Correct Timestamp format

        firestore.collection("announcements")
                .document(UUID.randomUUID().toString())
                .set(announcement, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast.makeText(requireActivity(), "Announcement posted successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(requireActivity(), "Failed to save announcement: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == requireActivity().RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                imageUri = data.getData();
                imagePreview.setImageURI(imageUri); // ✅ Safely update UI
                Toast.makeText(requireActivity(), "Image selected successfully!", Toast.LENGTH_SHORT).show();
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imagePreview.setImageBitmap(photo);
                imageUri = getImageUriFromBitmap(photo);
                Toast.makeText(requireActivity(), "Image captured successfully!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        String path = MediaStore.Images.Media.insertImage(requireActivity().getContentResolver(), bitmap, "CapturedImage", null);
        return Uri.parse(path);
    }
}
