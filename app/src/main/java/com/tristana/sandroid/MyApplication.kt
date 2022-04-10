package com.tristana.sandroid

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.tristana.customViewWithToolsLibrary.tools.log.Timber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MyApplication : Application() {
    private lateinit var timber: Timber

    override fun onCreate() {
        super.onCreate()
        instance = this
        timber = Timber().timber
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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