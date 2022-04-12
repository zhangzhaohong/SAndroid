package com.tristana.sandroid.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AboutViewModel : ViewModel() {
    val appName: MutableLiveData<String> = MutableLiveData()
    val appPackageName: MutableLiveData<String> = MutableLiveData()
    val appVersionName: MutableLiveData<String> = MutableLiveData()
    val appVersionCode: MutableLiveData<String> = MutableLiveData()
    val appPathName: MutableLiveData<String> = MutableLiveData()
    val appRootMode: MutableLiveData<String> = MutableLiveData()
    val appDebugMode: MutableLiveData<String> = MutableLiveData()
    val systemAppMode: MutableLiveData<String> = MutableLiveData()
    val appSignatureNameSHA1: MutableLiveData<String> = MutableLiveData()
    val appSignatureNameSHA256: MutableLiveData<String> = MutableLiveData()
    val appSignatureNameMD5: MutableLiveData<String> = MutableLiveData()

    init {
        initAppInfo()
    }

    private fun initAppInfo() {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                appName.value = withContext(Dispatchers.IO) {
                    AppUtils.getAppName()
                }
                appPackageName.value = withContext(Dispatchers.IO) {
                    AppUtils.getAppPackageName()
                }
                appVersionName.value = withContext(Dispatchers.IO) {
                    AppUtils.getAppVersionName()
                }
                appVersionCode.value = withContext(Dispatchers.IO) {
                    AppUtils.getAppVersionCode().toString()
                }
                appPathName.value = withContext(Dispatchers.IO) {
                    AppUtils.getAppPath()
                }
                appRootMode.value = withContext(Dispatchers.IO) {
                    AppUtils.isAppRoot().toString()
                }
                appDebugMode.value = withContext(Dispatchers.IO) {
                    AppUtils.isAppDebug().toString()
                }
                systemAppMode.value = withContext(Dispatchers.IO) {
                    AppUtils.isAppSystem().toString()
                }
                appSignatureNameSHA1.value = withContext(Dispatchers.IO) {
                    AppUtils.getAppSignaturesSHA1().toString()
                }
                appSignatureNameSHA256.value = withContext(Dispatchers.IO) {
                    AppUtils.getAppSignaturesSHA256().toString()
                }
                appSignatureNameMD5.value = withContext(Dispatchers.IO) {
                    AppUtils.getAppSignaturesMD5().toString()
                }
            }
        }
    }
}