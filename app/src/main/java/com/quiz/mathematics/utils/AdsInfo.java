package com.quiz.mathematics.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.quiz.mathematics.model.AdsModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdsInfo {


    public static String getBannerId(Context context) {
        String id = "ca-app-pub-3940256099942544/6300978111";
        AdsModel.Adsdetail adsdetail = Constant.getAdsModel(context);

        if (adsdetail != null) {
            if (adsdetail.bannerAdsIdStatus.equals("1")) {
                if (!TextUtils.isEmpty(adsdetail.bannerAdsId)) {
                    id = adsdetail.bannerAdsId;
                }
            }
        }

        return id;
    }


    public static String getInterstitialId(Context context) {
        String id = "ca-app-pub-3940256099942544/1033173712";
        AdsModel.Adsdetail adsdetail = Constant.getAdsModel(context);

        if (adsdetail != null) {
            if (adsdetail.interstitialAdsIdStatus.equals("1")) {
                if (!TextUtils.isEmpty(adsdetail.interstitialAdsId)) {
                    id = adsdetail.interstitialAdsId;
                }
            }
        }

        return id;
    }

    public static void loadInterstitialId(Activity context, onInterstitialId onInterstitialId) {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context, AdsInfo.getInterstitialId(context), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
//                        mInterstitialAd = interstitialAd;
                        if (onInterstitialId != null) {
                            onInterstitialId.onLoaded(interstitialAd);
                        }

                        Log.e("interstitialAd---","--true");
//                            Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        Log.e("interstitialAd---","--false");


                        // Handle the error
//                            Log.i(TAG, loadAdError.getMessage());
//                        mInterstitialAd = null;
                    }
                });

    }

    public static void loadRewarded(Activity context, onRewarded onInterstitialId) {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(context, AdsInfo.getRewardId(context),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                        Log.e("rewardedAd---","--false"+AdsInfo.getRewardId(context));

                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        Log.e("rewardedAd---","--true"+AdsInfo.getRewardId(context));

                        if (onInterstitialId != null) {
                            onInterstitialId.onLoaded(rewardedAd);
                        }
                    }
                });





    }

    public static String getRewardId(Context context) {
//        String id = "ca-app-pub-3940256099942544/5354046379";
        String id = "ca-app-pub-3940256099942544/5224354917";

        AdsModel.Adsdetail adsdetail = Constant.getAdsModel(context);

        if (adsdetail != null) {

            if (adsdetail.rewardedVideoAdsIdStatus.equals("1")) {
                if (!TextUtils.isEmpty(adsdetail.rewardedVideoAdsId)) {
                    id = adsdetail.rewardedVideoAdsId;
                }
            }
        }

        return id;
    }


    public interface onInterstitialId {

        void onLoaded(InterstitialAd interstitialAd);
    }


    public interface onRewarded {

        void onLoaded(RewardedAd interstitialAd);
    }


}
