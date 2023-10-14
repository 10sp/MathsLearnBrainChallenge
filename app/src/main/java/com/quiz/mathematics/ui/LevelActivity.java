package com.quiz.mathematics.ui;

import static com.quiz.mathematics.ui.MainActivity.rate;
import static com.quiz.mathematics.utils.AdsInfo.loadInterstitialId;
import static com.quiz.mathematics.utils.Constant.sendFeedback;
import static com.quiz.mathematics.utils.Constant.share;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quiz.mathematics.R;
import com.quiz.mathematics.adapter.LevelAdapter;
import com.quiz.mathematics.database.DatabaseAccess;
import com.quiz.mathematics.model.MainModel;
import com.quiz.mathematics.model.ProgressModel;
import com.quiz.mathematics.model.SubModel;
import com.quiz.mathematics.utils.AdsInfo;
import com.quiz.mathematics.utils.CenteredToolbar;
import com.quiz.mathematics.utils.ConnectionDetector;
import com.quiz.mathematics.utils.Constant;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.interstitial.InterstitialAd;


import java.util.List;


public class LevelActivity extends AppCompatActivity implements LevelAdapter.ItemClick {

    RecyclerView recyclerView;
    ImageView btn_drawer;
    TextView text_header;
    ProgressDialog progressDialog;
    RelativeLayout layout_cell;
    TextView tv_total_set, tv_total_question;
    View view;
    InterstitialAd mInterstitialAd;
    MainModel mainModel;
    SubModel subModel;
    ConnectionDetector cd;
    DatabaseAccess databaseAccess;
    List<ProgressModel> progressModelLis;

    private void CallNewInsertial() {
        cd = new ConnectionDetector(this);
        if (cd.isConnectingToInternet()) {

            loadInterstitialId(LevelActivity.this, interstitialAd -> mInterstitialAd = interstitialAd);


        }
    }

//    private void requestNewInterstitial() {
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mInterstitialAd.loadAd(adRequest);
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Constant.setDefaultLanguage(this);
        setContentView(R.layout.activity_level);
        init();
        adview();
    }

    private void adview() {
        RelativeLayout linearLayout = findViewById(R.id.ll_ads_view);
        AdView adView = new AdView(getApplicationContext());
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(AdsInfo.getBannerId(getApplicationContext()));
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.addView(adView, params);

    }

    @Override
    protected void onResume() {
        super.onResume();
        cd = new ConnectionDetector(this);
        if (getResources().getString(R.string.HOME_ADS_VISIBILITY).equals("YES")) {
            CallNewInsertial();
        }
    }

    private void init() {
        subModel = Constant.getSubModel(this);
        mainModel = Constant.getMainModel(this);
        CenteredToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> backIntent());
        getSupportActionBar().setTitle(null);
        progressDialog = new ProgressDialog(this);


        btn_drawer = findViewById(R.id.btn_drawer);
        text_header = findViewById(R.id.text_header);
        layout_cell = findViewById(R.id.layout_cell);
        tv_total_set = findViewById(R.id.tv_total_set);
        tv_total_question = findViewById(R.id.tv_total_question);
        view = findViewById(R.id.view);


        text_header.setText(mainModel.title);
        getSupportActionBar().setTitle(subModel.title);
        recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        databaseAccess = DatabaseAccess.getInstance(LevelActivity.this);
        databaseAccess.open();
        int size = databaseAccess.getProgressLevels(mainModel.tableName, subModel.type).size();
        databaseAccess.close();


        Log.e("size==", "" + size);

        if (size > 0) {
            setAdapter();
        } else {
            try {
                new AddData().execute();
            }catch (OutOfMemoryError e){
                e.printStackTrace();
            }
        }


    }

    public void setAdapter() {


        databaseAccess = DatabaseAccess.getInstance(LevelActivity.this);
        databaseAccess.open();
        progressModelLis = databaseAccess.getProgressShowLevel(mainModel.tableName, subModel.type);
        databaseAccess.close();


        LevelAdapter levelAdapter = new LevelAdapter(this, progressModelLis);
        recyclerView.setAdapter(levelAdapter);
        levelAdapter.setClickListener(this);
        tv_total_set.setText(Constant.getAllTranslatedDigit(String.valueOf(Constant.DEFAULT_LEVEL)));
        tv_total_question.setText(Constant.getAllTranslatedDigit(String.valueOf((Constant.DEFAULT_LEVEL * Constant.DEFAULT_QUESTION))));


    }

    @Override
    public void itemClick(int position) {
        if (mInterstitialAd != null ) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.e("close---","true");
                    subModel.level_no = (position);
                    Constant.saveSubModel(LevelActivity.this, subModel);
                    startActivity(new Intent(LevelActivity.this, Constant.getTypeClass(subModel)));



                }


                @Override
                public void onAdShowedFullScreenContent() {
                    Log.e("show---","true");
                }

            });




            mInterstitialAd.show(LevelActivity.this);

        } else {
            subModel.level_no = (position);
            Constant.saveSubModel(LevelActivity.this, subModel);
            startActivity(new Intent(LevelActivity.this, Constant.getTypeClass(subModel)));
        }

    }

    public void backIntent() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        backIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_feedback:
                sendFeedback(this, null);
                return true;
            case R.id.menu_test:
                startActivity(new Intent(this, AllReviewTestActivity.class));
                return true;
            case R.id.menu_share:
                share(this);
                return true;
            case R.id.menu_rate:
                rate(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem register = menu.findItem(R.id.menu_test);
        register.setVisible(true);
        return true;
    }

    public class AddData extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            progressDialog.setMessage(getString(R.string.please_wait));
        }

        @Override
        protected String doInBackground(Void... voids) {


            for (int i = 0; i < Constant.DEFAULT_LEVEL; i++) {
                databaseAccess = DatabaseAccess.getInstance(LevelActivity.this);
                databaseAccess.open();

                Log.e("subModel---","--"+mainModel.tableName);
                Log.e("databaseAccess", "" + databaseAccess.insertProgressData(new ProgressModel(mainModel.tableName, subModel.type, (i + 1), 0)));
                databaseAccess.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            databaseAccess = DatabaseAccess.getInstance(LevelActivity.this);
            databaseAccess.open();
            int size = databaseAccess.getProgressLevels(mainModel.tableName, subModel.type).size();
            databaseAccess.close();


            Log.e("size==", "" + size);


            setAdapter();
        }
    }


}
