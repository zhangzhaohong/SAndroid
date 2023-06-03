package com.tristana.sandroid.ui.router

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.LogUtils
import com.tencent.smtt.sdk.WebView
import com.therouter.TheRouter
import com.therouter.router.Autowired
import com.therouter.router.Route
import com.tristana.sandroid.R

/**
 * @author koala
 * @version 1.0
 * @date 2023/6/3 19:42
 * @description
 */
@Route(path = RouterWebActivity.ROUTE, params = ["direct"])
class RouterWebActivity : Activity() {

    companion object {
        const val ROUTE = "/app/router/web"
    }

    @Autowired
    var direct: String? = null

    lateinit var routerWebView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_router_web)
        TheRouter.inject(this)
        routerWebView = findViewById<View>(R.id.router_web_view) as WebView
        LogUtils.i("current url: $direct")
        direct?.let {
            routerWebView.loadUrl(it)
        } ?: kotlin.run {
            routerWebView.loadUrl("http://192.168.2.250/error/404")
        }
    }

    override fun onDestroy() {
        routerWebView.destroy()
        super.onDestroy()
    }
}