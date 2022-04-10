package com.tristana.customViewWithToolsLibrary.tools.log;

import android.content.Context;

import com.blankj.utilcode.util.LogUtils;

/**
 * 自定义log类
 */
public class Timber {

    public Timber getTimber() {
        LogUtils.Config mConfig = LogUtils.getConfig();
        mConfig.setFilePrefix("AppLog");
        mConfig.setLog2FileSwitch(true);
        LogUtils.d("LogConfig", mConfig);
        return this;
    }

    public void i(Context context, String message) {
        LogUtils.i(context, message);
    }

    public void w(Context context, String message) {
        LogUtils.w(context, message);
    }

    public void d(Context context, String message) {
        LogUtils.d(context, message);
    }

    public void v(Context context, String message) {
        LogUtils.v(context, message);
    }
}
