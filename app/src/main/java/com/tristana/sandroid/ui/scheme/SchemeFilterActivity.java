package com.tristana.sandroid.ui.scheme;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import utils.router.RouterUtils;

public class SchemeFilterActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri uri = getIntent().getData();
        RouterUtils.route(uri.toString());
    }
}