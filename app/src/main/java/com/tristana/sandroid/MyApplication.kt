package com.tristana.sandroid

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.qmuiteam.qmui.arch.QMUISwipeBackActivityManager
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.TbsListener
import com.tristana.sandroid.tools.log.Timber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.immutableListOf


class MyApplication : Application() {
    private lateinit var timber: Timber

    override fun onCreate() {
        super.onCreate()
        instance = this
        timber = Timber("MyApplication")
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        MainScope().launch {
            withContext(Dispatchers.IO) {
                /* 设置允许移动网络下进行内核下载。默认不下载，会导致部分一直用移动网络的用户无法使用x5内核 */
                QbSdk.setDownloadWithoutWifi(true)
                /* SDK内核初始化周期回调，包括 下载、安装、加载 */
                QbSdk.setTbsListener(object : TbsListener {
                    /**
                     * @param stateCode 110: 表示当前服务器认为该环境下不需要下载
                     */
                    override fun onDownloadFinish(stateCode: Int) {
                        timber.i("onDownloadFinished: $stateCode")
                    }

                    /**
                     * @param stateCode 200、232安装成功
                     */
                    override fun onInstallFinish(stateCode: Int) {
                        timber.i("onInstallFinished: $stateCode")
                    }

                    /**
                     * 首次安装应用，会触发内核下载，此时会有内核下载的进度回调。
                     * @param progress 0 - 100
                     */
                    override fun onDownloadProgress(progress: Int) {
                        timber.i("Core Downloading: $progress")
                    }
                })
                val callback: QbSdk.PreInitCallback = object : QbSdk.PreInitCallback {
                    override fun onViewInitFinished(arg0: Boolean) {
                        // TODO Auto-generated method stub
                        //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                        timber.i("onViewInitFinished: $arg0")
                    }

                    override fun onCoreInitFinished() {
                        // TODO Auto-generated method stub
                        timber.i("onCoreInitFinished")
                    }
                }
                // 在调用TBS初始化、创建WebView之前进行如下配置
                val map: Map<String, Any> = mapOf(
                    TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER to true,
                    TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE to true
                )
                QbSdk.initTbsSettings(map)
                //x5内核初始化接口
                QbSdk.initX5Environment(applicationContext, callback)
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