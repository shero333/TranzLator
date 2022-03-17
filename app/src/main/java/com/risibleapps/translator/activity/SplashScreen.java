package com.risibleapps.translator.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.risibleapps.translator.BuildConfig;
import com.risibleapps.translator.R;

public class SplashScreen extends AppCompatActivity {

    static int SPLASH_SCREEN = 3500;
    Animation topAnim, bottomAnim;
    ImageView logoImageView;
    TextView sloganTextView;
    private InterstitialAd mInterstitialAd;

    //shared preference for storing the switched theme (Dark or Light) value/status
    SharedPreferences preference;
    SharedPreferences.Editor prefEditor;

    //variable for checking whether dark theme is applied or not
    boolean isDarkModeEnabled = false;

    public static int prefCounter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initializing preferences
        initializePreference();

        //getting the theme saved value from preference
        isDarkModeEnabled=preference.getBoolean(getString(R.string.pref_theme),false);

        if(!isDarkModeEnabled)
        {
            prefCounter++;
            AppCompatDelegate.setDefaultNightMode((AppCompatDelegate.MODE_NIGHT_NO));
        }
        else if(isDarkModeEnabled)
        {
            prefCounter++;
            AppCompatDelegate.setDefaultNightMode((AppCompatDelegate.MODE_NIGHT_YES));
        }

        //initializing animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        //splash screen components initialization
        logoImageView = findViewById(R.id.image_splash);
        sloganTextView = findViewById(R.id.slogan_splash);

        //setting animations
        logoImageView.setAnimation(topAnim);
        sloganTextView.setAnimation(bottomAnim);

        if(prefCounter<=1) {
            //ads initialization
            MobileAds.initialize(this, initializationStatus -> {
            });

            //loading the ads
            loadAd();

            //delaying the splash screen for 3.5 seconds
            //loading the ad
            new Handler().postDelayed(this::showAd, SPLASH_SCREEN);
        }

    }

    public void loadAd() {

        //checking whether app is running on release/debug version
        String interstitialAdId="";
        if(BuildConfig.DEBUG)
        {
            interstitialAdId="ca-app-pub-3940256099942544/1033173712";
            Log.i("INTER_AD_ID", "if called: "+interstitialAdId);
        }
        else {
            interstitialAdId=getString(R.string.interstitial_ad_id);
            Log.i("INTER_AD_ID", "else called: "+interstitialAdId);
        }

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, interstitialAdId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.i("FAILED_AD", "splash interstitial failed ad: "+loadAdError.getCode());
                mInterstitialAd = null;
            }
        });
    }

    public void showAd() {
        //checking if ad is loaded or not
        if (mInterstitialAd != null) {
            mInterstitialAd.show(SplashScreen.this);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    mInterstitialAd = null;

                    //moving to Home Screen
                    Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
                    startActivity(intent);
                    finish();
                }

                //this function make sure no ad is loaded for second time
                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();

                    mInterstitialAd = null;
                }
            });
        } else {
            //if there is no internet connection, then no ad will be loaded and app will move onto Home Screen
            Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mInterstitialAd = null;
    }

    @Override
    protected void onDestroy() {

        mInterstitialAd = null;
        super.onDestroy();
    }

    private void initializePreference() {
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor = preference.edit();
    }
}