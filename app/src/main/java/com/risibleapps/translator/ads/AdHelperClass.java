package com.risibleapps.translator.ads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.risibleapps.translator.BuildConfig;
import com.risibleapps.translator.R;
import com.risibleapps.translator.application.App;

public class AdHelperClass {

    static InterstitialAd mInterstitialAd;

    static UnifiedNativeAd nativeAd;

    public interface AdCloseListener{
        void onAdClosed();
    }

    public static InterstitialAd loadInterstitialAd(Context context) {

        String interstitialAdId="";

        if(BuildConfig.DEBUG) {
            interstitialAdId = App.getAppContext().getString(R.string.interstitial_ad_id_debug);
        }
        else {
            interstitialAdId = App.getAppContext().getString(R.string.interstitial_ad_id_release);
        }

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(context, interstitialAdId, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitialAd = interstitialAd;
                Log.i("ADS", "interstitial ad loaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitialAd = null;

                Log.e("ADS", "interstitial ad failed: "+loadAdError.getCode());
            }
        });

        return mInterstitialAd;
    }

    public static void showInterstitialAd(Activity activity, AdCloseListener adCloseListener) {
        //checking if ad is loaded or not
        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    mInterstitialAd = null;

                    //calling the interface
                    adCloseListener.onAdClosed();
                }

                //this prevents ad from showing it second time
                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    mInterstitialAd = null;
                }
            });
        } else {
            //when there is no internet connection, calling the interface
            adCloseListener.onAdClosed();
        }
    }
}
