package com.quiz.mathematics.ui;

import static android.app.PendingIntent.FLAG_MUTABLE;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiz.mathematics.R;
import com.quiz.mathematics.adapter.LanguageAdapter;
import com.quiz.mathematics.adapter.MainAdapter;
import com.quiz.mathematics.data.MainData;
import com.quiz.mathematics.model.LearnModel;
import com.quiz.mathematics.model.MainModel;
import com.quiz.mathematics.model.SubModel;
import com.quiz.mathematics.service.AlarmReceiver;
import com.quiz.mathematics.utils.ConnectionDetector;
import com.quiz.mathematics.utils.Constant;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.quiz.mathematics.ui.application.ONESIGNAL_APP_ID;
import static com.quiz.mathematics.utils.AdsInfo.loadInterstitialId;
import static com.quiz.mathematics.utils.Constant.EASY;
import static com.quiz.mathematics.utils.Constant.HARD;
import static com.quiz.mathematics.utils.Constant.MEDIUM;
import static com.quiz.mathematics.utils.Constant.getLanguageCode;
import static com.quiz.mathematics.utils.Constant.sendFeedback;
import static com.quiz.mathematics.utils.Constant.setDefaultLanguage;
import static com.quiz.mathematics.utils.Constant.setLanguageCode;
//import static com.quiz.mathematics.utils.URLData.registerData;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_PUSH_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_RELEASE_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

//import static com.example_2.brainchallenge.utils.Constant.PRIVACY_POLICY_LINK;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MainAdapter.MainItemClick {
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    MainAdapter mainAdapter;
    ImageView btn_drawer;
    DrawerLayout drawer;
    List<MainModel> mainModels = new ArrayList<>();
    Menu menu;
    MenuItem nav_language;
    InterstitialAd mInterstitialAd;
    ConnectionDetector cd;
    MenuItem nav_coin;
    LanguageAdapter languageAdapter;

    public static void showFeedbackDialog(Activity activity) {
        final androidx.appcompat.app.AlertDialog alertDialog;
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_feedback, null);
        builder.setView(view);

        final EditText edt_feedback = view.findViewById(R.id.edt_feedback);
        TextView btn_submit = view.findViewById(R.id.btn_submit);
        TextView btn_cancel = view.findViewById(R.id.btn_cancel);
        alertDialog = builder.create();
        Objects.requireNonNull(alertDialog.getWindow()).getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        PushDownAnim.setPushDownAnimTo(btn_submit).setScale(MODE_SCALE, 0.89f).setDurationPush(DEFAULT_PUSH_DURATION).setDurationRelease(DEFAULT_RELEASE_DURATION);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        alertDialog.show();
        btn_cancel.setOnClickListener(v -> alertDialog.dismiss());
        btn_submit.setOnClickListener(v -> {

            if (!TextUtils.isEmpty(edt_feedback.getText().toString())) {
                alertDialog.dismiss();
                sendFeedback(activity, edt_feedback.getText().toString());
            } else {
                Toast.makeText(activity, "" + activity.getString(R.string.empty_feedback), Toast.LENGTH_SHORT).show();
            }

        });

    }

    public static void showRatingDialog(Activity activity) {
        final AlertDialog alert_dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_rating, null);
        builder.setView(view);
        final RatingBar rating_bar = view.findViewById(R.id.rating_bar);
        TextView btn_submit = view.findViewById(R.id.btn_submit);
        TextView tv_no = view.findViewById(R.id.tv_no);
        PushDownAnim.setPushDownAnimTo(btn_submit).setScale(MODE_SCALE, 0.89f).setDurationPush(DEFAULT_PUSH_DURATION).setDurationRelease(DEFAULT_RELEASE_DURATION);

        alert_dialog = builder.create();
        alert_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alert_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationTheme;
        alert_dialog.show();

        btn_submit.setOnClickListener(v -> {
            if (rating_bar.getRating() >= 3) {
                try {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));

                } catch (ActivityNotFoundException anfe) {
                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
                }
                alert_dialog.dismiss();
            } else if (rating_bar.getRating() <= 0) {
                Toast.makeText(activity, "" + activity.getString(R.string.rating_error), Toast.LENGTH_SHORT).show();
            }

        });
        tv_no.setOnClickListener(v -> alert_dialog.dismiss());
    }

    public static void rate(Activity
                                    activity) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));

        } catch (ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cd = new ConnectionDetector(this);
        if (getResources().getString(R.string.HOME_ADS_VISIBILITY).equals("YES")) {
            CallNewInsertial();
        }

    }

    private void CallNewInsertial() {
        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
//            mInterstitialAd = new InterstitialAd(this);
//            mInterstitialAd.setAdUnitId(AdsInfo.getInterstitialId(getApplicationContext()));
//            requestNewInterstitial();

            loadInterstitialId(MainActivity.this, interstitialAd -> mInterstitialAd = interstitialAd);


        }
    }

