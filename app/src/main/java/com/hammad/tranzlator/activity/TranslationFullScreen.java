package com.hammad.tranzlator.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.hammad.tranzlator.R;

public class TranslationFullScreen extends AppCompatActivity {

    ImageView imageViewClose;
    TextView textViewSourceText,textViewTranslatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation_full_screen);

        //setting the layout to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initializing views
        imageViewClose=findViewById(R.id.imageview_close);
        textViewSourceText=findViewById(R.id.textview_source);
        textViewTranslatedText=findViewById(R.id.textview_translation);

        //getting the data from Fragment Translation
        Intent intent=getIntent();
        String sourceText=intent.getStringExtra("sourceText");
        String translatedText=intent.getStringExtra("translatedText");

        //setting the received data to textviews
        textViewSourceText.setText(sourceText);
        textViewTranslatedText.setText(translatedText);

        //click listener for finishing the current activity
        imageViewClose.setOnClickListener(v-> finish());
    }
}