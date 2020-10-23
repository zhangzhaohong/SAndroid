package com.tristana.sandroid.ui.illegalManager.fragment.picture.viewer;

import android.graphics.Bitmap;

import com.tristana.sandroid.tools.http.HttpUtils;

import java.util.ArrayList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ImageViewerViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<ArrayList<Bitmap>> picList;

    public ImageViewerViewModel() {
        picList = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("This is videoPlayer fragment");
    }

    public MutableLiveData<ArrayList<Bitmap>> getPicList() {
        return picList;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void startGetPic(final String url) {
        final ArrayList<Bitmap> result = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                result.add(HttpUtils.getBitmap(url));
                picList.postValue(result);
            }
        }).start();
    }

}