package com.quiz.mathematics.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.quiz.mathematics.R;
import com.quiz.mathematics.database.DatabaseAccess;
import com.quiz.mathematics.model.MainModel;
import com.quiz.mathematics.model.ProgressModel;
import com.quiz.mathematics.model.SubModel;
import com.quiz.mathematics.utils.CenteredToolbar;
import com.quiz.mathematics.utils.ConnectionDetector;
import com.quiz.mathematics.utils.Constant;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.quiz.mathematics.utils.AdsInfo.loadInterstitialId;
import static com.quiz.mathematics.utils.AdsInfo.loadRewarded;
import static com.quiz.mathematics.utils.Constant.DEFAULT_QUESTION_SIZE;
import static com.quiz.mathematics.utils.Constant.setDefaultLanguage;


public class ScoreActivity extends BaseActivity {

    TextView tv_right_answer, tv_wrong_answer;
    ImageView img_retry, img_next, img_home, img_share;
    Button btn_view_answer;

    ProgressBar progress_bar;
    DatabaseAccess databaseAccess;
    int best_score;
    ConnectionDetector cd;
    List<ProgressModel> progressModels;
    boolean isVideoComplete;
    ProgressDialog progressDialog;
//    Handler addHandler = new Handler();
    Intent intent;
    CardView btn_retry, btn_next, btn_share, btn_home;
    TextView tv_coin, tv_set_count, tv_total_count, tv_score, tv_best_score;
    LinearLayout progressView;
    CenteredToolbar toolbar;
    int percentageProgress;
    MainModel mainModel;
    SubModel subModel;
    InterstitialAd mInterstitialAd;
//    Runnable addRunnable = new Runnable() {
//        public void run() {
//
//            if (mRewardedAd!=null) {
//                videoShow();
//                progressDialog.dismiss();
//                addHandler.removeCallbacks(addRunnable);
//            } else {
//                progressDialog.show();
//                addHandler.postDelayed(addRunnable, 500);
//            }
//
//        }
//    };


    public String getTranslatedString(String s) {
        return Constant.getAllTranslatedDigit(s);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        cd = new ConnectionDetector(this);

//        if (getResources().getString(R.string.ADS_VISIBILITY).equals("YES")) {
            CallNewInsertial();
//        }
        setAddViews();

    }

//    private void loadRewardedVideoAd() {
//        rewardedVideoAd.loadAd(AdsInfo.getRewardId(getApplicationContext()),
//                new AdRequest.Builder().build());
//    }

    public void setAddViews() {
//        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);

        loadRewarded(ScoreActivity.this, rewardedAd -> mRewardedAd = rewardedAd);

//        loadRewardedVideoAd();


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

//    private void requestNewInterstitial() {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mInterstitialAd.loadAd(adRequest);
//    }

    private void CallNewInsertial() {
        cd = new ConnectionDetector(ScoreActivity.this);
        if (cd.isConnectingToInternet()) {
//            mInterstitialAd = new InterstitialAd(ScoreActivity.this);
//            mInterstitialAd.setAdUnitId(AdsInfo.getInterstitialId(getApplicationContext()));
//            requestNewInterstitial();
            loadInterstitialId(ScoreActivity.this, interstitialAd -> mInterstitialAd = interstitialAd);

        }
    }

    public void videoShow() {


        isEarn=false;

        mRewardedAd.show(ScoreActivity.this, rewardItem -> {
            isEarn=true;

        });

        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
            }



            @Override
            public void onAdDismissedFullScreenContent() {
                if(isEarn) {
                    Constant.setCoins(getApplicationContext(), (Constant.getCoins(getApplicationContext()) + 10));
                    showGetCoinsDialogs();
                }
            }
        });




