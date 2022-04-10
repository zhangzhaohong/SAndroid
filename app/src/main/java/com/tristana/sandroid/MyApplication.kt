package com.tristana.sandroid

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.LogUtils
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.tristana.customViewWithToolsLibrary.tools.sharedPreferences.SpUtils
import com.tristana.sandroid.model.data.DataModel
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
                instance?.let {
                    val mConfig: LogUtils.Config = LogUtils.getConfig()
                    mConfig.isLogSwitch = SpUtils.get(it, DataModel.LOGGER_SP, true) as Boolean
                    mConfig.filePrefix = SpUtils.get(it, DataModel.LOG_FILE_PREFIX_SP, "AppLog") as String
                    mConfig.isLog2FileSwitch = SpUtils.get(it, DataModel.LOG_SAVE_2_LOCAL_SP, false) as Boolean
                    mConfig.saveDays = SpUtils.get(it, DataModel.LOG_SAVE_DAY_SP, 3) as Int
                    LogUtils.d(mConfig)
                }
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