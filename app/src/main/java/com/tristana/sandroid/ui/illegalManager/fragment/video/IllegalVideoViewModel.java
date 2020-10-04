package com.tristana.sandroid.ui.illegalManager.fragment.video;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IllegalVideoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public IllegalVideoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is illegalVideo fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}