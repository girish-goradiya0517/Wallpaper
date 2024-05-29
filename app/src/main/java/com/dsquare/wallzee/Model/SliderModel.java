package com.dsquare.wallzee.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SliderModel {
    @SerializedName("status")
    private String status;

    @SerializedName("slider")
    private List<Slider> sliderItems;

    @SerializedName("message")
    private String message;

    public String getStatus() {
        return status;
    }

    public List<Slider> getSliderItems() {
        return sliderItems;
    }

    public String getMessage() {
        return message;
    }
}
