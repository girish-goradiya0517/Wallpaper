package com.dsquare.wallzee.Utils;

public class Constant {

    //global variables
    public static final long DELAY_TIME = 250;
    public static final String ADDED_NEWEST = "image_id DESC";
    public static final String ADDED_OLDEST = "n.view_count DESC";
    public static final String EXTRA_OBJC = "key.EXTRA_OBJC";

    public static final String AD_STATUS_ON = "on";
    public static final String ADMOB = "admob";
    public static final String FAN = "fan";
    public static final String STARTAPP = "startapp";
    public static final String UNITY = "unity";
    public static final String DEVELOPER_NAME = "YMG-Developers";
    public static final String APPLOVIN = "applovin";


    public static String packageName = "", search_item = "", gifName = "", gifPath = "";
    public static final int DELAY_SET_WALLPAPER = 2000;

    //startapp native ad image parameters
    public static final int STARTAPP_IMAGE_XSMALL = 1; //for image size 100px X 100px
    public static final int STARTAPP_IMAGE_SMALL = 2; //for image size 150px X 150px
    public static final int STARTAPP_IMAGE_MEDIUM = 3; //for image size 340px X 340px
    public static final int STARTAPP_IMAGE_LARGE = 4; //for image size 1200px X 628px

    //unity banner ad size
    public static final int UNITY_ADS_BANNER_WIDTH = 320;
    public static final int UNITY_ADS_BANNER_HEIGHT = 50;

    public static final int MAX_NUMBER_OF_NATIVE_AD_DISPLAYED = 25;
    public static final int BANNER_HOME = 1;
    public static final int BANNER_POST_DETAIL = 1;
    public static final int BANNER_CATEGORY_DETAIL = 1;
    public static final int BANNER_SEARCH = 1;
    public static final int INTERSTITIAL_POST_LIST = 1;
    public static final int NATIVE_AD_POST_DETAIL = 1;

}