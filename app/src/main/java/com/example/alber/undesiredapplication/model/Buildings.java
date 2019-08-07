package com.example.alber.undesiredapplication.model;

import com.google.firebase.database.Exclude;

public class Buildings {

    private String name;
    private String description;
    private String categorie;
    private String address;
    private String image_url;
    private String mKey;
    public double  latitude;
    public double  longitude;
    private String userID;


    public Buildings() {

        }

    public Buildings(String name, String image_url, String address, String description, String categorie, double latitude, double longitude, String userID) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        this.name = name;
        this.image_url = image_url;
        this.address = address;
        this.description = description;
        this.categorie = categorie;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategorie() {
        return categorie;
    }

    public String getAddress() {
        return address;
    }

    public String getImage_url() {
        return image_url;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUserID() {
        return userID;
    }






    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public void setUserID(String userID) {
        this.userID = userID;
    }


    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

}