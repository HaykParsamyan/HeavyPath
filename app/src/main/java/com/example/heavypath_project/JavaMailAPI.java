package com.example.heavypath_project;

import android.os.AsyncTask;
import android.util.Log;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class JavaMailAPI {

    private static final String TAG = "JavaMailAPI";
    private static final String EMAIL = "heavypath@mail.ru"; // Your email address
    private static final String PASSWORD = "5z7vjqYqcuxaKf5hkD6b"; // Your email password

    public static void sendVerificationEmail(String recipientEmail, String verificationCode) {
        new SendEmailTask().execute(recipientEmail, verificationCode);
    }

    private static class SendEmailTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            String recipientEmail = params[0];
            String verificationCode = params[1];

            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.mail.ru");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(EMAIL, PASSWORD);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Email Verification");
                message.setText("Your verification code is: " + verificationCode);

                Log.d(TAG, "Attempting to send email to " + recipientEmail);
                Transport.send(message);
                Log.d(TAG, "Email sent successfully to " + recipientEmail);
                return true;
            } catch (MessagingException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to send email: " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Log.d(TAG, "Verification email sent successfully");
            } else {
                Log.e(TAG, "Failed to send verification email");
            }
        }
    }
}

