package com.tristana.sandroid.ui.scheme

import android.app.Activity
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.therouter.TheRouter
import com.therouter.router.Route
import com.tristana.sandroid.MainActivity
import utils.router.RouterUtils

@Route(path = SchemeFilterActivity.ROUTE)
class SchemeFilterActivity : Activity() {

    companion object {
        const val ROUTE = "/app/activity/scheme"
    }

    var direct: String? = null
    var extra: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TheRouter.inject(this)
        direct = intent.data?.getQueryParameter("direct")
        extra = intent.data?.getQueryParameter("extra")
        RouterUtils.routeWithDirect(MainActivity.ROUTE, direct, extra)
        ActivityUtils.finishActivity(this@SchemeFilterActivity)
    }
}