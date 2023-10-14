package com.quiz.mathematics.utils;

//import static com.android.volley.Request.Method.GET;
//import static com.android.volley.Request.Method.POST;

//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;


public class URLData {

    public static String BASE_URL = "https://templatevictory.com/ultimatemathquiz/api/";

    public static String REGISTER_URL = BASE_URL + "register.php";

    public static String ADS_URL = BASE_URL + "adsdetail.php";


//    public static void getAllAdsId(Context context) {
//
//
//        StringRequest stringRequest = new StringRequest(GET, ADS_URL, response -> {
//
//            AdsModel adsModel = new Gson().fromJson(response, AdsModel.class);
//
//            if (adsModel != null) {
//                if (adsModel.data.success == 1) {
//
//                    if (adsModel.data.adsdetail != null) {
//                        Log.e("rewardedVideoAdsId--", "---" + adsModel.data.adsdetail.rewardedVideoAdsIdStatus);
//                        Constant.saveAdsModel(context, adsModel.data.adsdetail);
//                    }
//                }
//            }
//
//            Log.e("response12--", "---" + response);
//
//        }, error -> Log.e("error==", "--" + error)) {
//
//        };
//        Volley.newRequestQueue(context).add(stringRequest);
//    }


//    public static void registerData(Context context, String app_id, String device, String deviceId) {
//
//        Log.e("app_id--", "---" + app_id + "---" + device + "---" + deviceId);
//
//
//        StringRequest stringRequest = new StringRequest(POST, REGISTER_URL, response -> {
//
//
//            Log.e("response--", "---" + response);
//
//        }, error -> Log.e("error==", "--" + error)) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> mParams = new HashMap<>();
//                mParams.put("device_id", deviceId);
//                mParams.put("device", device);
//                mParams.put("app_id", app_id);
//                return mParams;
//            }
//        };
//        Volley.newRequestQueue(context).add(stringRequest);
//    }

}
