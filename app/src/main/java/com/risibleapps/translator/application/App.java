package com.risibleapps.translator.application;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;

        //initializing mobile ad instance
        MobileAds.initialize(this, initializationStatus -> {});
    }

    public static App getAppContext(){
        return app;
    }
}
