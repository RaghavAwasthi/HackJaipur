package com.hackathon.myapplication.profile;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hackathon.myapplication.ConstantsUtils;
import com.hackathon.myapplication.MainFragment;
import com.hackathon.myapplication.R;
import com.hackathon.myapplication.VolunteerModel;

import static com.hackathon.myapplication.about.AboutFragment.PREF_DARK_THEME;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends MainFragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        AdView mAdView = view.findViewById(R.id.adView);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        if (useDarkTheme) {
            getActivity().setTheme(R.style.AppTheme_Dark);
            view.findViewById(R.id.holder).setBackgroundColor(getResources().getColor(R.color.black));


        }
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        AppCompatTextView name, mobile, age, email, contributions;
        AppCompatImageView imageView;
        name = view.findViewById(R.id.employee_name);
        mobile = view.findViewById(R.id.employeeMobile);
        contributions = view.findViewById(R.id.employeelevel);
        age = view.findViewById(R.id.employeeAge);
        email = view.findViewById(R.id.employeeEmail);
        imageView = view.findViewById(R.id.employee_image);
        if (useDarkTheme) {
            getActivity().setTheme(R.style.AppTheme_Dark);
            view.findViewById(R.id.holder).setBackgroundColor(getResources().getColor(R.color.black));
            name.setTextColor(getResources().getColor(R.color.white));
            mobile.setTextColor(getResources().getColor(R.color.white));
            contributions.setTextColor(getResources().getColor(R.color.white));
            age.setTextColor(getResources().getColor(R.color.white));
            email.setTextColor(getResources().getColor(R.color.white));


        }
        db.collection(ConstantsUtils.VOLUNTEER).document(mAuth.getCurrentUser().getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                VolunteerModel vm = documentSnapshot.toObject(VolunteerModel.class);

                name.setText(vm.getUsername());
                contributions.setText(Integer.toString(vm.getContributionCount()));
                age.setText(Integer.toString(vm.getAge()));
                mobile.setText(Long.toString(vm.getMobileNumber()));
                email.setText(mAuth.getCurrentUser().getEmail());
                if (mAuth.getCurrentUser().getPhotoUrl() != null)
                    Glide.with(getContext()).load(mAuth.getCurrentUser().getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(imageView);
            }
        });

        return view;
    }

    @Override
    public int getTitle() {
        return R.string.profile;
    }
}