//    private void requestNewInterstitial() {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mInterstitialAd.loadAd(adRequest);
//    }

    public Drawable getThemeDrawable(int drawableID) {
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), drawableID);
        assert drawable != null;
        drawable.setColorFilter(Constant.getThemeColor(this, R.attr.theme_text_color), PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Constant.getIsFirstTime(getApplicationContext())) {
            Constant.setDeviceLanguage(this);
        } else {
            setDefaultLanguage(this);
        }
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });


        List<String> testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231");
        RequestConfiguration configuration =
                new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
        MobileAds.setRequestConfiguration(configuration);


        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        OneSignal.setNotificationOpenedHandler(result -> {

        });


        OneSignal.setNotificationWillShowInForegroundHandler(notificationReceivedEvent -> {

        });
        OSDeviceState device = OneSignal.getDeviceState();

        String appId = device.getUserId();
        String dId = device.getPushToken();

        String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.e("imei---","--"+deviceId);



//        registerData(getApplicationContext(),appId,dId,deviceId);
//        getAllAdsId(getApplicationContext());


//
//        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
//        String imei = TelephonyMgr.getDeviceId();









        init();
        setClick();

        setNotification();

    }

    public void setNotification() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent alarmPendingIntent ;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            alarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, FLAG_MUTABLE);
        }
        else
        {
            alarmPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }


        long firstMillis = System.currentTimeMillis(); // first run of alarm is immediate
        int intervalMillis = 60000; // as of API 19, alarm manager will be forced up to 60000 to save battery
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, intervalMillis, alarmPendingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public String getTranslatedString(String s) {
        return Constant.getAllTranslatedDigit(s);
    }

    private void setClick() {
        btn_drawer.setOnClickListener(v -> drawer.openDrawer(GravityCompat.START));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showExitDialog();
        }
    }

    private void init() {
        progressDialog = new ProgressDialog(MainActivity.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        nav_language = menu.findItem(R.id.nav_language);
        nav_coin = menu.findItem(R.id.nav_coin);

        nav_language.setTitle(getString(R.string.language) + getString(R.string.single_space) + ":" + getString(R.string.single_space) + getLanguageCode(getApplicationContext()));
        nav_coin.setTitle(getString(R.string.coins) + getString(R.string.single_space) + Constant.getAllTranslatedDigit(String.valueOf(Constant.getCoins(getApplicationContext()))));
        btn_drawer = findViewById(R.id.btn_drawer);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        setDefaultLanguage(MainActivity.this);

        new GetData().execute();


        btn_drawer.setImageDrawable(getThemeDrawable(R.drawable.ic_dehaze_black_24dp));
    }

    public class GetData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            progressDialog.setMessage(getString(R.string.please_wait));
        }

        @Override
        protected String doInBackground(Void... voids) {
            mainModels.clear();

            mainModels = MainData.getMainModel(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();


            mainAdapter = new MainAdapter(MainActivity.this, mainModels);
            recyclerView.setAdapter(mainAdapter);


            mainAdapter.setMainClickListener(MainActivity.this);
            recyclerView.scrollToPosition(Constant.getExpandPosition(MainActivity.this));


            Log.e("getExpandPosition==", "" + Constant.getExpandPosition(MainActivity.this));
            setExpandView(Constant.getExpandPosition(MainActivity.this));



        }
    }




    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation analytical.xml item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_language) {
            showLanguageDialog();
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(MainActivity.this, ActivitySetting.class);
            startActivity(intent);
        } else if (id == R.id.nav_reminder) {
            Intent intent = new Intent(MainActivity.this, ActivityReminder.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            Constant.share(MainActivity.this);
        } else if (id == R.id.nav_rate_us) {
            showRatingDialog(this);
        } else if (id == R.id.nav_feedback) {
            showFeedbackDialog(this);
        } else if (id == R.id.nav_more_app) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=AzmiSoft Inc.")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=AzmiSoft Inc.")));
            }
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void mainItemClick(int main_id, int position, SubModel subModel, MainModel mainModel) {
        Constant.setExpandPosition(MainActivity.this, main_id);
        Constant.saveMainModel(MainActivity.this, mainModel);
        Constant.saveSubModel(MainActivity.this, subModel);

//            if (mInterstitialAd != null ) {
//                mInterstitialAd.show();
//                mInterstitialAd.setAdListener(new AdListener() {
//                    public void onAdClosed() {
//
//
//
//
//                        Log.e("subModel1", "" + Constant.getSubModel(MainActivity.this).title);
//
//                        if (subModel.TYPE_CODE == MainData.LEARN_QUIZ) {
//                            LearnModel learnModel = new LearnModel();
//                            learnModel.main_id = main_id;
//                            learnModel.sign = mainModel.sign;
//                            Constant.saveLearnModel(MainActivity.this, learnModel);
//                            startActivity(new Intent(MainActivity.this, LearnTableActivity.class));
//
//                        } else if (subModel.TYPE_CODE == MainData.DUEL) {
//                            showDualType();
//                        } else {
//                            startActivity(new Intent(MainActivity.this, LevelActivity.class));
//                        }
//
//                    }
//                });
//            } else {

        if (subModel.TYPE_CODE == MainData.LEARN_QUIZ) {
            LearnModel learnModel = new LearnModel();
            learnModel.main_id = main_id;
            learnModel.sign = mainModel.sign;
            Constant.saveLearnModel(this, learnModel);
            startActivity(new Intent(this, LearnTableActivity.class));

        } else if (subModel.TYPE_CODE == MainData.DUEL) {
            showDualType();
        } else {
            startActivity(new Intent(this, LevelActivity.class));
        }


    }

    public void showDualType() {
        setDefaultLanguage(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_dual_type, null);
        builder.setView(view);


        SubModel subModel = Constant.getSubModel(this);
        CardView btn_easy, btn_medium, btn_hard;
        TextView text_easy, text_medium, text_hard;
        btn_easy = view.findViewById(R.id.btn_easy);
        btn_medium = view.findViewById(R.id.btn_medium);
        btn_hard = view.findViewById(R.id.btn_hard);
        text_easy = view.findViewById(R.id.text_easy);
        text_medium = view.findViewById(R.id.text_medium);
        text_hard = view.findViewById(R.id.text_hard);


        btn_easy.setOnClickListener(v -> {
            setModel(EASY, subModel);
        });

        btn_medium.setOnClickListener(v -> {
            setModel(MEDIUM, subModel);
        });


        btn_hard.setOnClickListener(v -> {
            setModel(HARD, subModel);
        });

        if (subModel.mode_type == MEDIUM) {
            setSelectedType(btn_medium, text_medium);
        } else if (subModel.mode_type == HARD) {
            setSelectedType(btn_hard, text_hard);
        } else {
            setSelectedType(btn_easy, text_easy);
        }


        final AlertDialog dialog = builder.create();
        dialog.show();


        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

    }

    public void setSelectedType(CardView cardView, TextView textView) {
        cardView.setCardBackgroundColor(Constant.getThemeColor(this, R.attr.colorPrimary));
        textView.setTextColor(Color.WHITE);
    }

    public void setModel(int type, SubModel subModel) {
        subModel.mode_type = type;
        Constant.saveSubModel(this, subModel);



        if (mInterstitialAd != null ) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.e("close---","true");
                    startActivity(new Intent(MainActivity.this, DualActivity.class));



                }


                @Override
                public void onAdShowedFullScreenContent() {
                    Log.e("show---","true");
                }

            });



            mInterstitialAd.show(MainActivity.this);



