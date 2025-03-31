package com.example.heavypath_project;

public class Announcement {
    private String imageUri; // Store as String for Firestore compatibility
    private String title;
    private String carModel;
    private String rentingPrice;
    private String description;

    // Default constructor for Firestore
    public Announcement() { }

    // Parameterized constructor
    public Announcement(String imageUri, String title, String carModel, String rentingPrice, String description) {
        this.imageUri = imageUri;
        this.title = title;
        this.carModel = carModel;
        this.rentingPrice = rentingPrice;
        this.description = description;
    }

    // Getters and setters
    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getRentingPrice() {
        return rentingPrice;
    }

    public void setRentingPrice(String rentingPrice) {
        this.rentingPrice = rentingPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
