package com.tristana.sandroid.ui.login;

import com.tristana.sandroid.tools.http.HttpUtils;
import com.tristana.sandroid.tools.log.Timber;
import com.tristana.sandroid.tools.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private MutableLiveData<String> mToast;

    public MutableLiveData<Boolean> getLoginStatus() {
        return mLoginStatus;
    }

    private MutableLiveData<Boolean> mLoginStatus;

    private boolean isRequest = false;

    public LoginViewModel() {
        mText = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        mLoginStatus = new MutableLiveData<>(false);
        mText.setValue("This is login fragment");
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
                        String url = "https://data.meternity.cn/api/v0/login.php?api_key=stiei20201014war&account=" + userName + "&password=" + password;
                        new Timber("LoginViewModel").d(url);
                        String[] data = new HttpUtils().getDataFromUrl(url);
                        if (Integer.parseInt(data[0]) == -1 || Integer.parseInt(data[0]) > 400) {
                            mToast.postValue("请求失败 code:" + data[0] + "\n" + data[1]);
                        } else {
                            String json = data[1];
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                int code = Integer.parseInt(jsonObject.get("code").toString());
                                if (code == 0) {
                                    mToast.postValue(jsonObject.getString("msg"));
                                    mLoginStatus.postValue(true);
                                } else {
                                    mToast.postValue("登录失败 code:" + code + "\n" + jsonObject.getString("msg"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mToast.postValue("JSONException" + "\n" + data[1]);
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