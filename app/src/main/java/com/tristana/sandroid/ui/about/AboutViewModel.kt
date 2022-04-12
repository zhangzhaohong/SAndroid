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
    private val appName: MutableLiveData<String> = MutableLiveData()
    val getAppName: LiveData<String>
        get() = appName
    private val appPackageName: MutableLiveData<String> = MutableLiveData()
    val getAppPackageName: LiveData<String>
        get() = appPackageName
    private val appVersionName: MutableLiveData<String> = MutableLiveData()
    val getAppVersionName: LiveData<String>
        get() = appVersionName
    private val appVersionCode: MutableLiveData<String> = MutableLiveData()
    val getAppVersionCode: LiveData<String>
        get() = appVersionCode
    private val appPathName: MutableLiveData<String> = MutableLiveData()
    val getAppPathName: LiveData<String>
        get() = appPathName
    private val appRootMode: MutableLiveData<String> = MutableLiveData()
    val getAppRootMode: LiveData<String>
        get() = appRootMode
    private val appDebugMode: MutableLiveData<String> = MutableLiveData()
    val getAppDebugMode: LiveData<String>
        get() = appDebugMode
    private val systemAppMode: MutableLiveData<String> = MutableLiveData()
    val getSystemAppMode: LiveData<String>
        get() = systemAppMode
    private val appSignatureNameSHA1: MutableLiveData<String> = MutableLiveData()
    val getAppSignatureNameSHA1: LiveData<String>
        get() = appSignatureNameSHA1
    private val appSignatureNameSHA256: MutableLiveData<String> = MutableLiveData()
    val getAppSignatureNameSHA256: LiveData<String>
        get() = appSignatureNameSHA256
    private val appSignatureNameMD5: MutableLiveData<String> = MutableLiveData()
    val getAppSignatureNameMD5: LiveData<String>
        get() = appSignatureNameMD5

    init {
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