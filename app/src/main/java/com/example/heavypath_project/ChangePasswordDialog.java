package com.example.heavypath_project;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ChangePasswordDialog extends AppCompatDialogFragment {

    private EditText currentPasswordInput;
    private EditText newPasswordInput;
    private EditText confirmPasswordInput;
    private Button changePasswordButton;
    private static final int MIN_PASSWORD_LENGTH = 6; // Minimum password length requirement

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);

        builder.setView(view);

        // Initialize inputs and button
        currentPasswordInput = view.findViewById(R.id.current_password);
        newPasswordInput = view.findViewById(R.id.new_password);
        confirmPasswordInput = view.findViewById(R.id.confirm_password);
        changePasswordButton = view.findViewById(R.id.button_change_password);

        // Handle Change Password button click
        changePasswordButton.setOnClickListener(v -> {
            if (validateAndChangePassword()) {
                dismiss(); // Close dialog only if password is successfully changed
            }
        });

        return builder.create();
    }

    private boolean validateAndChangePassword() {
        // Retrieve stored password from SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String storedPassword = preferences.getString("PASSWORD", null);

        // Handle case when no password is stored
        if (storedPassword == null) {
            Toast.makeText(getActivity(), "No password is set. Please contact support.", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Get user input
        String currentPassword = currentPasswordInput.getText().toString().trim();
        String newPassword = newPasswordInput.getText().toString().trim();
        String confirmPassword = confirmPasswordInput.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(currentPassword) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(getActivity(), "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate current password
        if (!currentPassword.equals(storedPassword)) {
            Toast.makeText(getActivity(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate new password length
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            Toast.makeText(getActivity(), "New password must be at least " + MIN_PASSWORD_LENGTH + " characters long", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validate new passwords match
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(getActivity(), "New passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Save the new password in SharedPreferences
        preferences.edit().putString("PASSWORD", newPassword).apply();
        Toast.makeText(getActivity(), "Password changed successfully", Toast.LENGTH_SHORT).show();
        return true;
    }
}
