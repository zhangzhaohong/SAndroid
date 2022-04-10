package com.tristana.CustomViewWithToolsLibrary.tools.toast;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
    public static void showToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }
}
