package com.tristana.sandroid

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.tencent.smtt.sdk.QbSdk
import com.tristana.sandroid.tools.log.Timber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MyApplication : Application() {
    private lateinit var timber: Timber

    override fun onCreate() {
        super.onCreate()
        instance = this
        timber = Timber("MyApplication")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        MainScope().launch {
            withContext(Dispatchers.IO) {
                val cb: QbSdk.PreInitCallback = object : QbSdk.PreInitCallback {
                    override fun onViewInitFinished(arg0: Boolean) {
                        // TODO Auto-generated method stub
                        //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                        timber.d("onViewInitFinished is $arg0")
                    }

                    override fun onCoreInitFinished() {
                        // TODO Auto-generated method stub
                        timber.d("onCoreInitFinished")
                    }
                }
                //x5内核初始化接口
                QbSdk.initX5Environment(applicationContext, cb)
            }
        }
        MainScope().launch {
            withContext(Dispatchers.IO) {
                instance?.let { QMUISwipeBackActivityManager.init(it) }
            }
        }
    }

    companion object {
        var instance: Application? = null
            private set
    }
}