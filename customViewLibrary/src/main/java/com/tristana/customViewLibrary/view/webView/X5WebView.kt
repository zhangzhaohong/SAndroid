package com.tristana.customViewLibrary.view.webView

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Process
import android.util.AttributeSet
import android.view.View
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.tristana.customViewLibrary.customInterface.IOnPageFinishedInterface
import com.tristana.customViewLibrary.tools.log.Timber


class X5WebView(context: Context?, attributeSet: AttributeSet?) : WebView(context, attributeSet) {

    var onLoadFinishListener: IOnPageFinishedInterface? = null

    private val timber: Timber = Timber("X5WebView")

    private val client: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {
            timber.d("shouldOverrideUrlLoading:$p1")
            if (!p1!!.startsWith("http")) {
                try {
                    // 以下固定写法
                    val intent = Intent(Intent.ACTION_VIEW,
                            Uri.parse(url))
                    intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    context!!.startActivity(intent)
                } catch (e: Exception) {
                    // 防止没有安装的情况
                    e.printStackTrace()
                }
                return true
            }
            return false
        }

        override fun onPageFinished(p0: WebView?, p1: String?) {
            super.onPageFinished(p0, p1)
            onLoadFinishListener?.onPageFinished(p1)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebViewSettings() {
        val webSetting: WebSettings = this.settings
        webSetting.javaScriptEnabled = true
        webSetting.javaScriptCanOpenWindowsAutomatically = true
        webSetting.allowFileAccess = true
        webSetting.layoutAlgorithm = WebSettings.LayoutAlgorithm.NARROW_COLUMNS
        webSetting.setSupportZoom(true)
        webSetting.builtInZoomControls = true
        webSetting.useWideViewPort = true
        webSetting.setSupportMultipleWindows(true)
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true)
        // webSetting.setDatabaseEnabled(true);
        webSetting.domStorageEnabled = true
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE)
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.pluginState = WebSettings.PluginState.ON_DEMAND
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.cacheMode = WebSettings.LOAD_NO_CACHE
        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计
    }

    init {
        setBackgroundColor(85621)
        this.webViewClient = this.client
        // this.setWebChromeClient(chromeClient);
        // WebStorage webStorage = WebStorage.getInstance();
        initWebViewSettings()
        this.view.isClickable = true
    }


    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        val ret = super.drawChild(canvas, child, drawingTime)
        canvas.save()
        val paint = Paint()
        paint.color = 0x7fff0000
        paint.textSize = 24f
        paint.isAntiAlias = true
        if (x5WebViewExtension != null) {
            canvas.drawText(this.context.packageName + "-pid:"
                    + Process.myPid(), 10F, 50F, paint)
            canvas.drawText(
                    "X5  Core:" + QbSdk.getTbsVersion(this.context), 10F,
                    100F, paint)
        } else {
            canvas.drawText(this.context.packageName + "-pid:"
                    + Process.myPid(), 10F, 50F, paint)
            canvas.drawText("Sys Core", 10F, 100F, paint)
        }
        canvas.drawText(Build.MANUFACTURER, 10F, 150F, paint)
        canvas.drawText(Build.MODEL, 10F, 200F, paint)
        canvas.restore()
        return ret
    }

}