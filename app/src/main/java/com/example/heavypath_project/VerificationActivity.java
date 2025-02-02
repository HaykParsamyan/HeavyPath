package com.example.heavypath_project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class VerificationActivity extends AppCompatActivity {

    private EditText verificationCodeEditText;
    private Button confirmButton;
    private String email;
    private String generatedVerificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);

        verificationCodeEditText = findViewById(R.id.verification_code);
        confirmButton = findViewById(R.id.confirm_button);

        email = getIntent().getStringExtra("email");
        generatedVerificationCode = generateVerificationCode();
        sendVerificationEmail(email, generatedVerificationCode);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmRegistration();
            }
        });
    }

    private void confirmRegistration() {
        String enteredCode = verificationCodeEditText.getText().toString().trim();

        if (TextUtils.isEmpty(enteredCode)) {
            Toast.makeText(VerificationActivity.this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
            return;
        }

        if (enteredCode.equals(generatedVerificationCode)) {
            // Registration is successful
            Toast.makeText(VerificationActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(VerificationActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(VerificationActivity.this, "Invalid verification code", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // Generates a 6-digit code
    }

    private void sendVerificationEmail(String email, String code) {
        // Set up properties for the mail session
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.yourdomain.com"); // Replace with your SMTP host
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", "587"); // Replace with your SMTP port

        // Create a new session with an authenticator
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("your_email@yourdomain.com", "your_email_password"); // Replace with your email and password
            }
        });

        try {
            // Create a new email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("your_email@yourdomain.com")); // Replace with your email
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Verification Code");
            message.setText("Your verification code is: " + code);

            // Send the email
            Transport.send(message);
            Toast.makeText(VerificationActivity.this, "Verification email sent", Toast.LENGTH_SHORT).show();
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
