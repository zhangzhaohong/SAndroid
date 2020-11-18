package com.tristana.sandroid.ui.illegalManager.fragment.picture.viewer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ImageViewerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ImageViewerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is videoPlayer fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}