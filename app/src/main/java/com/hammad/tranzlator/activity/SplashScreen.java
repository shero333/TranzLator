package com.hammad.tranzlator.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.hammad.tranzlator.R;

public class SplashScreen extends AppCompatActivity {

    Animation topAnim,bottomAnim;
    ImageView logoImageView;
    TextView sloganTextView;

    static int SPLASH_SCREEN = 3500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //initializing animations
        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_anim);
        bottomAnim=AnimationUtils.loadAnimation(this,R.anim.bottom_anim);

        //splash screen components initialization
        logoImageView=findViewById(R.id.image_splash);
        sloganTextView=findViewById(R.id.slogan_splash);

        //setting animations
        logoImageView.setAnimation(topAnim);
        sloganTextView.setAnimation(bottomAnim);

        new Handler().postDelayed(() -> {

            Intent intent=new Intent(SplashScreen.this, HomeScreen.class);
            startActivity(intent);
        }, SPLASH_SCREEN);

    }
}