package com.tristana.sandroid.ui.illegalManager.fragment.picture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IllegalPictureViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public IllegalPictureViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is illegalPicture fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}