package com.dsquare.wallzee.Model;

public class AdsTest {

    private String ad_status;
    private String ad_type;
    private String admob_app_id;
    private String admob_app_open_ad_unit_id;
    private String admob_banner_unit_id;
    private String admob_interstitial_unit_id;
    private String admob_native_unit_id;
    private String admob_publisher_id;
    private int applovin_banner_ad_unit_id;
    private int applovin_interstitial_ad_unit_id;
    private int fan_banner_unit_id;
    private int fan_interstitial_unit_id;
    private int fan_native_unit_id;
    private int id;
    private int interstitial_ad_interval;
    private String last_update_ads;
    private String mopub_banner_ad_unit_id;
    private String mopub_interstitial_ad_unit_id;
    private int native_ad_index;
    private int native_ad_interval;
    private int startapp_app_id;
    private String unity_banner_placement_id;
    private int unity_game_id;
    private String unity_interstitial_placement_id;

    // Constructor
    public AdsTest(String ad_status, String ad_type, String admob_app_id, String admob_app_open_ad_unit_id,
               String admob_banner_unit_id, String admob_interstitial_unit_id, String admob_native_unit_id,
               String admob_publisher_id, int applovin_banner_ad_unit_id, int applovin_interstitial_ad_unit_id,
               int fan_banner_unit_id, int fan_interstitial_unit_id, int fan_native_unit_id, int id,
               int interstitial_ad_interval, String last_update_ads, String mopub_banner_ad_unit_id,
               String mopub_interstitial_ad_unit_id, int native_ad_index, int native_ad_interval,
               int startapp_app_id, String unity_banner_placement_id, int unity_game_id,
               String unity_interstitial_placement_id) {
        this.ad_status = ad_status;
        this.ad_type = ad_type;
        this.admob_app_id = admob_app_id;
        this.admob_app_open_ad_unit_id = admob_app_open_ad_unit_id;
        this.admob_banner_unit_id = admob_banner_unit_id;
        this.admob_interstitial_unit_id = admob_interstitial_unit_id;
        this.admob_native_unit_id = admob_native_unit_id;
        this.admob_publisher_id = admob_publisher_id;
        this.applovin_banner_ad_unit_id = applovin_banner_ad_unit_id;
        this.applovin_interstitial_ad_unit_id = applovin_interstitial_ad_unit_id;
        this.fan_banner_unit_id = fan_banner_unit_id;
        this.fan_interstitial_unit_id = fan_interstitial_unit_id;
        this.fan_native_unit_id = fan_native_unit_id;
        this.id = id;
        this.interstitial_ad_interval = interstitial_ad_interval;
        this.last_update_ads = last_update_ads;
        this.mopub_banner_ad_unit_id = mopub_banner_ad_unit_id;
        this.mopub_interstitial_ad_unit_id = mopub_interstitial_ad_unit_id;
        this.native_ad_index = native_ad_index;
        this.native_ad_interval = native_ad_interval;
        this.startapp_app_id = startapp_app_id;
        this.unity_banner_placement_id = unity_banner_placement_id;
        this.unity_game_id = unity_game_id;
        this.unity_interstitial_placement_id = unity_interstitial_placement_id;
    }

    public String getAd_status() {
        return ad_status;
    }

    public void setAd_status(String ad_status) {
        this.ad_status = ad_status;
    }

    public String getAd_type() {
        return ad_type;
    }

    public void setAd_type(String ad_type) {
        this.ad_type = ad_type;
    }

    public String getAdmob_app_id() {
        return admob_app_id;
    }

    public void setAdmob_app_id(String admob_app_id) {
        this.admob_app_id = admob_app_id;
    }

    public String getAdmob_app_open_ad_unit_id() {
        return admob_app_open_ad_unit_id;
    }

    public void setAdmob_app_open_ad_unit_id(String admob_app_open_ad_unit_id) {
        this.admob_app_open_ad_unit_id = admob_app_open_ad_unit_id;
    }

    public String getAdmob_banner_unit_id() {
        return admob_banner_unit_id;
    }

    public void setAdmob_banner_unit_id(String admob_banner_unit_id) {
        this.admob_banner_unit_id = admob_banner_unit_id;
    }

    public String getAdmob_interstitial_unit_id() {
        return admob_interstitial_unit_id;
    }

