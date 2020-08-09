package com.example.easywateringsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.easywateringsystem.R;

public class LauncherActivity extends AppCompatActivity {

    // Member variables
    private Animation mTopAnim;
    private Animation mBottomAnim;
    private ImageView mSplashImage;

    // Set timer for slpash screen to last however long (4 seconds here e.g)
    private static int SPLASH_SCREEN = 4000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        // Animations
        mTopAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        mBottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        // Hooks
        mSplashImage = findViewById(R.id.splash_iv);

        mSplashImage.setAnimation(mTopAnim);
        mSplashImage.setAnimation(mBottomAnim);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LauncherActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }

}