package com.aqsoft.bookingappnoran.model;

public class Danhgiamodel {
    private String Uid;
    private String Comment;
    private String Rating;

    public Danhgiamodel(String uid, String comment, String rating) {
        Uid = uid;
        Comment = comment;
        Rating = rating;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }
}
