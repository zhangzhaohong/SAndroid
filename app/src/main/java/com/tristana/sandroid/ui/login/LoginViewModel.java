package com.tristana.sandroid.ui.login;

import com.google.gson.Gson;
import com.tristana.sandroid.model.login.LoginRespModel;
import com.tristana.sandroid.tools.http.HttpUtils;
import com.tristana.sandroid.tools.http.RequestInfo;
import com.tristana.sandroid.tools.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private MutableLiveData<String> mToast;
    private MutableLiveData<Boolean> mLoginStatus;
    private boolean isRequest = false;

    public LoginViewModel() {
        mText = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        mLoginStatus = new MutableLiveData<>(false);
        mText.setValue("This is login fragment");
    }

    public MutableLiveData<Boolean> getLoginStatus() {
        return mLoginStatus;
    }

    public MutableLiveData<String> getToast() {
        return mToast;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void doLogin(final String userName, final String password) {
        if (TextUtils.checkEmpty(userName) && TextUtils.checkEmpty(password)) {
            if (!isRequest) {
                isRequest = true;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Map<String, Object> header = new HashMap<>();
                        Map<String, Object> urlParams = new HashMap<>();
                        urlParams.put("api_key", RequestInfo.REQUEST_API_KEY);
                        urlParams.put("account", userName);
                        urlParams.put("password", password);
                        String[] data = new HttpUtils().getDataFromUrlByOkHttp3(RequestInfo.REQUEST_LOGIN, urlParams, header);
                        if (Integer.parseInt(data[0]) == -1 || Integer.parseInt(data[0]) > 400) {
                            mToast.postValue("请求失败 code:" + data[0] + "\n" + data[1]);
                        } else {
                            String json = data[1];
                            Gson gson = new Gson();
                            LoginRespModel loginRespModel = gson.fromJson(json, LoginRespModel.class);
                            int code = Integer.parseInt(loginRespModel.getCode());
                            String msg = loginRespModel.getMsg();
                            if (code == 0) {
                                mToast.postValue(msg);
                                mLoginStatus.postValue(true);
                            } else {
                                mToast.postValue("登录失败 code:" + code + "\n" + msg);
                            }
                        }
                        isRequest = false;
                    }
                }).start();
            } else {
                mToast.setValue("上一个请求正在进行中，请稍后重试！");
            }
        } else {
            mToast.setValue("用户名密码不能为空！");
        }
    }
}