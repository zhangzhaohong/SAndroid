package com.tristana.sandroid.ui.httpTester;

import com.tristana.sandroid.tools.http.HttpUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HttpTesterViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private MutableLiveData<String> mToast;

    private boolean isRequest = false;

    public HttpTesterViewModel() {
        mToast = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("This is httpTester fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getToast() {
        return mToast;
    }

    public void startRequest(final String url) {
        if (!isRequest) {
            isRequest = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String[] data = new HttpUtils().getDataFromUrl(url);
                    if (Integer.parseInt(data[0]) == -1 || Integer.parseInt(data[0]) > 400) {
                        mText.postValue("请求失败 code:" + data[0] + "\n" + data[1]);
                    } else {
                        mText.postValue(data[1]);
                    }
                    isRequest = false;
                }
            }).start();
        } else {
            mToast.setValue("上一个请求正在进行中，请稍后重试！");
        }
    }
}