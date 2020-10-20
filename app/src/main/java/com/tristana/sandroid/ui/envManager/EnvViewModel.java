package com.tristana.sandroid.ui.envManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EnvViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EnvViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is env fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}