package com.quiz.mathematics.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.quiz.mathematics.R;
import com.quiz.mathematics.data.RandomOptionData;
import com.quiz.mathematics.model.HistoryModel;
import com.quiz.mathematics.model.MainModel;
import com.quiz.mathematics.model.QuizModel;
import com.quiz.mathematics.model.SubModel;
import com.quiz.mathematics.model.TextModel;
import com.quiz.mathematics.receiver.NotificationScheduler;
import com.quiz.mathematics.utils.AdVideoInterface;
import com.quiz.mathematics.utils.AnimatorUtils;
import com.quiz.mathematics.utils.CenterLineTextView;
import com.quiz.mathematics.utils.CenteredToolbar;
import com.quiz.mathematics.utils.ConnectionDetector;
import com.quiz.mathematics.utils.Constant;
import com.quiz.mathematics.utils.ConstantDialog;
import com.quiz.mathematics.utils.ExitInterface;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.ogaclejapan.arclayout.ArcLayout;
import com.thekhaeng.pushdownanim.PushDownAnim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.quiz.mathematics.utils.AdsInfo.loadInterstitialId;
import static com.quiz.mathematics.utils.AdsInfo.loadRewarded;
import static com.quiz.mathematics.utils.Constant.DELAY_SEOCND;
import static com.quiz.mathematics.utils.Constant.getPlusScore;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_PUSH_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_RELEASE_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;


public class TrueFalseActivity extends BaseActivity implements View.OnClickListener, AdVideoInterface, ExitInterface {

    CardView card_1, card_2;
    ImageView btn_help_line;
    CenterLineTextView textView1;
    TextView audience_op_1, audience_op_2, tv_set, tv_score, tv_plus_score, tv_right_count, tv_wrong_count, tv_timer,
            tv_coin, tv_question_count, tv_total_count, btn_op_false, btn_op_true;
    List<QuizModel> quizModelList = new ArrayList<>();
    ProgressDialog progressDialog;
    ImageView btn_fifty, btn_timer, btn_audiance;
    boolean isTimer, isCount;
    Vibrator vibe;
    boolean isHelpLine = true;
    List<TextModel> optionViewList = new ArrayList<>();
    List<Integer> integerArrayList = new ArrayList<>();
    QuizModel quizModel;
    View menuLayout;
    int history_id, helpLineCount, position, countTime, score,
            plusScore, wrong_answer_count, coin, right_answer_count;
    LinearLayout helpLineView;
    Intent intent;
    List<HistoryModel> historyModels = new ArrayList<>();
    ArcLayout arcLayout;
    Handler handler = new Handler();
    ProgressBar progress_bar;
    RelativeLayout layout_cell;
    String historyQuestion, historyAnswer, historyUserAnswer;
    CenteredToolbar toolbar;
    CountDownTimer countDownTimer;
    MediaPlayer answerPlayer;
    boolean isClick = true;
    boolean isVideoComplete = false;
//
    private RewardedAd mRewardedAd;

