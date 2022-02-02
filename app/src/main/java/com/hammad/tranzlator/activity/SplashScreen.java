package com.hammad.tranzlator.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.hammad.tranzlator.R;

public class SplashScreen extends AppCompatActivity {

    Animation topAnim, bottomAnim;
    ImageView logoImageView;
    TextView sloganTextView;

    static int SPLASH_SCREEN = 2700;

    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initializing animations
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_anim);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        //splash screen components initialization
        logoImageView = findViewById(R.id.image_splash);
        sloganTextView = findViewById(R.id.slogan_splash);

        //setting animations
        logoImageView.setAnimation(topAnim);
        sloganTextView.setAnimation(bottomAnim);

        //ads initialization
        MobileAds.initialize(this, initializationStatus -> {
        });

        //loading the ads
        loadAd();

        //delaying the splash screen for 3.5 seconds
        //loading the ad
        new Handler().postDelayed(this::showAd, SPLASH_SCREEN);

    }

    public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;
            }
        });
    }

    public void showAd()
    {
        //checking if ad is loaded or not
        if (mInterstitialAd != null) {
            mInterstitialAd.show(SplashScreen.this);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    //moving to Home Screen
                    Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
                    startActivity(intent);

                    mInterstitialAd = null;
                }

                //this function make sure no ad is loaded for second time
                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();

                    mInterstitialAd = null;
                }
            });
        } else{
            //if there is no internet connection, then no ad will be loaded and app will move onto Home Screen
            Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        mInterstitialAd=null;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mInterstitialAd=null;
        super.onDestroy();
    }
}