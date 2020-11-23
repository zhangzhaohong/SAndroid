package com.tristana.sandroid.ui.webView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class X5ViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public X5ViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }

}