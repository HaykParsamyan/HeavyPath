package com.example.heavypath_project;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class VerificationActivity extends AppCompatActivity {

    private EditText verificationCodeEditText;
    private Button confirmButton;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        verificationCodeEditText = findViewById(R.id.verification_code);
        confirmButton = findViewById(R.id.confirm_button);
        email = getIntent().getStringExtra("email");

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmVerificationCode();
            }
        });
    }

    private void confirmVerificationCode() {
        String verificationCode = verificationCodeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(verificationCode)) {
            verificationCodeEditText.setError("Verification code is required");
            return;
        }

        if (verificationCode.length() != 6) {
            Toast.makeText(VerificationActivity.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verify the code with the server
        new VerifyCodeTask().execute(email, verificationCode);
    }

    private class VerifyCodeTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String email = params[0];
            String code = params[1];
            try {
                URL url = new URL("http://localhost:3000/verify");  // Replace with your server's address
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; utf-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("email", email);
                json.put("code", code);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = json.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                return responseCode == 200;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(VerificationActivity.this, "Verification successful!", Toast.LENGTH_SHORT).show();
                // Proceed to the main app or dashboard
            } else {
                Toast.makeText(VerificationActivity.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
