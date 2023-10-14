package com.quiz.mathematics.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdsModel {

    @SerializedName("data")
    @Expose
    public Data data;


    public class Data {

        @SerializedName("success")
        @Expose
        public Integer success;
        @SerializedName("adsdetail")
        @Expose
        public Adsdetail adsdetail;
    }

    public class Adsdetail {

        @SerializedName("ads_control_id")
        @Expose
        public String adsControlId;

        @SerializedName("app_ids")
        @Expose
        public String appIds;

        @SerializedName("banner_ads_id")
        @Expose
        public String bannerAdsId;

        @SerializedName("interstitial_ads_id")
        @Expose
        public String interstitialAdsId;

        @SerializedName("rewarded_video_ads_id")
        @Expose
        public String rewardedVideoAdsId;

        @SerializedName("app_ids_status")
        @Expose
        public String appIdsStatus;

        @SerializedName("banner_ads_id_status")
        @Expose
        public String bannerAdsIdStatus;

        @SerializedName("interstitial_ads_id_status")
        @Expose
        public String interstitialAdsIdStatus;

        @SerializedName("rewarded_video_ads_id_status")
        @Expose
        public String rewardedVideoAdsIdStatus;

        @SerializedName("created_at")
        @Expose
        public String createdAt;


        @SerializedName("updated_at")
        @Expose
        public String updatedAt;

    }

}