    public void setAdmob_interstitial_unit_id(String admob_interstitial_unit_id) {
        this.admob_interstitial_unit_id = admob_interstitial_unit_id;
    }

    public String getAdmob_native_unit_id() {
        return admob_native_unit_id;
    }

    public void setAdmob_native_unit_id(String admob_native_unit_id) {
        this.admob_native_unit_id = admob_native_unit_id;
    }

    public String getAdmob_publisher_id() {
        return admob_publisher_id;
    }

    public void setAdmob_publisher_id(String admob_publisher_id) {
        this.admob_publisher_id = admob_publisher_id;
    }

    public int getApplovin_banner_ad_unit_id() {
        return applovin_banner_ad_unit_id;
    }

    public void setApplovin_banner_ad_unit_id(int applovin_banner_ad_unit_id) {
        this.applovin_banner_ad_unit_id = applovin_banner_ad_unit_id;
    }

    public int getApplovin_interstitial_ad_unit_id() {
        return applovin_interstitial_ad_unit_id;
    }

    public void setApplovin_interstitial_ad_unit_id(int applovin_interstitial_ad_unit_id) {
        this.applovin_interstitial_ad_unit_id = applovin_interstitial_ad_unit_id;
    }

    public int getFan_banner_unit_id() {
        return fan_banner_unit_id;
    }

    public void setFan_banner_unit_id(int fan_banner_unit_id) {
        this.fan_banner_unit_id = fan_banner_unit_id;
    }

    public int getFan_interstitial_unit_id() {
        return fan_interstitial_unit_id;
    }

    public void setFan_interstitial_unit_id(int fan_interstitial_unit_id) {
        this.fan_interstitial_unit_id = fan_interstitial_unit_id;
    }

    public int getFan_native_unit_id() {
        return fan_native_unit_id;
    }

    public void setFan_native_unit_id(int fan_native_unit_id) {
        this.fan_native_unit_id = fan_native_unit_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInterstitial_ad_interval() {
        return interstitial_ad_interval;
    }

    public void setInterstitial_ad_interval(int interstitial_ad_interval) {
        this.interstitial_ad_interval = interstitial_ad_interval;
    }

    public String getLast_update_ads() {
        return last_update_ads;
    }

    public void setLast_update_ads(String last_update_ads) {
        this.last_update_ads = last_update_ads;
    }

    public String getMopub_banner_ad_unit_id() {
        return mopub_banner_ad_unit_id;
    }

    public void setMopub_banner_ad_unit_id(String mopub_banner_ad_unit_id) {
        this.mopub_banner_ad_unit_id = mopub_banner_ad_unit_id;
    }

    public String getMopub_interstitial_ad_unit_id() {
        return mopub_interstitial_ad_unit_id;
    }

    public void setMopub_interstitial_ad_unit_id(String mopub_interstitial_ad_unit_id) {
        this.mopub_interstitial_ad_unit_id = mopub_interstitial_ad_unit_id;
    }

    public int getNative_ad_index() {
        return native_ad_index;
    }

    public void setNative_ad_index(int native_ad_index) {
        this.native_ad_index = native_ad_index;
    }

    public int getNative_ad_interval() {
        return native_ad_interval;
    }

    public void setNative_ad_interval(int native_ad_interval) {
        this.native_ad_interval = native_ad_interval;
    }

    public int getStartapp_app_id() {
        return startapp_app_id;
    }

    public void setStartapp_app_id(int startapp_app_id) {
        this.startapp_app_id = startapp_app_id;
    }

    public String getUnity_banner_placement_id() {
        return unity_banner_placement_id;
    }

    public void setUnity_banner_placement_id(String unity_banner_placement_id) {
        this.unity_banner_placement_id = unity_banner_placement_id;
    }

    public int getUnity_game_id() {
        return unity_game_id;
    }

    public void setUnity_game_id(int unity_game_id) {
        this.unity_game_id = unity_game_id;
    }

    public String getUnity_interstitial_placement_id() {
        return unity_interstitial_placement_id;
    }

    public void setUnity_interstitial_placement_id(String unity_interstitial_placement_id) {
        this.unity_interstitial_placement_id = unity_interstitial_placement_id;
    }
}
