package com.example.clinicsys.Appointment.pending;

public class AppointmentPending {
    private String title, image;
    private String price;
//    private float rating;

    public AppointmentPending(String title, String price){

        this.title = title;
//        this.image = image;
//        this.rating = rating;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

//    public String getImage() {
//        return image;
//    }

//    public void setImage(String image) {
//        this.image = image;
//    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

//    public float getRating() {
//        return rating;
//    }
//
//    public void setRating(float rating) {
//        this.rating = rating;
//    }
}
