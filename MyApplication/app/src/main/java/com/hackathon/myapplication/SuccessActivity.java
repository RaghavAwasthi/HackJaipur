package com.hackathon.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;

import static com.hackathon.myapplication.about.AboutFragment.PREF_DARK_THEME;


public class SuccessActivity extends AppCompatActivity {
    private ContributionFoodModel fm;
    AppCompatTextView serviceId, username, foodName, date,location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if (useDarkTheme) {
            setTheme(R.style.AppTheme_BG);
            getApplication().setTheme(R.style.AppTheme_Dark);

        }
        setContentView(R.layout.activity_success);
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        Intent i = getIntent();
        fm = (ContributionFoodModel) i.getExtras().get("MODEL");
        serviceId = findViewById(R.id.contribution_id);
        username = findViewById(R.id.employee_name);
        foodName = findViewById(R.id.contribution_count);
        location=findViewById(R.id.location);
        date=findViewById(R.id.date);
        serviceId.setText(fm.getId());
        foodName.setText(fm.getFoodname());
        location.setText(fm.getLocation());
        username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());



    }

    private void print() {
        // TODO- Setup Print function
        /*
        This function should contain the logic for direct printing the qr code

         */
    }


}



