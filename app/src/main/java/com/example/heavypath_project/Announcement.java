package com.example.heavypath_project;

import android.net.Uri;

/**
 * Represents an announcement with details such as title, car model, renting price, description, and an image URI.
 */
public class Announcement {
    private String title;
    private String carModel;
    private String rentingPrice;
    private String description;
    private Uri imageUri;

    /**
     * Constructor to initialize the Announcement object with provided details.
     *
     * @param title the title of the announcement
     * @param carModel the car model associated with the announcement
     * @param rentingPrice the renting price of the car
     * @param description the description of the announcement
     * @param imageUri the URI of the associated image
     */
    public Announcement(String title, String carModel, String rentingPrice, String description, Uri imageUri) {
        this.title = title;
        this.carModel = carModel;
        this.rentingPrice = rentingPrice;
        this.description = description;
        this.imageUri = imageUri;
    }

    // Getters
    public String getTitle() { return title; }
    public String getCarModel() { return carModel; }
    public String getRentingPrice() { return rentingPrice; }
    public String getDescription() { return description; }
    public Uri getImageUri() { return imageUri; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setCarModel(String carModel) { this.carModel = carModel; }
    public void setRentingPrice(String rentingPrice) { this.rentingPrice = rentingPrice; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUri(Uri imageUri) { this.imageUri = imageUri; }
}
