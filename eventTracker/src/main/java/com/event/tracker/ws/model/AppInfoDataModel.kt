package com.event.tracker.ws.model

/**
 * @author koala
 * @date 2023/6/1 14:03
 * @version 1.0
 * @description
 */
data class AppInfoDataModel(
    val appName: String?,
    val packageName: String?,
    val versionName: String?,
    val versionCode: Int,
    val mainVersionName: String?,
    val mainVersionCode: String?,
    val expandVersionName: String?,
    val expandVersionCode: String?,
    val appVersionCode: String?,
    val gitCommitId: String?,
    val buildTime: String?,
    val appRootMode: Boolean?,
    val appDebugMode: Boolean?,
    val systemAppMode: Boolean?
)