//            mInterstitialAd.setAdListener(new AdListener() {
//                public void onAdClosed() {
//                    startActivity(new Intent(MainActivity.this, DualActivity.class));
//
//                }
//            });
        } else {
            startActivity(new Intent(MainActivity.this, DualActivity.class));


        }


    }

    @Override
    public void expandClick(int position) {


        mainModels.get(position).isExpand = !mainModels.get(position).isExpand;


        for (int i = 0; i < mainModels.size(); i++) {
            if (i != position) {
                mainModels.get(i).isExpand = false;
            }
        }

        mainAdapter.setExapndArray(mainModels);
        recyclerView.smoothScrollToPosition(position);


    }

    public void setExpandView(int position) {

        mainModels.get(position).isExpand = true;


        for (int i = 0; i < mainModels.size(); i++) {
            if (i != position) {
                mainModels.get(i).isExpand = false;
            }
        }

        mainAdapter.setExapndArray(mainModels);
        recyclerView.smoothScrollToPosition(position);

    }

    public void showLanguageDialog() {
        setDefaultLanguage(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_language, null);
        builder.setView(view);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);


        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

        final AlertDialog dialog = builder.create();
        dialog.show();
        languageAdapter = new LanguageAdapter(this, Constant.getLanguageList(getApplicationContext()), s -> {

            Log.e("s2----", "" + Constant.getLanguageCodeFromLanguage(getApplicationContext(), s));
            setLanguageCode(getApplicationContext(), Constant.getLanguageCodeFromLanguage(getApplicationContext(), s));
            Log.e("s1----", "" + Constant.getLanguageCode(this) + "===" + getLanguageCode(this));
            languageAdapter.notifyDataSetChanged();
            nav_language.setTitle(getString(R.string.language) + getString(R.string.single_space) + ":" + getString(R.string.single_space) +
                    getLanguageCode(getApplicationContext()));
            dialog.dismiss();
            refreshAcitivity();

        });
        recyclerView.setAdapter(languageAdapter);

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

    }

    public void refreshAcitivity() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(0, 0);
    }

    public void showExitDialog() {

        setDefaultLanguage(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_exit_app, null);
        builder.setView(view);

        TextView btn_yes = view.findViewById(R.id.btn_yes);
        TextView btn_no = view.findViewById(R.id.btn_no);

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        btn_yes.setOnClickListener(v -> {
            dialog.dismiss();
            ActivityCompat.finishAffinity(MainActivity.this);
        });


        btn_no.setOnClickListener(v -> {
            dialog.dismiss();
        });

    }


}
