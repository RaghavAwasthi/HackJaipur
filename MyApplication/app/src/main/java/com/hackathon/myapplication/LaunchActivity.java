package com.hackathon.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;

import static com.hackathon.myapplication.about.AboutFragment.PREF_DARK_THEME;


public class LaunchActivity extends AppCompatActivity {
    private boolean wasRunBefore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        MobileAds.initialize(this);
        ImageView logo = findViewById(R.id.app_logo);
        TextView title = findViewById(R.id.app_title);
        SharedPreferences preferences2 = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = preferences2.getBoolean(PREF_DARK_THEME, false);
        if (useDarkTheme) {
            setTheme(R.style.AppTheme_Dark);
        }
        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);

        logo.startAnimation(slide_up);
        title.startAnimation(slide_up);

        new Handler().postDelayed(() -> {
            SharedPreferences preferences = getSharedPreferences(ConstantsUtils.PREFERENCENAME, Context.MODE_PRIVATE);
            wasRunBefore = preferences.getBoolean(ConstantsUtils.WASRUNBEFORE, false);
            //Show info slider on first run
            if (wasRunBefore) {
                final Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(this, InfoSliderActivity.class);
                preferences.edit().putBoolean(ConstantsUtils.WASRUNBEFORE, true).apply();
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
