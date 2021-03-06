package com.tristana.sandroid.ui.feedback;

import com.tristana.sandroid.tools.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FeedbackViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    private MutableLiveData<String> mToast;
    private MutableLiveData<Boolean> mPhoneInvalid;

    public FeedbackViewModel() {
        mToast = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mPhoneInvalid = new MutableLiveData<>(true);
        mText.setValue("This is feedback fragment");
    }

    public MutableLiveData<Boolean> getPhoneInvalid() {
        return mPhoneInvalid;
    }

    public MutableLiveData<String> getToast() {
        return mToast;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void sendFeedBack(String title, String content, String phoneNum) {
        if (TextUtils.checkPhoneNumber(phoneNum)) {
            mToast.setValue("PHONE NUM 合法");
        } else {
            mToast.setValue("PHONE NUM 非法");
        }
    }

    public void checkPhone(String phoneNum) {
        if (TextUtils.checkPhoneNumber(phoneNum)) {
            mPhoneInvalid.setValue(true);
        } else {
            mPhoneInvalid.setValue(false);
        }
    }
}