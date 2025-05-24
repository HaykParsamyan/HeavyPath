package com.example.heavypath_project;

public class Announcement {
    private String imageUri;
    private String title;
    private String carModel;
    private String rentingPrice;
    private String description;
    private long timestamp;
    private String userId; // ✅ Ensure userId is included

    // ✅ Required empty constructor for Firestore
    public Announcement() {}

    // ✅ Updated Constructor to match the parameters being passed
    public Announcement(String imageUri, String title, String carModel, String rentingPrice, String description, long timestamp, String userId) {
        this.imageUri = imageUri;
        this.title = title;
        this.carModel = carModel;
        this.rentingPrice = rentingPrice;
        this.description = description;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public Announcement(String string, String title, String carModel, String rentingPrice, String description, long l) {
        this.imageUri = imageUri;
        this.title = title;
        this.carModel = carModel;
        this.rentingPrice = rentingPrice;
        this.description = description;


    }


    public String getImageUri() { return imageUri; }
    public String getTitle() { return title; }
    public String getCarModel() { return carModel; }
    public String getRentingPrice() { return rentingPrice; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }

    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public void setTitle(String title) { this.title = title; }
    public void setCarModel(String carModel) { this.carModel = carModel; }
    public void setRentingPrice(String rentingPrice) { this.rentingPrice = rentingPrice; }
    public void setDescription(String description) { this.description = description; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setUserId(String userId) { this.userId = userId; }
}

