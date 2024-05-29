package com.dsquare.wallzee.Utils;

import static com.dsquare.wallzee.Config.LEGACY_GDPR;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.ymg.ads.sdk.BuildConfig;
import com.ymg.ads.sdk.format.AdNetwork;
import com.ymg.ads.sdk.format.AppOpenAd;
import com.ymg.ads.sdk.format.BannerAd;
import com.ymg.ads.sdk.format.InterstitialAd;
import com.ymg.ads.sdk.format.NativeAd;
import com.ymg.ads.sdk.format.NativeAdView;
import com.ymg.ads.sdk.format.RewardedAd;
import com.ymg.ads.sdk.gdpr.GDPR;
import com.ymg.ads.sdk.gdpr.LegacyGDPR;
import com.ymg.ads.sdk.util.OnInterstitialAdDismissedListener;
import com.ymg.ads.sdk.util.OnInterstitialAdShowedListener;
import com.ymg.ads.sdk.util.OnRewardedAdCompleteListener;
import com.ymg.ads.sdk.util.OnRewardedAdDismissedListener;
import com.ymg.ads.sdk.util.OnRewardedAdErrorListener;
import com.ymg.ads.sdk.util.OnShowAdCompleteListener;
import com.dsquare.wallzee.Config;
import com.dsquare.wallzee.R;

public class AdsManager {

    Activity activity;
    AdNetwork.Initialize adNetwork;
    AppOpenAd.Builder appOpenAd;
    BannerAd.Builder bannerAd;
    InterstitialAd.Builder interstitialAd;
    NativeAd.Builder nativeAd;
    NativeAdView.Builder nativeAdView;
    RewardedAd.Builder rewardedAd;
    PrefManager sharedPref;
    AdsPref adsPref;
    LegacyGDPR legacyGDPR;
    GDPR gdpr;

    public AdsManager(Activity activity) {
        this.activity = activity;
        this.sharedPref = new PrefManager(activity);
        this.adsPref = new AdsPref(activity);
        this.legacyGDPR = new LegacyGDPR(activity);
        this.gdpr = new GDPR(activity);
        adNetwork = new AdNetwork.Initialize(activity);
        appOpenAd = new AppOpenAd.Builder(activity);
        bannerAd = new BannerAd.Builder(activity);
        interstitialAd = new InterstitialAd.Builder(activity);
        nativeAd = new NativeAd.Builder(activity);
        nativeAdView = new NativeAdView.Builder(activity);
        rewardedAd = new RewardedAd.Builder(activity);
    }

    public void initializeAd() {
        if (adsPref.getAdStatus()) {
            adNetwork.setAdStatus("1")
                    .setAdNetwork(adsPref.getAdType())
                    .setBackupAdNetwork(adsPref.getBackupAds())
                    .setStartappAppId(adsPref.getStartappAppId())
                    .setUnityGameId(adsPref.getUnityGameId())
                    .setIronSourceAppKey(adsPref.getIronSourceAppKey())
                    .setWortiseAppId(adsPref.getWortiseAppId())
                    .setDebug(BuildConfig.DEBUG)
                    .build();
        }
    }

