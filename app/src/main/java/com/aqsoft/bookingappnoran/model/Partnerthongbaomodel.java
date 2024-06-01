package com.aqsoft.bookingappnoran.model;

public class Partnerthongbaomodel {
    private String Date1;
    private String Date2;
    private String Luuy;
    private String Tongtien;
    private String Useruid;
    private String Room;

    private String TrangThai;

    private String NameKS;
    private String Keyks;
    private String ID;

public Partnerthongbaomodel() {
    }


    public Partnerthongbaomodel(String date1, String date2, String luuy, String tongtien, String useruid, String room, String trangThai, String nameKS, String keyks, String ID) {
        Date1 = date1;
        Date2 = date2;
        Luuy = luuy;
        Tongtien = tongtien;
        Useruid = useruid;
        Room = room;
        TrangThai = trangThai;
        NameKS = nameKS;
        Keyks = keyks;
        this.ID = ID;
    }

    public String getDate1() {
        return Date1;
    }

    public void setDate1(String date1) {
        Date1 = date1;
    }

    public String getDate2() {
        return Date2;
    }

    public void setDate2(String date2) {
        Date2 = date2;
    }

    public String getLuuy() {
        return Luuy;
    }

    public void setLuuy(String luuy) {
        Luuy = luuy;
    }

    public String getTongtien() {
        return Tongtien;
    }

    public void setTongtien(String tongtien) {
        Tongtien = tongtien;
    }

    public String getUseruid() {
        return Useruid;
    }

    public void setUseruid(String useruid) {
        Useruid = useruid;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(String trangThai) {
        TrangThai = trangThai;
    }

    public String getNameKS() {
        return NameKS;
    }

    public void setNameKS(String nameKS) {
        NameKS = nameKS;
    }

    public String getKeyks() {
        return Keyks;
    }

    public void setKeyks(String keyks) {
        Keyks = keyks;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
