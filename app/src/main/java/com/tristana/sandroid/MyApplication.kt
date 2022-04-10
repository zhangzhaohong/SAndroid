package com.tristana.sandroid

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.LogUtils
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        MainScope().launch {
            withContext(Dispatchers.IO) {
                val mConfig: LogUtils.Config = LogUtils.getConfig()
                mConfig.filePrefix = "AppLog"
                mConfig.isLog2FileSwitch = true
                LogUtils.d(mConfig)
            }
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