//        isVideoComplete = false;
//        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
//            @Override
//            public void onRewardedVideoAdLoaded() {
//
//            }
//
//            @Override
//            public void onRewardedVideoAdOpened() {
//
//            }
//
//            @Override
//            public void onRewardedVideoStarted() {
//
//            }
//
//            @Override
//            public void onRewardedVideoAdClosed() {
//                if (isVideoComplete) {
//                    Constant.setCoins(getApplicationContext(), (Constant.getCoins(getApplicationContext()) + 10));
//                    showGetCoinsDialogs();
//                }
//
//
//            }
//
//            @Override
//            public void onRewarded(RewardItem rewardItem) {
//
//            }
//
//            @Override
//            public void onRewardedVideoAdLeftApplication() {
//
//            }
//
//            @Override
//            public void onRewardedVideoAdFailedToLoad(int i) {
//
//            }
//
//            @Override
//            public void onRewardedVideoCompleted() {
//                isVideoComplete = true;
//
//            }
//        });


    }


    private void init() {

        subModel = Constant.getSubModel(this);
        mainModel = Constant.getMainModel(this);


        progressModels = new ArrayList<>();


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> backIntent());


        img_home = findViewById(R.id.img_home);
        img_next = findViewById(R.id.img_next);
        img_retry = findViewById(R.id.img_retry);
        img_share = findViewById(R.id.img_share);
        btn_share = findViewById(R.id.btn_share);
        btn_retry = findViewById(R.id.btn_retry);
        tv_total_count = findViewById(R.id.tv_total_count);
        tv_set_count = findViewById(R.id.tv_set_count);
        tv_coin = findViewById(R.id.tv_coin);
        tv_score = findViewById(R.id.tv_score);
        tv_best_score = findViewById(R.id.tv_best_score);
        progress_bar = findViewById(R.id.progress_bar);
        progressView = findViewById(R.id.progressView);
        tv_wrong_answer = findViewById(R.id.tv_wrong_answer);
        tv_right_answer = findViewById(R.id.tv_right_answer);
        btn_home = findViewById(R.id.btn_home);
        btn_next = findViewById(R.id.btn_next);
        btn_view_answer = findViewById(R.id.btn_view_answer);


        tv_score.setText(getTranslatedString(getString(R.string.score) + getString(R.string.str_space) + subModel.score));
        tv_coin.setText(getTranslatedString(String.valueOf(Constant.getCoins(getApplicationContext()))));
        tv_set_count.setText(getTranslatedString(String.valueOf(subModel.level_no)));
        tv_total_count.setText(getTranslatedString(getString(R.string.slash) + DEFAULT_QUESTION_SIZE));


        getSupportActionBar().setTitle(subModel.title + " " + getString(R.string.mode) + " " + mainModel.title);

        tv_right_answer.setText(getTranslatedString(String.valueOf(subModel.right_count)));
        tv_wrong_answer.setText(getTranslatedString(String.valueOf(subModel.wrong_count)));


        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        progressModels = databaseAccess.getScore(mainModel.tableName, subModel.type, subModel.level_no);
        databaseAccess.close();

        if (progressModels.size() > 0) {
            best_score = progressModels.get(0).highScore;
            tv_best_score.setText(getTranslatedString(getString(R.string.best_score) + getString(R.string.str_space) + best_score));
        }

        Log.e("best_score", "" + best_score + "===" + subModel.score);


        if (best_score < subModel.score) {
            best_score = subModel.score;
        }


        Log.e("best_score1", "" + best_score + "===" + subModel.score);
        percentageProgress = (subModel.right_count * 100) / DEFAULT_QUESTION_SIZE;

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        databaseAccess.updateProgress(subModel.level_no, subModel.type, mainModel.tableName, percentageProgress);
        databaseAccess.close();

        updateScores();


        if (Constant.DEFAULT_QUESTION_SIZE <= subModel.level_no || percentageProgress < 75) {
            btn_next.setAlpha(0.5f);
            img_next.setAlpha(0.5f);
        }

        setClick();
        setThemes(percentageProgress);


    }


    public void updateScores() {

        Log.e("percentageProgress", "" + percentageProgress);
        if (percentageProgress >= 50) {
            if (DEFAULT_QUESTION_SIZE >= subModel.level_no) {
                databaseAccess = DatabaseAccess.getInstance(this);
                databaseAccess.open();
                databaseAccess.updateLevel(subModel.type, (subModel.level_no + 1), mainModel.tableName, 1);
                databaseAccess.close();
            }
        }
        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        databaseAccess.updateScore(subModel.type, subModel.level_no, mainModel.tableName, subModel.score, best_score);
        databaseAccess.close();
    }

    public void setThemes(int percentageProgress) {

        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);

            if (percentageProgress == 0) {
                imageView.setImageDrawable(getThemeDrawable(R.drawable.ic_star_border));
            } else {
                if (Constant.getStarCount(percentageProgress) >= (i + 1)) {
                    imageView.setImageDrawable(getThemeDrawable(R.drawable.ic_star));
                } else {
                    imageView.setImageDrawable(getThemeDrawable(R.drawable.ic_star_border));
                }
            }
            progressView.addView(imageView);
        }


        progress_bar.setMax(DEFAULT_QUESTION_SIZE);
        progress_bar.setProgress(subModel.level_no);

        int textColor = Constant.getThemeColor(this, R.attr.theme_text_color);
        img_share.getDrawable().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        img_retry.getDrawable().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        img_home.getDrawable().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        img_next.getDrawable().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);


    }

    public Drawable getThemeDrawable(int drawableID) {
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), drawableID);
        assert drawable != null;
        drawable.setColorFilter(Constant.getThemeColor(this, R.attr.theme_text_color), PorterDuff.Mode.SRC_IN);
        return drawable;
    }


    public void setClick() {
        btn_next.setOnClickListener(v -> {
            if (Constant.getStarCount(percentageProgress) >= 2) {
                if (DEFAULT_QUESTION_SIZE >= subModel.level_no) {
                    subModel.level_no = subModel.level_no + 1;
                    Constant.saveSubModel(this, subModel);
                    passIntent(Constant.getTypeClass(subModel));
                }
            } else {
                Toast.makeText(this, "" + getString(R.string.clear_previous_level), Toast.LENGTH_SHORT).show();
            }

        });

        btn_home.setOnClickListener(v -> {
            passIntent(LevelActivity.class);
        });

        btn_view_answer.setOnClickListener(v -> showDialogs());


        btn_share.setOnClickListener(v -> Constant.share(ScoreActivity.this));

        btn_retry.setOnClickListener(v -> {
                if (mInterstitialAd != null ) {


                    mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            Log.e("close---","true");
                            passIntent(Constant.getTypeClass(subModel));

                        }


                        @Override
                        public void onAdShowedFullScreenContent() {
                            Log.e("show---","true");
                        }

                    });
                    mInterstitialAd.show(ScoreActivity.this);
//                    mInterstitialAd.setAdListener(new AdListener() {
//                        public void onAdClosed() {
//                            passIntent(Constant.getTypeClass(subModel));
//                        }
//                    });




                } else {
                    passIntent(Constant.getTypeClass(subModel));
                }

        });
    }


    public void passIntent(Class c) {
        intent = new Intent(this, c);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        backIntent();
    }

    public void backIntent() {
//        if (addHandler != null) {
//            addHandler.removeCallbacks(addRunnable);
//        }
        passIntent(LevelActivity.class);
    }


    public void showDialogs() {
        setDefaultLanguage(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_view_answer, null);
        builder.setView(view);

        LinearLayout btn_show_video = view.findViewById(R.id.btn_show_video);
        LinearLayout btn_use_coin = view.findViewById(R.id.btn_use_coin);
        TextView text_coint = view.findViewById(R.id.text_coint);
        ImageView btn_close = view.findViewById(R.id.btn_close);

        final AlertDialog dialog = builder.create();
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        if (Constant.getCoins(getApplicationContext()) < 10) {
            text_coint.setText(getString(R.string.coins_error));
        } else {
            text_coint.setText(getTranslatedString(getString(R.string.view_answer_coin) + "(" + (Constant.getCoins(getApplicationContext())) + ")"));
            btn_use_coin.setOnClickListener(v -> {
                Constant.setCoins(getApplicationContext(), (Constant.getCoins(getApplicationContext()) - 10));
                tv_coin.setText(getTranslatedString(String.valueOf(Constant.getCoins(getApplicationContext()))));
                showHistoryDialogs();
                dialog.dismiss();
            });
        }

        btn_close.setOnClickListener(v -> dialog.dismiss());

        btn_show_video.setOnClickListener(v -> {
            showVideo();
            dialog.dismiss();
        });

    }


    public void showGetCoinsDialogs() {
        setDefaultLanguage(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_get_coin, null);
        builder.setView(view);

        TextView btn_ok = view.findViewById(R.id.btn_ok);
        TextView btn_cancel = view.findViewById(R.id.btn_cancel);
        final AlertDialog dialog = builder.create();
        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        btn_cancel.setOnClickListener(v -> {
            dialog.dismiss();
            showHistoryDialogs();
        });
        btn_ok.setOnClickListener(v -> {
            dialog.dismiss();
            showHistoryDialogs();
        });
    }


    public void showHistoryDialogs() {

        Intent intent = new Intent(ScoreActivity.this, ReviewAnswerActivity.class);
        startActivityForResult(intent, 256);

    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 256) {


            if (resultCode == Activity.RESULT_OK) {


            }
        }
    }

    private RewardedAd mRewardedAd;

    boolean isEarn=false;

    public void showVideo() {
        if (cd.isConnectingToInternet()) {
            if (mRewardedAd != null ) {
                videoShow();

            } else {
                setAddViews();
                Toast.makeText(this, "" + getString(R.string.str_video_error), Toast.LENGTH_SHORT).show();

//
//                progressDialog = new ProgressDialog(this);
//                progressDialog.setCancelable(false);
//                progressDialog.setMessage(getString(R.string.please_wait));
//                progressDialog.show();
//                addHandler.postDelayed(addRunnable, 50);

            }

        } else {
            setAddViews();
            Toast.makeText(this, "" + getString(R.string.str_video_error), Toast.LENGTH_SHORT).show();

        }

    }


}
