package com.tristana.sandroid.ui.trafficManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrafficManagerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TrafficManagerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is traffic manager fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}