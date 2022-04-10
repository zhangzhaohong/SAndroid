package com.tristana.CustomViewWithToolsLibrary.tools.log;

import android.util.Log;

/**
 * 自定义log类
 */
public class Timber {
    String tag;

    public Timber(String tag) {
        this.tag = tag;
    }

    public void i(String message) {
        Log.i(tag, message);
    }

    public void w(String message) {
        Log.w(tag, message);
    }

    public void d(String message) {
        Log.d(tag, message);
    }

    public void v(String message) {
        Log.v(tag, message);
    }
}
