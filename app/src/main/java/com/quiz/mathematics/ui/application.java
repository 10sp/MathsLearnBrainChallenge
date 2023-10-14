package com.quiz.mathematics.ui;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;

import com.onesignal.OneSignal;


public class application extends Application {

public static     String ONESIGNAL_APP_ID="64d331b7-a767-4e15-99c5-71f07d2dabb9";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);


    }

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);



    }



}