    public void loadAppOpenAd(boolean placement, OnShowAdCompleteListener onShowAdCompleteListener) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                appOpenAd = new AppOpenAd.Builder(activity)
                        .setAdStatus("1")
                        .setAdNetwork(adsPref.getAdType())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobAppOpenId(adsPref.getAdMobAppOpenAdId())
                        .setAdManagerAppOpenId(adsPref.getAdManagerAppOpenAdId())
                        .setApplovinAppOpenId(adsPref.getAppLovinAppOpenAdUnitId())
                        .setWortiseAppOpenId(adsPref.getWortiseAppOpenId())
                        .build(onShowAdCompleteListener);
            } else {
                onShowAdCompleteListener.onShowAdComplete();
            }
        } else {
            onShowAdCompleteListener.onShowAdComplete();
        }
    }

    public void loadAppOpenAd(boolean placement) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                appOpenAd = new AppOpenAd.Builder(activity)
                        .setAdStatus("1")
                        .setAdNetwork(adsPref.getAdType())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobAppOpenId(adsPref.getAdMobAppOpenAdId())
                        .setAdManagerAppOpenId(adsPref.getAdManagerAppOpenAdId())
                        .setApplovinAppOpenId(adsPref.getAppLovinAppOpenAdUnitId())
                        .setWortiseAppOpenId(adsPref.getWortiseAppOpenId())
                        .build();
            }
        }
    }

    public void showAppOpenAd(boolean placement) {
        if (placement) {
            appOpenAd.show();
        }
    }

    public void destroyAppOpenAd(boolean placement) {
        if (placement) {
            appOpenAd.destroyOpenAd();
        }
    }

    public void loadBannerAd(boolean placement) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                bannerAd.setAdStatus("1")
                        .setAdNetwork(adsPref.getAdType())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobBannerId(adsPref.getAdMobBannerId())
                        .setGoogleAdManagerBannerId(adsPref.getAdManagerBannerId())
                        .setFanBannerId(adsPref.getFanBannerId())
                        .setUnityBannerId(adsPref.getUnityBannerPlacementId())
                        .setAppLovinBannerId(adsPref.getAppLovinBannerAdUnitId())
                        .setAppLovinBannerZoneId(adsPref.getAppLovinBannerZoneId())
                        .setIronSourceBannerId(adsPref.getIronSourceBannerId())
                        .setWortiseBannerId(adsPref.getWortiseBannerId())
                        .setDarkTheme(sharedPref.loadNightModeState())
                        .setPlacementStatus(1)
                        .setLegacyGDPR(LEGACY_GDPR)
                        .build();
            }
        }
    }

    public void loadInterstitialAd(boolean placement, int interval) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                interstitialAd.setAdStatus("1")
                        .setAdNetwork(adsPref.getAdType())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobInterstitialId(adsPref.getAdMobInterstitialId())
                        .setGoogleAdManagerInterstitialId(adsPref.getAdManagerInterstitialId())
                        .setFanInterstitialId(adsPref.getFanInterstitialId())
                        .setUnityInterstitialId(adsPref.getUnityInterstitialPlacementId())
                        .setAppLovinInterstitialId(adsPref.getAppLovinInterstitialAdUnitId())
                        .setAppLovinInterstitialZoneId(adsPref.getAppLovinInterstitialZoneId())
                        .setIronSourceInterstitialId(adsPref.getIronSourceInterstitialId())
                        .setWortiseInterstitialId(adsPref.getWortiseInterstitialId())
                        .setInterval(interval)
                        .setPlacementStatus(1)
                        .setLegacyGDPR(LEGACY_GDPR)
                        .build();
            }
        }
    }

    public void loadNativeAd(boolean placement, String style) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                nativeAd.setAdStatus("1")
                        .setAdNetwork(adsPref.getAdType())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobNativeId(adsPref.getAdMobNativeId())
                        .setAdManagerNativeId(adsPref.getAdManagerNativeId())
                        .setFanNativeId(adsPref.getFanNativeId())
                        .setAppLovinNativeId(adsPref.getAppLovinNativeAdManualUnitId())
                        .setWortiseNativeId(adsPref.getWortiseNativeId())
                        .setPlacementStatus(1)
                        .setDarkTheme(sharedPref.loadNightModeState())
                        .setLegacyGDPR(LEGACY_GDPR)
                        .setNativeAdStyle(style)
                        .setNativeAdBackgroundColor(R.color.white, R.color.black)
                        .build();
            }
        }
    }

    public void loadNativeAdView(View view, boolean placement, String style) {
        if (placement) {
            if (adsPref.getAdStatus()) {
                nativeAdView.setAdStatus("1")
                        .setAdNetwork(adsPref.getAdType())
                        .setBackupAdNetwork(adsPref.getBackupAds())
                        .setAdMobNativeId(adsPref.getAdMobNativeId())
                        .setAdManagerNativeId(adsPref.getAdManagerNativeId())
                        .setFanNativeId(adsPref.getFanNativeId())
                        .setAppLovinNativeId(adsPref.getAppLovinNativeAdManualUnitId())
                        .setWortiseNativeId(adsPref.getWortiseNativeId())
                        .setPlacementStatus(1)
                        .setDarkTheme(sharedPref.loadNightModeState())
                        .setLegacyGDPR(LEGACY_GDPR)
                        .setNativeAdStyle(style)
                        .setView(view)
                        .setNativeAdBackgroundColor(R.color.white, R.color.black)
                        .build();
            }
        }
    }

    public void showRewardedAd(Context context, View view, ImageView imageView, Dialog dialog) {
        rewardedAd.show(new OnRewardedAdCompleteListener() {
            @Override
            public void onRewardedAdComplete() {
                Toast.makeText(activity, "Rewarded complete", Toast.LENGTH_SHORT).show();
                view.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                dialog.dismiss();
            }
        }, new OnRewardedAdDismissedListener() {
            @Override
            public void onRewardedAdDismissed() {

            }
        }, new OnRewardedAdErrorListener() {
            @Override
            public void onRewardedAdError() {
                Toast.makeText(activity, "Rewarded error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface RewardedAdListener {
        void onRewardedAdCompleted();
    }


    public void showInterstitialAd() {
        interstitialAd.show();
    }

    public void showInterstitialAd(OnInterstitialAdShowedListener show, OnInterstitialAdDismissedListener dismiss) {
        interstitialAd.show(show, dismiss);
    }

    public void destroyBannerAd() {
        bannerAd.destroyAndDetachBanner();
    }

//    public void resumeBannerAd(boolean placement) {
//        if (adsPref.getAdStatus().equals(AD_STATUS_ON) && !adsPref.getIronSourceBannerId().equals("0")) {
//            if (adsPref.getAdType().equals(IRONSOURCE) || adsPref.getBackupAds().equals(IRONSOURCE)) {
//                loadBannerAd(placement);
//            }
//        }
//    }

    public void updateConsentStatus() {
        if (LEGACY_GDPR) {
            legacyGDPR.updateLegacyGDPRConsentStatus(adsPref.getAdMobPublisherId(), Config.PRIVACY_POLICY_URL);
        } else {
            gdpr.updateGDPRConsentStatus();
        }
    }

}
