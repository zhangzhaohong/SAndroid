package com.tristana.sandroid.ui.illegalManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IllegalManagerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public IllegalManagerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is IllegalManager fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}