package com.tristana.sandroid

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.CrashUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.Utils.OnAppStatusChangedListener
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration
import com.tonyodev.fetch2core.Downloader
import com.tonyodev.fetch2okhttp.OkHttpDownloader
import com.tristana.customViewWithToolsLibrary.tools.sharedPreferences.SpUtils
import com.tristana.sandroid.model.data.DataModel
import com.tristana.sandroid.model.data.SettingModel
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
                    fetch = Fetch.getInstance(getFetchConfiguration(it))
                }
            }
            withContext(Dispatchers.IO) {
                instance?.let {
                    val mConfig: LogUtils.Config = LogUtils.getConfig()
                    mConfig.isLogSwitch = SpUtils.get(it, DataModel.LOGGER_SP, true) as Boolean
                    mConfig.filePrefix =
                        SpUtils.get(it, DataModel.LOG_FILE_PREFIX_SP, "AppLog") as String
                    mConfig.isLog2FileSwitch =
                        SpUtils.get(it, DataModel.LOG_SAVE_2_LOCAL_SP, false) as Boolean
                    mConfig.saveDays = SpUtils.get(it, DataModel.LOG_SAVE_DAY_SP, 3) as Int
                    LogUtils.d(mConfig)
                }
            }
            withContext(Dispatchers.IO) {
                instance?.let {
                    CrashUtils.init()
                }
            }
            withContext(Dispatchers.IO) {
                instance?.let { QMUISwipeBackActivityManager.init(it) }
            }
        }
        AppUtils.registerAppStatusChangedListener(appStatusChangeListener)
    }

    private fun getFetchConfiguration(application: Application): FetchConfiguration {
        return FetchConfiguration.Builder(application)
            .setDownloadConcurrentLimit(
                SpUtils.get(
                    application,
                    DataModel.MAX_DOWNLOAD_CONCURRENT_LIMIT_SP,
                    3
                ) as Int
            )
            .setProgressReportingInterval(
                SpUtils.get(
                    application,
                    SettingModel.DOWNLOAD_PROGRESS_REPORTING_INTERVAL,
                    1000L
                ) as Long
            )
            .setHttpDownloader(OkHttpDownloader(Downloader.FileDownloaderType.PARALLEL))
            .setNamespace("SAndroidApplication")
            .enableAutoStart(
                SpUtils.get(
                    application,
                    SettingModel.DOWNLOAD_AUTO_START,
                    true
                ) as Boolean
            )
            .build()
    }

    fun getFetchInstance(application: Application): Fetch? {
        if (fetch == null || fetch?.isClosed == true) {
            fetch = Fetch.getInstance(getFetchConfiguration(application))
        }
        return fetch
    }

    companion object {
        var instance: Application? = null
            private set
        var appStatusChangeListener = object : OnAppStatusChangedListener {
            override fun onForeground(activity: Activity?) {
                LogUtils.i("AppStatus: onForeground", System.currentTimeMillis())
            }

            override fun onBackground(activity: Activity?) {
                LogUtils.i("AppStatus: onBackground", System.currentTimeMillis())
            }
        }
            private set
        var fetch: Fetch? = null
    }
}