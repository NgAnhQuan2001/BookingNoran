package com.aqsoft.bookingappnoran.model;

public class Khachsan {
    private String Name;
    private String Address;
    private String Phone;
    private String Rating;
    private String Key;
    private String Image;

    private String uid;

    private String quy;

    public Khachsan() {
    }

    public Khachsan(String name, String address, String phone, String rating, String key, String image, String uid, String quy) {
        Name = name;
        Address = address;
        Phone = phone;
        Rating = rating;
        Key = key;
        Image = image;
        this.uid = uid;
        this.quy = quy;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getQuy() {
        return quy;
    }

    public void setQuy(String quy) {
        this.quy = quy;
    }
}
