package com.risibleapps.translator.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.risibleapps.translator.BuildConfig;
import com.risibleapps.translator.R;

public class TranslationFullScreen extends AppCompatActivity {

    ImageView imageViewClose;
    TextView textViewSourceText, textViewTranslatedText;

    private String nativeAdId = ""; /*getString(R.string.native_ad_id)*//*"ca-app-pub-3940256099942544/2247696110"*/;
    private UnifiedNativeAd nativeAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation_full_screen);

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, initializationStatus -> {});

        //setting the layout to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initializing views
        imageViewClose = findViewById(R.id.imageview_close);
        textViewSourceText = findViewById(R.id.textview_source);
        textViewTranslatedText = findViewById(R.id.textview_translation);

        //getting the data from Fragment Translation
        Intent intent = getIntent();
        String sourceText = intent.getStringExtra("sourceText");
        String translatedText = intent.getStringExtra("translatedText");

        //setting the received data to textviews
        textViewSourceText.setText(sourceText);
        textViewTranslatedText.setText(translatedText);

        //click listener for finishing the current activity
        imageViewClose.setOnClickListener(v -> finish());

        //loading the native advance ad
        refreshAd();
    }

    private void refreshAd() {

        //checking whether the app is running in release or debug version
        if(BuildConfig.DEBUG)
        {
            nativeAdId="ca-app-pub-3940256099942544/2247696110";
            Log.i("NATIVE_AD_ID: ", "if called: "+nativeAdId);
        }
        else {
            nativeAdId=getString(R.string.native_ad_id);
            Log.i("NATIVE_AD_ID: ", "else called: "+nativeAdId);
        }

        AdLoader.Builder builder = new AdLoader.Builder(this, nativeAdId);

        // OnUnifiedNativeAdLoadedListener implementation.
        builder.forUnifiedNativeAd(
                unifiedNativeAd -> {
                    // If this callback occurs after the activity is destroyed, you must call
                    // destroy and return or you may get a memory leak.
                    boolean isDestroyed = false;
                    if (isDestroyed || isFinishing() || isChangingConfigurations()) {
                        unifiedNativeAd.destroy();
                        return;
                    }
                    // You must call destroy on old ads when you are done with them,
                    // otherwise you will have a memory leak.
                    if (nativeAd != null) {
                        nativeAd.destroy();
                    }
                    nativeAd = unifiedNativeAd;
                    FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                    UnifiedNativeAdView adView =
                            (UnifiedNativeAdView) getLayoutInflater()
                                    .inflate(R.layout.ad_unified, null);
                    populateUnifiedNativeAdView(unifiedNativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                });

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.i("FAILED_AD", "Translation Full Screen native ad failed: "+loadAdError.getCode());
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder().build());

    }

    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
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
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }
}