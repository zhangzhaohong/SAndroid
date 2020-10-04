package com.tristana.sandroid.ui.illegalManager.fragment.video.player;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VideoPlayerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public VideoPlayerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is videoPlayer fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}