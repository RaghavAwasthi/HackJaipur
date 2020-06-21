package com.hackathon.myapplication;


import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

public abstract class MainFragment extends Fragment {
    public abstract @StringRes
    int getTitle();
}
