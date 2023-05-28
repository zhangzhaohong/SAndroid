package com.tristana.sandroid.ui.httpTester;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tristana.library.tools.http.HttpUtils;
import com.tristana.library.tools.http.RequestInfo;

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

    public void startGetRequest(final String url) {
        if (!isRequest) {
            isRequest = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Map<String, Object> header = new HashMap<>();
                    Map<String, Object> params = new HashMap<>();
                    params.put("url", url);
                    String[] data = new HttpUtils().getDataFromCustomUrlByOkHttp3(RequestInfo.IS_TEST, params, header);
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

    public void startPostRequest(final String url) {
        if (!isRequest) {
            isRequest = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Map<String, Object> header = new HashMap<>();
                    header.put("apiId", RequestInfo.POST_API_ID);
                    header.put("apiType", RequestInfo.POST_API_TYPE);
                    header.put("signature", RequestInfo.POST_SIGNATURE);
                    Map<String, Object> params = new HashMap<>();
                    params.put("username", "admin");
                    params.put("password", "12345");
                    String[] data = new HttpUtils().postDataFromUrlByOkHttp3(url, params, header);
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