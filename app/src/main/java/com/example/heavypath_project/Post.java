package com.example.heavypath_project;

import android.net.Uri;

public class Post {
    private Uri imageUri;           // URI of the image
    private String title;           // Title of the post
    private String carModel;        // Car model associated with the post
    private String rentingPrice;    // Renting price per hour
    private String description;     // Description of the post

    // Constructor to initialize the Post object
    public Post(Uri imageUri, String title, String carModel, String rentingPrice, String description) {
        this.imageUri = imageUri;
        this.title = title;
        this.carModel = carModel;
        this.rentingPrice = rentingPrice;
        this.description = description;
    }

    // Getter for Image URI
    public Uri getImageUri() {
        return imageUri;
    }

    // Setter for Image URI (optional, for future updates)
    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    // Getter for Title
    public String getTitle() {
        return title;
    }

    // Setter for Title
    public void setTitle(String title) {
        this.title = title;
    }

    // Getter for Car Model
    public String getCarModel() {
        return carModel;
    }

    // Setter for Car Model
    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    // Getter for Renting Price
    public String getRentingPrice() {
        return rentingPrice;
    }

    // Setter for Renting Price
    public void setRentingPrice(String rentingPrice) {
        this.rentingPrice = rentingPrice;
    }

    // Getter for Description
    public String getDescription() {
        return description;
    }

    // Setter for Description
    public void setDescription(String description) {
        this.description = description;
    }

    // Method to display all the details of the Post as a String
    @Override
    public String toString() {
        return "Post{" +
                "imageUri=" + imageUri +
                ", title='" + title + '\'' +
                ", carModel='" + carModel + '\'' +
                ", rentingPrice='" + rentingPrice + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
