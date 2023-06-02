package com.tristana.sandroid.ui.scheme;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.therouter.router.Navigator;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.therouter.TheRouter;

public class SchemeFilterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        TheRouter.build(uri).navigation(this, new NavCallback() {
            @Override
            public void onArrival(Navigator postcard) {
                finish();
            }
        });
    }
}