package com.aqsoft.bookingappnoran.model;

public class Thongbaomodel {
    private String Date1;
    private String Date2;
    private String Luuy;
    private String NameKS;
    private String Room;
    private String ID;
    private String Soluong;
    private String Tongtien;
    private String Useruid;
    private String Keyks;
    private String TrangThai;

    public Thongbaomodel() {
    }

    public Thongbaomodel(String date1, String date2, String luuy, String nameKS, String room, String ID, String soluong, String tongtien, String useruid, String keyks, String trangThai) {
        Date1 = date1;
        Date2 = date2;
        Luuy = luuy;
        NameKS = nameKS;
        Room = room;
        this.ID = ID;
        Soluong = soluong;
        Tongtien = tongtien;
        Useruid = useruid;
        Keyks = keyks;
        TrangThai = trangThai;
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

    public String getNameKS() {
        return NameKS;
    }

    public void setNameKS(String nameKS) {
        NameKS = nameKS;
    }

    public String getRoom() {
        return Room;
    }

    public void setRoom(String room) {
        Room = room;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSoluong() {
        return Soluong;
    }

    public void setSoluong(String soluong) {
        Soluong = soluong;
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

    public String getKeyks() {
        return Keyks;
    }

    public void setKeyks(String keyks) {
        Keyks = keyks;
    }

    public String getTrangThai() {
        return TrangThai;
    }

    public void setTrangThai(String trangThai) {
        TrangThai = trangThai;
    }
}
