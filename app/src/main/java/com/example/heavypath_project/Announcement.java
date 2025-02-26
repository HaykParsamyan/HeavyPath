public class Announcement {
    private String title;
    private String carModel;
    private String rentingPrice;
    private String description;
    private Uri imageUri;

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
}
