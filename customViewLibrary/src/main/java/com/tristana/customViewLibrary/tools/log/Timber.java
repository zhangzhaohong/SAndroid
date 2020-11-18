package com.tristana.customViewLibrary.tools.log;

import android.util.Log;

/**
 * 自定义log类
 */
public class Timber {
    String TAG;

    public Timber(String TAG) {
        this.TAG = TAG;
    }

    public void i(String MESS) {
        Log.i(TAG, MESS);
    }

    public void w(String MESS) {
        Log.w(TAG, MESS);
    }

    public void d(String MESS) {
        Log.d(TAG, MESS);
    }

    public void v(String MESS) {
        Log.v(TAG, MESS);
    }
}
