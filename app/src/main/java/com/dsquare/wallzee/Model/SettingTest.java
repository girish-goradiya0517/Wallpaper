package com.dsquare.wallzee.Model;

public class SettingTest {
    private String api_cat_post_order_by;
    private String api_key;
    private String app_logo;
    private String app_name;
    private String app_privacy_policy;
    private String category_order;
    private String category_sort;
    private int id;
    private int limit_recent_wallpaper;
    private String onesignal_app_id;
    private String onesignal_rest_key;

    // Constructor
    public SettingTest(String api_cat_post_order_by, String api_key, String app_logo, String app_name,
                    String app_privacy_policy, String category_order, String category_sort, int id,
                    int limit_recent_wallpaper, String onesignal_app_id, String onesignal_rest_key) {
        this.api_cat_post_order_by = api_cat_post_order_by;
        this.api_key = api_key;
        this.app_logo = app_logo;
        this.app_name = app_name;
        this.app_privacy_policy = app_privacy_policy;
        this.category_order = category_order;
        this.category_sort = category_sort;
        this.id = id;
        this.limit_recent_wallpaper = limit_recent_wallpaper;
        this.onesignal_app_id = onesignal_app_id;
        this.onesignal_rest_key = onesignal_rest_key;
    }

    public String getApi_cat_post_order_by() {
        return api_cat_post_order_by;
    }

    public void setApi_cat_post_order_by(String api_cat_post_order_by) {
        this.api_cat_post_order_by = api_cat_post_order_by;
    }

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getApp_logo() {
        return app_logo;
    }

    public void setApp_logo(String app_logo) {
        this.app_logo = app_logo;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String app_name) {
        this.app_name = app_name;
    }

    public String getApp_privacy_policy() {
        return app_privacy_policy;
    }

    public void setApp_privacy_policy(String app_privacy_policy) {
        this.app_privacy_policy = app_privacy_policy;
    }

    public String getCategory_order() {
        return category_order;
    }

    public void setCategory_order(String category_order) {
        this.category_order = category_order;
    }

    public String getCategory_sort() {
        return category_sort;
    }

    public void setCategory_sort(String category_sort) {
        this.category_sort = category_sort;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLimit_recent_wallpaper() {
        return limit_recent_wallpaper;
    }

    public void setLimit_recent_wallpaper(int limit_recent_wallpaper) {
        this.limit_recent_wallpaper = limit_recent_wallpaper;
    }

    public String getOnesignal_app_id() {
        return onesignal_app_id;
    }

    public void setOnesignal_app_id(String onesignal_app_id) {
        this.onesignal_app_id = onesignal_app_id;
    }

    public String getOnesignal_rest_key() {
        return onesignal_rest_key;
    }

    public void setOnesignal_rest_key(String onesignal_rest_key) {
        this.onesignal_rest_key = onesignal_rest_key;
    }
}
