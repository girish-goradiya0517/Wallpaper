package com.dsquare.wallzee.Model;

// Define a model class for the API request
public class RegistrationRequest {
    private String email;
    private String name;
    private String profileImageUrl;

    // Constructors, getters, and setters

    public RegistrationRequest(String email, String name, String profileImageUrl) {
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }

    public RegistrationRequest() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}

// Create a Retrofit service interface

