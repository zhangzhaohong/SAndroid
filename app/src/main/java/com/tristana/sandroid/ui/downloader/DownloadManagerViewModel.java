package com.tristana.sandroid.ui.downloader;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DownloadManagerViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DownloadManagerViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is download manager fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}