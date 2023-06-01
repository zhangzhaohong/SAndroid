package com.event.tracker.ws.model

/**
 * @author koala
 * @date 2023/6/1 14:03
 * @version 1.0
 * @description
 */
data class DeviceInfoDataModel(
    val deviceRootMode: Boolean?,
    val deviceAdbMode: Boolean?,
    val sdkVersionName: String?,
    val sdkVersionCode: Int?,
    val systemVersionName: String?,
    val androidId: String?,
    val macAddress: String?,
    val manuFacturer: String?,
    val model: String?,
    val abis: String?,
    val isTablet: Boolean?,
    val isEmulator: Boolean?,
    val uniqueDeviceId: String?
)