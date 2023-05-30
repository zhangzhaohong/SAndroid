package com.tristana.sandroid.ui.about

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.DeviceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.reflect.Field

class AboutViewModel : ViewModel() {

    // appInfo
    val appName: MutableLiveData<String?> = MutableLiveData()
    val appPackageName: MutableLiveData<String?> = MutableLiveData()
    val appVersionName: MutableLiveData<String?> = MutableLiveData()
    val appVersionCode: MutableLiveData<String?> = MutableLiveData()
    val appBuildInfo: MutableLiveData<String?> = MutableLiveData()
    val appPathName: MutableLiveData<String?> = MutableLiveData()
    val appRootMode: MutableLiveData<String?> = MutableLiveData()
    val appDebugMode: MutableLiveData<String?> = MutableLiveData()
    val systemAppMode: MutableLiveData<String?> = MutableLiveData()
    val appSignatureNameSHA1: MutableLiveData<String?> = MutableLiveData()
    val appSignatureNameSHA256: MutableLiveData<String?> = MutableLiveData()
    val appSignatureNameMD5: MutableLiveData<String?> = MutableLiveData()

    // deviceInfo
    val deviceRootMode: MutableLiveData<String?> = MutableLiveData()
    val deviceAdbMode: MutableLiveData<String?> = MutableLiveData()
    val sdkVersionName: MutableLiveData<String?> = MutableLiveData()
    val sdkVersionCode: MutableLiveData<String?> = MutableLiveData()
    val systemVersionName: MutableLiveData<String?> = MutableLiveData()
    val androidId: MutableLiveData<String?> = MutableLiveData()
    val macAddress: MutableLiveData<String?> = MutableLiveData()
    val manuFacturer: MutableLiveData<String?> = MutableLiveData()
    val model: MutableLiveData<String?> = MutableLiveData()
    val abis: MutableLiveData<String?> = MutableLiveData()
    val isTablet: MutableLiveData<String?> = MutableLiveData()
    val isEmulator: MutableLiveData<String?> = MutableLiveData()
    val uniqueDeviceId: MutableLiveData<String?> = MutableLiveData()

    init {
        initAppInfo()
        initDeviceInfo()
    }

    private fun initDeviceInfo() {
        MainScope().launch {
            withContext(Dispatchers.Main) {
                deviceRootMode.value = withContext(Dispatchers.IO) {
                    DeviceUtils.isDeviceRooted().toString()
                }
                deviceAdbMode.value = withContext(Dispatchers.IO) {
                    DeviceUtils.isAdbEnabled().toString()
                }
                sdkVersionName.value = withContext(Dispatchers.IO) {
                    DeviceUtils.getSDKVersionName()
                }
                sdkVersionCode.value = withContext(Dispatchers.IO) {
                    DeviceUtils.getSDKVersionCode().toString()
                }
                systemVersionName.value = withContext(Dispatchers.IO) {
                    android.os.Build.DISPLAY
                }
                androidId.value = withContext(Dispatchers.IO) {
                    DeviceUtils.getAndroidID()
                }
                macAddress.value = withContext(Dispatchers.IO) {
                    DeviceUtils.getMacAddress().toString()
                }
                manuFacturer.value = withContext(Dispatchers.IO) {
                    DeviceUtils.getManufacturer()
                }
                model.value = withContext(Dispatchers.IO) {
                    DeviceUtils.getModel()
                }
                abis.value = withContext(Dispatchers.IO) {
                    mutableListOf(*DeviceUtils.getABIs()).toString()
                }
                isTablet.value = withContext(Dispatchers.IO) {
                    DeviceUtils.isTablet().toString()
                }
                isEmulator.value = withContext(Dispatchers.IO) {
                    DeviceUtils.isEmulator().toString()
                }
                uniqueDeviceId.value = withContext(Dispatchers.IO) {
                    DeviceUtils.getUniqueDeviceId().toString()
                }
            }

        }
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
                appBuildInfo.value = withContext(Dispatchers.IO) {
                    getBuildConfigValue("MAIN_VERSION_NAME") + "(" + getBuildConfigValue("MAIN_VERSION_CODE") + ")" + "\n" +
                            getBuildConfigValue("EXPAND_VERSION_NAME") + "(" + getBuildConfigValue("EXPAND_VERSION_CODE") + ")" + "\n" +
                            getBuildConfigValue("APP_VERSION_CODE") + "\n" +
                            getBuildConfigValue("BUILD_TIME")
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

    private fun getBuildConfigValue(fieldName: String?): String? {
        fieldName?.let {
            try {
                val packageName = AppUtils.getAppPackageName()
                val clazz = Class.forName("$packageName.BuildConfig")
                val field: Field = clazz.getField(it)
                return field.get(null)?.toString()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        return null
    }
}