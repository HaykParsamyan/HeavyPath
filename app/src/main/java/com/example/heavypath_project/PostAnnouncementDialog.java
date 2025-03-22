package com.example.heavypath_project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.app.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.io.IOException;

public class PostAnnouncementDialog extends AppCompatDialogFragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 2;

    private ImageView imagePreview;
    private EditText editTitle, editCarModel, editRentingPrice, editDescription;
    private Button capturePhotoButton, choosePhotoButton, postButton;

    private Uri selectedImageUri;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_post_announcement, null);

        builder.setView(view);

        // Initialize UI components
        imagePreview = view.findViewById(R.id.image_preview);
        editTitle = view.findViewById(R.id.editTextTitle);
        editCarModel = view.findViewById(R.id.editTextCarModel);
        editRentingPrice = view.findViewById(R.id.editTextRentingPrice);
        editDescription = view.findViewById(R.id.editTextDescription);
        capturePhotoButton = view.findViewById(R.id.buttonCaptureImage);
        choosePhotoButton = view.findViewById(R.id.buttonUploadImage);
        postButton = view.findViewById(R.id.buttonPost);

        // Capture photo
        capturePhotoButton.setOnClickListener(v -> {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (captureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(captureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(getActivity(), "Camera not available", Toast.LENGTH_SHORT).show();
            }
        });

        // Choose photo from gallery
        choosePhotoButton.setOnClickListener(v -> {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickIntent, PICK_IMAGE);
        });

        // Handle Post button click
        postButton.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String carModel = editCarModel.getText().toString().trim();
            String rentingPrice = editRentingPrice.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            if (selectedImageUri == null) {
                Toast.makeText(getActivity(), "Please select or capture an image", Toast.LENGTH_SHORT).show();
                return;
            }

            if (title.isEmpty() || carModel.isEmpty() || rentingPrice.isEmpty() || description.isEmpty()) {
                Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass data back to MainHomeActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("IMAGE_URI", selectedImageUri.toString());
            resultIntent.putExtra("TITLE", title);
            resultIntent.putExtra("CAR_MODEL", carModel);
            resultIntent.putExtra("RENTING_PRICE", rentingPrice);
            resultIntent.putExtra("DESCRIPTION", description);
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, resultIntent);

            dismiss(); // Close dialog
        });

        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE && data != null && data.getExtras() != null) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                imagePreview.setImageBitmap(photo);
                // You may need to save this image as a file to get its URI
            } else if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                    imagePreview.setImageBitmap(bitmap);
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
