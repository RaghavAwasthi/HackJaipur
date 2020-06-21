package com.hackathon.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class InfoSliderActivity extends TutorialActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addFragment(new Step.Builder().setTitle("hello")
                .setContent("A better way to help needy ")
                .setBackgroundColor(Color.parseColor("#04bcf4"))
                .setDrawable(R.drawable.ic_hello)
                .build());

        addFragment(new Step.Builder().setTitle("Everything in one place")
                .setContent("Easily manage and safely store all your Service related data")
                .setBackgroundColor(Color.parseColor("#153f87"))
                .setDrawable(R.drawable.ic_client)
                .build());

        addFragment(new Step.Builder().setTitle("Learn easily with us and get skilled")
                .setContent("Easily learn the technical skills for better future")
                .setBackgroundColor(Color.parseColor("#FF4081"))
                .setDrawable(R.drawable.ic_untitled)
                .build());
    }

    //Whole tutorial has been completed. Open MainActivity and don't show Info Slider again
    @Override
    public void finishTutorial() {
        super.finishTutorial();
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        SharedPreferences preferences = getSharedPreferences(ConstantsUtils.PREFERENCENAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(ConstantsUtils.WASRUNBEFORE, true);
        editor.apply();

        finish();
    }
}
