package com.tristana.sandroid.ui.scheme

import android.app.Activity
import android.os.Bundle
import com.blankj.utilcode.util.ActivityUtils
import com.therouter.TheRouter
import com.therouter.router.Autowired
import com.therouter.router.Route
import com.tristana.sandroid.MainActivity
import utils.router.RouterUtils

@Route(path = SchemeFilterActivity.ROUTE, params = ["direct"])
class SchemeFilterActivity : Activity() {

    companion object {
        const val ROUTE = "http://m.sandroid.com/app/activity/scheme"
    }

    @Autowired
    var direct: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityUtils.finishToActivity(this@SchemeFilterActivity, false)
        TheRouter.inject(this)
        direct = intent.data?.getQueryParameter("direct")
        RouterUtils.routeWithDirect(MainActivity.ROUTE, direct)
        ActivityUtils.finishActivity(this@SchemeFilterActivity)
    }
}