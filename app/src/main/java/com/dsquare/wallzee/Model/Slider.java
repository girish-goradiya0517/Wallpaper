package com.dsquare.wallzee.Model;

import com.google.gson.annotations.SerializedName;

public class Slider {
    @SerializedName("id")
    private int id;

    @SerializedName("Slider_Image")
    private String sliderImage;

    @SerializedName("screen_id")
    private int screenId;

    @SerializedName("Screen_Name")
    private String screenName;

    @SerializedName("screen_ids")
    private int screenIds;

    public int getId() {
        return id;
    }

    public String getSliderImage() {
        return sliderImage;
    }

    public int getScreenId() {
        return screenId;
    }

    public String getScreenName() {
        return screenName;
    }

    public int getScreenIds() {
        return screenIds;
    }
}
