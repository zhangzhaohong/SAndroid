package com.tristana.library.tools.http

import com.blankj.utilcode.util.AppUtils
import java.lang.reflect.Field

/**
 * @author koala
 * @version 1.0
 * @date 2023/6/24 15:49
 * @description
 */
object OkHttpRequestGenerator {
    fun create(url: String): OkHttpUtils {
        return OkHttpUtils.builder().url(url)
            .addHeader("package-name", AppUtils.getAppPackageName())
            .addHeader("version-code", getBuildConfigValue("APP_VERSION_CODE"))
            .addHeader("package-git", getBuildConfigValue("GIT_COMMIT_ID"))
            .addHeader("request-time", System.currentTimeMillis().toString())
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