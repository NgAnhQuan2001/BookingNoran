package com.aqsoft.bookingappnoran.model;

public class UserModel {
    private String name;
    private String email;
    private String gioitinh;
    private String sodienthoai;
    private String uid;
    private String role;

    public UserModel(String name, String email, String gioitinh, String sodienthoai, String uid, String role) {
        this.name = name;
        this.email = email;
        this.gioitinh = gioitinh;
        this.sodienthoai = sodienthoai;
        this.uid = uid;
        this.role = role;
    }
    public UserModel() {
        // Bạn có thể giữ trống hoặc thêm logic khởi tạo nếu cần
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGioitinh() {
        return gioitinh;
    }

    public void setGioitinh(String gioitinh) {
        this.gioitinh = gioitinh;
    }

    public String getSodienthoai() {
        return sodienthoai;
    }

    public void setSodienthoai(String sodienthoai) {
        this.sodienthoai = sodienthoai;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
