package com.tristana.sandroid

import android.app.Application
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
import com.tristana.sandroid.tools.log.Timber


class MyApplication : Application() {
    private lateinit var timber: Timber

    override fun onCreate() {
        super.onCreate()
        timber = Timber("MyApplication")
        val cb: PreInitCallback = object : PreInitCallback {
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