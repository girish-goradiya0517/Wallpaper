package com.dsquare.wallzee.Model;

import com.google.gson.annotations.SerializedName;

public class ReportRequestBody {
    @SerializedName("wallpaperid")
    private String wallpaperId;

    @SerializedName("userid")
    private String userId;

    @SerializedName("message")
    private String message;

    @SerializedName("status")
    private int status;

    public String getWallpaperId() {
        return wallpaperId;
    }

    public void setWallpaperId(String wallpaperId) {
        this.wallpaperId = wallpaperId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ReportRequestBody(String wallpaperId, String userId, String message, int status) {
        this.wallpaperId = wallpaperId;
        this.userId = userId;
        this.message = message;
        this.status = status;
    }
}
