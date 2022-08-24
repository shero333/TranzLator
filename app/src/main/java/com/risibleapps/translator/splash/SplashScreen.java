package com.risibleapps.translator.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.risibleapps.translator.R;
import com.risibleapps.translator.ads.AdHelperClass;
import com.risibleapps.translator.mainActivity.HomeScreen;

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

        if(!isDarkModeEnabled) {

            prefCounter++;
            AppCompatDelegate.setDefaultNightMode((AppCompatDelegate.MODE_NIGHT_NO));
        }
        else if(isDarkModeEnabled) {

            prefCounter++;
            AppCompatDelegate.setDefaultNightMode((AppCompatDelegate.MODE_NIGHT_YES));
        }

        //initializing animations
        setSplashAnimation();

        if(prefCounter<=1) {

            //loading the ads
            mInterstitialAd = AdHelperClass.loadInterstitialAd(this);

            //delaying the splash screen for 3.5 seconds
            new Handler().postDelayed(() -> {

                //showing the interstitial ad
                AdHelperClass.showInterstitialAd(this, () -> {

                    //moving to Home Screen
                    Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
                    startActivity(intent);
                    finish();
                });

            }, SPLASH_SCREEN);
        }
    }

    private void setSplashAnimation(){
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        //splash screen components initialization
        logoImageView = findViewById(R.id.image_splash);
        sloganTextView = findViewById(R.id.slogan_splash);

        //setting animations
        logoImageView.setAnimation(topAnim);
        sloganTextView.setAnimation(bottomAnim);
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