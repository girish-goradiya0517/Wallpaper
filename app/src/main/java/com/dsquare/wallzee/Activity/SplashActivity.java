package com.dsquare.wallzee.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;

import com.dsquare.wallzee.Callback.CallbackAds;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.MainActivity;
import com.dsquare.wallzee.Model.Ads;
import com.dsquare.wallzee.Model.Settings;
import com.dsquare.wallzee.R;
import com.dsquare.wallzee.Rest.ApiInterface;
import com.dsquare.wallzee.Rest.RestAdapter;
import com.dsquare.wallzee.Utils.AdsPref;
import com.dsquare.wallzee.Utils.Anims;
import com.dsquare.wallzee.Utils.DarkModeUtils;
import com.dsquare.wallzee.Utils.PrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {


    private PrefManager prf;
    private AppCompatImageView logo;
    public static final String TAG = "ActivitySplash";
    private AdsPref adsPref;
    private Settings settings;
    private Ads ads;
    TextView splashTxt;
//    private AdsManager adsManager;

    private Call<CallbackAds> callbackAdsCall = null;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prf = new PrefManager(this);
        adsPref = new AdsPref(this);
        splashTxt = findViewById(R.id.splashTxt);
        logo = findViewById(R.id.logo);
        Anims aVar = new Anims(this.getResources().getDrawable(R.drawable.logo));
        aVar.m14932a(true);
        logo.setImageDrawable(aVar);
        logo.setVisibility(View.VISIBLE);
        new Handler(getMainLooper()).postDelayed(this::requestListPostApi,0);
    }


    private void onSplashFinished() {
        SplashActivity.this.runOnUiThread(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        });
    }

    private void requestListPostApi() {

        ApiInterface apiInterface = RestAdapter.createAPI(Config.WEBSITE_URL);

        callbackAdsCall = apiInterface.getAds(Config.DEVELOPER_NAME);

        callbackAdsCall.enqueue(new Callback<CallbackAds>() {
            @Override
            public void onResponse(Call<CallbackAds> call, Response<CallbackAds> response) {
                CallbackAds resp = response.body();
                if (resp != null && resp.status.equals("ok")) {
                    settings = resp.settings;
                    ads = resp.ads;
                    getConfig();
                    prf.saveSettings(settings.onesignal_app_id,settings.app_privacy_policy);
                }
            }

            @Override
            public void onFailure(Call<CallbackAds> call, Throwable t) {
                Log.e(TAG, "onFailure: Sorry String parse Issue",t);
            }

        });
    }


    private void getConfig() {
        String status = "0";
        if (ads.ad_status.equals("on")){
            status = "1";
        }
        adsPref.saveAds(
                true,
                ads.ad_type,
                ads.backup_ads,
                ads.admob_publisher_id,
                ads.admob_app_id,
                ads.admob_banner_unit_id,
                ads.admob_interstitial_unit_id,
                ads.admob_native_unit_id,
                ads.admob_app_open_ad_unit_id,
                ads.ad_manager_banner_unit_id,
                ads.ad_manager_interstitial_unit_id,
                ads.ad_manager_native_unit_id,
                ads.ad_manager_app_open_ad_unit_id,
                ads.fan_banner_unit_id,
                ads.fan_interstitial_unit_id,
                ads.fan_native_unit_id,
                ads.startapp_app_id,
                ads.unity_game_id,
                ads.unity_banner_placement_id,
                ads.unity_interstitial_placement_id,
                ads.applovin_banner_ad_unit_id,
                ads.applovin_interstitial_ad_unit_id,
                ads.applovin_native_ad_manual_unit_id,
                ads.applovin_app_open_ad_unit_id,
                ads.applovin_banner_zone_id,
                ads.applovin_banner_mrec_zone_id,
                ads.applovin_interstitial_zone_id,
                ads.ironsource_app_key,
                ads.ironsource_banner_id,
                ads.ironsource_interstitial_id,
                ads.wortise_app_id,
                ads.wortise_banner_unit_id,
                ads.wortise_interstitial_unit_id,
                ads.wortise_native_unit_id,
                ads.wortise_app_open_unit_id,
                ads.interstitial_ad_interval,
                ads.native_ad_interval,
                ads.native_ad_index
        );
        onSplashFinished();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCheck();
    }

    private void initCheck() {

        if (DarkModeUtils.isDarkModeEnabledWithUiModeManager(this)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            if (prf.loadNightModeState()==true){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }

    }
}