    ConnectionDetector cd;
    InterstitialAd mInterstitialAd;
    MainModel mainModel;
    SubModel subModel;
    final Runnable r = this::setNextData;
    LinearLayout linear_1, linear_2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_false);
        init();
    }

    public void startTimer(final int count) {
        countDownTimer = new CountDownTimer(count * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isTimer = true;
                countTime = (int) millisUntilFinished / 1000;
                tv_timer.setText(getTranslatedString(String.valueOf((millisUntilFinished / 1000))));
                progress_bar.setProgress(countTime);
                plusScore = getPlusScore(countTime);
                tv_plus_score.setText(getTranslatedString(getString(R.string.addition_sign) + plusScore));
            }

            @Override
            public void onFinish() {
                isTimer = false;
                helpLineCount++;
                setHelpLineView();
                if (helpLineCount > 3) {
                    if (!isVideoComplete) {
                        cancelTimer();
                        ConstantDialog.showVideoDialogs(TrueFalseActivity.this, TrueFalseActivity.this);
                    } else {
                        showFullScreenAds();
                    }

                } else {
                    handler.postDelayed(r, DELAY_SEOCND);
                }


            }
        }.start();
    }

    public void setCoins() {
        coin = Constant.getCoins(getApplicationContext());
        tv_coin.setText(getTranslatedString(String.valueOf(coin)));
    }

    public void addCoins() {
        Constant.setCoins(getApplicationContext(), (coin + 2));
    }

    private void init() {
        vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        subModel = Constant.getSubModel(this);
        mainModel = Constant.getMainModel(this);

        progressDialog = new ProgressDialog(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> {
            cancelTimer();
            ConstantDialog.showExitDialog(this, this);
        });

        linear_1 = findViewById(R.id.linear_1);
        linear_2 = findViewById(R.id.linear_2);
        tv_question_count = findViewById(R.id.tv_question_count);
        tv_right_count = findViewById(R.id.tv_right_count);
        tv_score = findViewById(R.id.tv_score);
        tv_plus_score = findViewById(R.id.tv_plus_score);
        tv_wrong_count = findViewById(R.id.tv_wrong_count);
        tv_set = findViewById(R.id.tv_set);
        layout_cell = findViewById(R.id.layout_cell);
        tv_coin = findViewById(R.id.tv_coin);
        helpLineView = findViewById(R.id.helpLineView);
        tv_total_count = findViewById(R.id.tv_total_count);
        textView1 = findViewById(R.id.textView1);
        card_1 = findViewById(R.id.card_1);
        card_2 = findViewById(R.id.card_2);
        btn_help_line = findViewById(R.id.btn_help_line);

        btn_op_true = findViewById(R.id.btn_op_true);
        btn_op_false = findViewById(R.id.btn_op_false);
        arcLayout = findViewById(R.id.arc_layout);
        progress_bar = findViewById(R.id.progress_bar);
        tv_timer = findViewById(R.id.tv_timer);
        menuLayout = findViewById(R.id.menu_layout);
        btn_fifty = findViewById(R.id.btn_fifty);
        btn_timer = findViewById(R.id.btn_timer);
        btn_audiance = findViewById(R.id.btn_audiance);
        audience_op_1 = findViewById(R.id.audience_op_1);
        audience_op_2 = findViewById(R.id.audience_op_2);


        progress_bar.setMax(Constant.TIMER);

        tv_set.setText(getTranslatedString(getString(R.string.level) + ": " + subModel.level_no));


        getSupportActionBar().setTitle(mainModel.title);

        setCoins();
        quizModelList.clear();
        setClick();
        setHelpLineView();
        setScore();


        new GetAllData().execute();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startTimer(countTime);
    }

    public void setHelpLineView() {
        helpLineView.removeAllViews();

        Log.e("helpLineCount", "" + helpLineCount);
        for (int i = 0; i < 3; i++) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(3, 3, 3, 3);
            imageView.setLayoutParams(layoutParams);
            if (helpLineCount > i) {
                imageView.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
            } else {
                imageView.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            }
            helpLineView.addView(imageView);
        }
    }

    private void setClick() {
        PushDownAnim.setPushDownAnimTo(linear_1, linear_2).setScale(MODE_SCALE, 0.89f).setDurationPush(DEFAULT_PUSH_DURATION).setDurationRelease(DEFAULT_RELEASE_DURATION);


        linear_1.setOnClickListener(this);
        linear_2.setOnClickListener(this);


        btn_help_line.setOnClickListener(v -> {
            if (isHelpLine) {
                isHelpLine = false;
                btn_help_line.setAlpha(0.5f);
                setPercentage();
            } else {
                Toast.makeText(this, getString(R.string.life_line_toast), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void setPercentage() {
        String checkAnswer;


        final int random1 = new Random().nextInt((100 - 70) + 1) + 70;
        final int random2 = new Random().nextInt((70 - 45) + 1) + 45;

        checkAnswer = quizModel.answer;
        int answerPosition = 0;

        audience_op_1.setVisibility(View.VISIBLE);
        audience_op_2.setVisibility(View.VISIBLE);
        for (int i = 0; i < optionViewList.size(); i++) {

            if (checkAnswer.equals(optionViewList.get(i).string)) {
                answerPosition = i;
                break;
            }
        }
        integerArrayList.clear();


        if (answerPosition == 0) {
            audience_op_1.setText(getTranslatedString(random1 + " %"));
            audience_op_2.setText(getTranslatedString(random2 + " %"));
        } else {
            audience_op_1.setText(getTranslatedString(random2 + " %"));
            audience_op_2.setText(getTranslatedString(random1 + " %"));
        }

        Log.e("answerPosition==", "===" + answerPosition);

    }

    private void hideMenu() {

        List<Animator> animList = new ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = btn_help_line.getX() - item.getX();
        float dy = btn_help_line.getY() - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.linear_1) {
            checkAnswer(0);
        } else if (id == R.id.linear_2) {
            checkAnswer(1);
        }
    }

    public void setNextData() {
        if (position < quizModelList.size() - 1) {
            position++;
            setData(position);
        } else {
            showFullScreenAds();

        }
    }

    public void onBackPressed() {
        cancelTimer();
        ConstantDialog.showExitDialog(this, this);
    }

    @Override
    public void onNo() {
        startTimer(countTime);
    }

    public void backIntent() {
        cancelTimer();
        quizModelList.clear();
        intent = new Intent(this, LevelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void cancelTimer() {
        if (isTimer) {
            countDownTimer.cancel();
        }
        if (handler != null) {
            handler.removeCallbacks(r);
        }

        if (answerPlayer != null) {
            answerPlayer.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelTimer();
    }

    public void passIntent() {
        NotificationScheduler.showNotification(getApplicationContext(), subModel.level_no);
        quizModelList.clear();
        subModel.right_count = right_answer_count;
        subModel.wrong_count = wrong_answer_count;
        subModel.score = score;
        Constant.saveSubModel(this, subModel);
        Constant.addModel(mainModel.title, mainModel.tableName, subModel.TYPE_CODE, getApplicationContext(), historyModels);
        intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
    }

    public void setScore() {
        tv_score.setText(getTranslatedString(String.valueOf(score)));
        tv_wrong_count.setText(getTranslatedString(String.valueOf(wrong_answer_count)));
        tv_right_count.setText(getTranslatedString(String.valueOf(right_answer_count)));
    }

    public void setFalseAction(CardView textView) {
        if (Constant.getVibrate(getApplicationContext())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibe.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibe.vibrate(400);
            }
        }
        if (!isCount) {
            isCount = true;
            wrong_answer_count++;
            tv_wrong_count.setText(getTranslatedString(String.valueOf(wrong_answer_count)));
            if ((score - 250) > 0) {
                score = score - 250;
            }
            setScore();
        }
        helpLineCount++;
        setHelpLineView();
        textView1.setColor(Color.RED);
        textView.setCardBackgroundColor(Constant.getThemeColor(this, R.attr.colorPrimary));
        textView1.setTextColor(ContextCompat.getColor(this, R.color.wrong_red_color));
        if (helpLineCount > 3) {
            if (!isVideoComplete) {
                cancelTimer();
                ConstantDialog.showVideoDialogs(TrueFalseActivity.this, TrueFalseActivity.this);
            } else {
                showFullScreenAds();
            }
        } else {
            handler.postDelayed(r, DELAY_SEOCND);
        }


    }
    boolean isEarn=false;

    public void videoShow() {


//        mRewardedAd.show();
        isEarn=false;

        mRewardedAd.show(TrueFalseActivity.this, rewardItem -> {
            isEarn=true;
            ConstantDialog.showGetLivesDialogs(TrueFalseActivity.this, TrueFalseActivity.this);

        });

        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
            }



            @Override
            public void onAdDismissedFullScreenContent() {
                if(!isEarn) {
                    showFullScreenAds();
                }
            }
        });







    }

    @Override
    protected void onResume() {
        super.onResume();
        cd = new ConnectionDetector(this);

        if (getResources().getString(R.string.GAME_OVER_ADS_VISIBILITY).equals("YES")) {
            CallNewInsertial();
        }
        setAddViews();
    }

//    private void requestNewInterstitial() {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mInterstitialAd.loadAd(adRequest);
//    }

    private void CallNewInsertial() {
        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {
//            mInterstitialAd = new InterstitialAd(this);
//            mInterstitialAd.setAdUnitId(AdsInfo.getInterstitialId(getApplicationContext()));
//            requestNewInterstitial();

            loadInterstitialId(TrueFalseActivity.this, interstitialAd -> mInterstitialAd = interstitialAd);


        }
    }

//    private void loadRewardedVideoAd() {
//        rewardedVideoAd.loadAd(AdsInfo.getRewardId(getApplicationContext()),
//                new AdRequest.Builder().build());
//    }

    public void setAddViews() {

        loadRewarded(TrueFalseActivity.this, rewardedAd -> mRewardedAd = rewardedAd);


    }

    public void setTrueAction(CardView textView) {
        if (!isCount) {
            isCount = true;
            right_answer_count++;
            addCoins();
            setCoins();
            score = score + plusScore;
            tv_right_count.setText(getTranslatedString(String.valueOf(right_answer_count)));
            setScore();
        }
        textView1.setColor(Color.TRANSPARENT);
        textView.setCardBackgroundColor(Constant.getThemeColor(this, R.attr.colorPrimary));
        textView1.setTextColor(ContextCompat.getColor(this, R.color.right_green_color));
        handler.postDelayed(r, DELAY_SEOCND);
    }

    public void checkAnswer(int pos) {

        if (isClick) {
            isClick = false;

            CardView cardView = optionViewList.get(pos).cardView;
            TextView textView = optionViewList.get(pos).textView;
            String s = optionViewList.get(pos).string;


            if (quizModel != null) {


                if (!isCount) {
                    historyUserAnswer = s;
                    history_id++;
                    historyModels.add(new HistoryModel(history_id, historyQuestion, historyAnswer, historyUserAnswer));
                }


                Log.e("checkAnswer==", "" + s + "==" + quizModel.answer);

                textView.setTextColor(Color.WHITE);
                if (s.equals((quizModel.answer))) {
                    setTrueAction(cardView);
                } else {
                    setFalseAction(cardView);
                }
            }
        }
    }

    @Override
    public void onExit() {
        backIntent();
    }

    public String getTranslatedString(String s) {
        return Constant.getAllTranslatedDigit(s);
    }

    public void setOptionView() {
        optionViewList.clear();
        optionViewList.add(new TextModel(btn_op_true, card_1));
        optionViewList.add(new TextModel(btn_op_false, card_2));


        for (int i = 0; i < optionViewList.size(); i++) {
            optionViewList.get(i).cardView.setVisibility(View.VISIBLE);
            optionViewList.get(i).cardView.setCardBackgroundColor(Constant.getThemeColor(this, R.attr.theme_cell_color));
//            optionViewList.get(i).audienceView.setTextColor(Constant.getThemeColor(this, R.attr.theme_text_color));
//            optionViewList.get(i).audienceView.setVisibility(View.GONE);
        }


        optionViewList.get(0).cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.right_green_color));
        optionViewList.get(1).cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.wrong_red_color));


    }

    public void setData(int position) {
        isClick = true;
        cancelTimer();
        plusScore = 500;
        countTime = Constant.TIMER;
        startTimer(countTime);
        isCount = false;

        audience_op_1.setVisibility(View.GONE);
        audience_op_2.setVisibility(View.GONE);
        setOptionView();
        textView1.setColor(Color.TRANSPARENT);
        quizModel = quizModelList.get(position);
        tv_question_count.setText(getTranslatedString(String.valueOf((position + 1))));
        textView1.setTextColor(Constant.getThemeColor(this, R.attr.theme_text_color));

        if (!TextUtils.isEmpty(quizModel.question)) {
            textView1.setText(getTranslatedString(String.valueOf(quizModel.question)));
            historyQuestion = textView1.getText().toString();


        }

        historyAnswer = quizModel.answer;


        for (int i = 0; i < optionViewList.size(); i++) {

            if (i == 0) {
                optionViewList.get(i).string = getString(R.string.str_true);
            } else {
                optionViewList.get(i).string = getString(R.string.str_false);
            }


        }


    }

    @Override
    public void showVideoClick(Dialog dialog) {
        if (mRewardedAd!=null) {
            videoShow();
            dialog.dismiss();
        } else {

            setAddViews();
            Toast.makeText(this, "" + getString(R.string.str_video_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void getLivesClick() {
        isVideoComplete = true;
        helpLineCount = helpLineCount - 2;
        setHelpLineView();
        setNextData();
    }

    @Override
    public void cancelClick() {
       showFullScreenAds();
    }

    public void showFullScreenAds() {
            if (mInterstitialAd != null ) {
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        Log.e("close---","true");
                        passIntent();
                    }


                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.e("show---","true");
                    }

                });
                mInterstitialAd.show(TrueFalseActivity.this);
//                mInterstitialAd.setAdListener(new AdListener() {
//                    public void onAdClosed() {
//                        passIntent();
//                    }
//                });

//                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
//                    @Override
//                    public void onAdDismissedFullScreenContent() {
//                        super.onAdDismissedFullScreenContent();
//                      passIntent();
//                    }
//                });
            } else {
                passIntent();
            }

    }


    public class GetAllData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage(getString(R.string.please_wait));
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... voids) {

            RandomOptionData learnData = new RandomOptionData(TrueFalseActivity.this, mainModel, subModel.level_no);
            learnData.setTrueFalseQuiz(true);

            for (int i = 0; i < Constant.DEFAULT_QUESTION_SIZE; i++) {
                QuizModel tableModel = learnData.getMethods();
                quizModelList.add(tableModel);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.e("quizModelList", "" + quizModelList.size());
            progressDialog.dismiss();
            tv_total_count.setText(getTranslatedString(getString(R.string.slash) + quizModelList.size()));
            if (quizModelList.size() > 0) {
                setData(position);
            }
        }

    }

}
