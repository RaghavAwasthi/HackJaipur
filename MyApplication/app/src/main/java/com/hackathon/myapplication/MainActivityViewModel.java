package com.hackathon.myapplication;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainActivityViewModel extends ViewModel {
    private MutableLiveData<MainActivityNavigationItem> mSelectedNavItem = new MutableLiveData<>();

    LiveData<MainActivityNavigationItem> getSelectedNavItem() {
        return mSelectedNavItem;
    }

    void setmSelectedNavItem(MainActivityNavigationItem item) {
        mSelectedNavItem.setValue(item);
    }

    enum MainActivityNavigationItem {
        HOME,
        ELEARN,
        CONTRIBUTION,
        PROFILE
    }
}
