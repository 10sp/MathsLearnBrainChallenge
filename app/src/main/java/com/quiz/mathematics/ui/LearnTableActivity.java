package com.quiz.mathematics.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.quiz.mathematics.R;
import com.quiz.mathematics.adapter.BottomPagerAdapter;
import com.quiz.mathematics.adapter.TopPagerAdapter;
import com.quiz.mathematics.model.LearnModel;
import com.quiz.mathematics.utils.CenteredToolbar;
import com.quiz.mathematics.utils.ConnectionDetector;
import com.quiz.mathematics.utils.Constant;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.material.tabs.TabLayout;

import static com.quiz.mathematics.utils.AdsInfo.loadInterstitialId;
import static com.quiz.mathematics.utils.Constant.setDefaultLanguage;


public class LearnTableActivity extends AppCompatActivity {

    public static int height;
    public static int width;
    BottomPagerAdapter bottomPagerAdapter;
    ViewPager topViewPager;
    ViewPager bottomViewPager;
    TabLayout tabLayout;
    CardView btn_play;
    private int table_no = 1;
    InterstitialAd mInterstitialAd;
    ConnectionDetector cd;
    private int table_page = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultLanguage(this);
        setContentView(R.layout.activity_learn_table);
        init();
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

            loadInterstitialId(LearnTableActivity.this, interstitialAd -> mInterstitialAd = interstitialAd);

        }
    }

//    private void requestNewInterstitial() {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mInterstitialAd.loadAd(adRequest);
//    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LearnTableActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void init() {


        if (Constant.getLearnModel(this) != null) {
            table_no = Constant.getLearnModel(this).table_no;
            table_page = Constant.getLearnModel(this).table_page;
            if (table_no == 0) {
                table_no = 1;
            }
        }


        CenteredToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.learn_table));
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(LearnTableActivity.this, MainActivity.class);
            startActivity(intent);
        });

        tabLayout = findViewById(R.id.tabLayout);
        btn_play = findViewById(R.id.btn_play);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;


        int count = 6;
        if (width == Constant.DEVICE_1080) {
            count = 7;
        } else if (width == Constant.DEVICE_720) {
            count = 8;
        }


        bottomViewPager = findViewById(R.id.bottomViewPager);
        topViewPager = findViewById(R.id.topViewPager);
        topViewPager.setAdapter(new TopPagerAdapter(this, count, position -> {
            table_no = position;
            setBottomPager();
        }));


        topViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if (position == 0) {
                    table_no = 1;

                } else {
                    table_no = 11;
                }

                setBottomPager();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setBottomPager();


        btn_play.setOnClickListener(view -> {


            if (mInterstitialAd != null ) {

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.e("close---","true");
                        passPracticeIntent();

                    }


                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.e("show---","true");
                    }

                });
                mInterstitialAd.show(LearnTableActivity.this);
//                mInterstitialAd.setAdListener(new AdListener() {
//                    public void onAdClosed() {
//                        passPracticeIntent();
//
//                    }
//                });


            } else {
                passPracticeIntent();
            }


        });


    }

    public void passPracticeIntent() {
        LearnModel learnModel = Constant.getLearnModel(this);

        Log.e("learnModel==", "" + learnModel);
        if (learnModel != null) {
            learnModel.table_no = table_no;
            learnModel.table_page = table_page;
            Constant.saveLearnModel(this, learnModel);
            startActivity(new Intent(this, LearnQuizActivity.class));
        }

    }


    public void setBottomPager() {
        bottomPagerAdapter = new BottomPagerAdapter(this, width);
        bottomPagerAdapter.setTableNo(table_no);
        bottomViewPager.setAdapter(bottomPagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(bottomViewPager);
        bottomViewPager.setCurrentItem(table_page);


        bottomViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                table_page = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


}
