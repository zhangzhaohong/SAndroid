package com.tristana.library.tools.http

import android.util.Base64
import com.blankj.utilcode.util.AppUtils
import com.tristana.library.tools.text.MD5Utils
import java.lang.reflect.Field
import java.util.UUID

/**
 * @author koala
 * @version 1.0
 * @date 2023/6/24 15:49
 * @description
 */
object OkHttpRequestGenerator {

    fun create(host:String, uri: String): OkHttpUtils {
        val id = UUID.randomUUID().toString().replace("-", "")
        val requestTime = System.currentTimeMillis().toString()
        val key = MD5Utils.md5(id + uri + requestTime)
        val info = MD5Utils.md5(id + "mobile" + requestTime)
        return OkHttpUtils.builder().url(host + uri)
            .addHeader("request-id", String(Base64.encode(MD5Utils.convertMD5(id).toByteArray(), Base64.NO_WRAP or Base64.URL_SAFE)))
            .addHeader("request-time", String(Base64.encode(MD5Utils.convertMD5(requestTime).toByteArray(), Base64.NO_WRAP or Base64.URL_SAFE)))
            .addHeader("request-key", key)
            .addHeader("request-info", info)
            .addHeader("package-name", AppUtils.getAppPackageName())
            .addHeader("version-code", getBuildConfigValue("APP_VERSION_CODE"))
            .addHeader("package-git", getBuildConfigValue("GIT_COMMIT_ID"))
            .addHeader("Proxy-Client-IP", ExternalIPUtil.get())
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