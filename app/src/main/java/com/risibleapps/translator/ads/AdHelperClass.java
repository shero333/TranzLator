package com.risibleapps.translator.ads;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
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

        String interstitialAdId;

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

                Log.e("FAILED_AD", "interstitial ad failed: "+loadAdError.getCode());
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

    public static UnifiedNativeAd refreshNativeAd(Activity activity, int viewType,Dialog dialog) {

        String nativeAdId;

        //checking whether the app is running in release or debug version
        if(BuildConfig.DEBUG) {
            nativeAdId = activity.getString(R.string.native_ad_id_debug);
        }
        else {
            nativeAdId = activity.getString(R.string.native_ad_id_release);
        }

        AdLoader.Builder builder = new AdLoader.Builder(activity, nativeAdId);

        // OnUnifiedNativeAdLoadedListener implementation.
        builder.forUnifiedNativeAd(
                unifiedNativeAd -> {
                    // If this callback occurs after the activity is destroyed, you must call
                    // destroy and return or you may get a memory leak.
                    boolean isDestroyed = false;
                    if (isDestroyed || activity.isFinishing() || activity.isChangingConfigurations()) {
                        unifiedNativeAd.destroy();
                        return;
                    }
                    // You must call destroy on old ads when you are done with them,
                    // otherwise you will have a memory leak.
                    if (nativeAd != null) {
                        nativeAd.destroy();
                    }
                    nativeAd = unifiedNativeAd;

                    // Native Ad based on viewType (Full Screen, Exit Dialog, Banner type, and recycler view native ad)
                    FrameLayout frameLayout = null;
                    UnifiedNativeAdView adView = null;

                    switch (viewType){

                        case 0: {
                            // Translation Full Screen Activity Native Ad
                            frameLayout = initializeNativeFrameLayout(activity,R.id.fl_adplaceholder);
                            adView = initializeNativeAdView(activity,R.layout.ad_unified);
                            break;
                        }

                        case 1: {
                            //Exit Dialog Native Ad
                            frameLayout = dialog.findViewById(R.id.fl_adplaceholder_dialog);
                            adView = (UnifiedNativeAdView) dialog.getLayoutInflater().inflate(R.layout.ad_unified_dialog,null);
                            break;
                        }

                        case 2:{
                            //Translate Home Fragment
                            frameLayout = initializeNativeFrameLayout(activity,R.id.fl_adplaceholder_trans_home);
                            adView = initializeNativeAdView(activity,R.layout.ad_unified_small);
                            break;
                        }

                        case 3:{
                            //Translation Fragment
                            frameLayout = initializeNativeFrameLayout(activity,R.id.fl_adplaceholder_translation);
                            adView = initializeNativeAdView(activity,R.layout.ad_unified_small);
                            break;
                        }

                        case 4:{
                            //Dictionary Fragment
                            frameLayout = initializeNativeFrameLayout(activity,R.id.fl_adplaceholder_dictionary);
                            adView = initializeNativeAdView(activity,R.layout.ad_unified_small);
                            break;
                        }

                        case 5: {
                            //Conversation Fragment
                            frameLayout = initializeNativeFrameLayout(activity,R.id.fl_adplaceholder_conversation);
                            adView = initializeNativeAdView(activity,R.layout.ad_unified_small);
                            break;
                        }

                    }


                    if(frameLayout != null){
                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);
                    }

                });

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e("FAILED_AD", "Unified Native Ad error: "+loadAdError.getCode());
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

        return nativeAd;
    }

    private static void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));


        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to

        // checking the adView before every native ad, if any views doesn't exist in native ad. It will be handled without error.

        if(adView.getBodyView() != null){

            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

        }

        if(adView.getCallToActionView() != null){

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

        }

        if(adView.getIconView() != null){

            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

        }

        if(adView.getPriceView() != null){

            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }

        }

        if(adView.getStoreView() != null){

            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }

        }

        if(adView.getStarRatingView() != null){

            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }

        }

        if(adView.getAdvertiserView() != null){

            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }

    //function for initializing the frame layout
    private static FrameLayout initializeNativeFrameLayout(Activity activity, int adPlaceHolder){
        return activity.findViewById(adPlaceHolder);
    }

    //function for initializing unified ad view
    private static UnifiedNativeAdView initializeNativeAdView(Activity activity, int layout){
        return (UnifiedNativeAdView) activity.getLayoutInflater().inflate(layout,null);
    }

}
