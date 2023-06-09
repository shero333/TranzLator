package com.risibleapps.translator.translate.translation;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.risibleapps.translator.R;
import com.risibleapps.translator.ads.AdHelperClass;

public class TranslationFullScreen extends AppCompatActivity {

    ImageView imageViewClose;
    TextView textViewSourceText, textViewTranslatedText;

    private UnifiedNativeAd nativeAd;

    //for shimmer effect of Native ad
    ShimmerFrameLayout shimmerFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation_full_screen);

        //setting the layout to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initializing views
        imageViewClose = findViewById(R.id.imageview_close);
        textViewSourceText = findViewById(R.id.textview_source);
        textViewTranslatedText = findViewById(R.id.textview_translation);
        shimmerFrameLayout = findViewById(R.id.shimmer_full_screen);

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
        nativeAd = AdHelperClass.refreshNativeAd(this,0,null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }
}