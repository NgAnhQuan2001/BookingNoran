package com.aqsoft.bookingappnoran.model;

public class LoaiPhongmodel {

    private String name;
    private String Motangan;
    private String Mota;
    private String Gia;
    private String Giuong;
    private String Bontam;
    private String Wifi;

    private String Key;
    private String Image;

    public LoaiPhongmodel() {
    }

    public LoaiPhongmodel(String name, String motangan, String mota, String gia, String giuong, String bontam, String wifi, String key, String image) {
        this.name = name;
        Motangan = motangan;
        Mota = mota;
        Gia = gia;
        Giuong = giuong;
        Bontam = bontam;
        Wifi = wifi;
        Key = key;
        Image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMotangan() {
        return Motangan;
    }

    public void setMotangan(String motangan) {
        Motangan = motangan;
    }

    public String getMota() {
        return Mota;
    }

    public void setMota(String mota) {
        Mota = mota;
    }

    public String getGia() {
        return Gia;
    }

    public void setGia(String gia) {
        Gia = gia;
    }

    public String getGiuong() {
        return Giuong;
    }

    public void setGiuong(String giuong) {
        Giuong = giuong;
    }

    public String getBontam() {
        return Bontam;
    }

    public void setBontam(String bontam) {
        Bontam = bontam;
    }

    public String getWifi() {
        return Wifi;
    }

    public void setWifi(String wifi) {
        Wifi = wifi;
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
}
