package com.dsquare.wallzee.Model;

import java.io.Serializable;

public class Users implements Serializable {

    public int id = -1;
    public String admin;
    public String creatorEmail;
    public String creatorLinks;
    public String creatorName;
    public String creatorProfileUrl;
    public String followers;
    public String followingList;
    public String isVerified;
    public String total_uploads;


    public Users(int id, String admin, String creatorEmail, String creatorLinks, String creatorName, String creatorProfileUrl, String followers, String followingList, String isVerified,String total_uploads) {
        this.id = id;
        this.admin = admin;
        this.creatorEmail = creatorEmail;
        this.creatorLinks = creatorLinks;
        this.creatorName = creatorName;
        this.creatorProfileUrl = creatorProfileUrl;
        this.followers = followers;
        this.followingList = followingList;
        this.isVerified = isVerified;
        this.total_uploads = total_uploads;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getCreatorLinks() {
        return creatorLinks;
    }

    public void setCreatorLinks(String creatorLinks) {
        this.creatorLinks = creatorLinks;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorProfileUrl() {
        return creatorProfileUrl;
    }

    public void setCreatorProfileUrl(String creatorProfileUrl) {
        this.creatorProfileUrl = creatorProfileUrl;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowingList() {
        return followingList;
    }

    public void setFollowingList(String followingList) {
        this.followingList = followingList;
    }

    public String getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(String isVerified) {
        this.isVerified = isVerified;
    }

    public String getTotal_uploads() {
        return total_uploads;
    }

    public void setTotal_uploads(String total_uploads) {
        this.total_uploads = total_uploads;
    }
}
