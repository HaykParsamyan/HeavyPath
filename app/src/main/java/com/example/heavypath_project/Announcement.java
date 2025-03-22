package com.example.heavypath_project;

import android.net.Uri;

public class Announcement {
    private Uri imageUri;
    private String title;
    private String carModel;
    private String rentingPrice;
    private String description;

    public Announcement(Uri imageUri, String title, String carModel, String rentingPrice, String description) {
        this.imageUri = imageUri;
        this.title = title;
        this.carModel = carModel;
        this.rentingPrice = rentingPrice;
        this.description = description;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public String getTitle() {
        return title;
    }

    public String getCarModel() {
        return carModel;
    }

    public String getRentingPrice() {
        return rentingPrice;
    }

    public String getDescription() {
        return description;
    }
}
