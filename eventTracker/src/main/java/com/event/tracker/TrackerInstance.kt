package com.event.tracker

import android.annotation.SuppressLint
import android.app.Application
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.event.tracker.ws.EventTrackerCenter
import com.event.tracker.ws.connector.EventTrackerWebSocketBackgroundService
import com.event.tracker.ws.connector.EventTrackerWebSocketService
import com.event.tracker.ws.connector.WebSocketServiceManager
import com.event.tracker.ws.model.EventTrackerDataModel
import com.event.tracker.ws.model.WebSocketDataModel
import java.util.UUID

/**
 * @author koala
 * @date 2023/5/31 14:35
 * @version 1.0
 * @description
 */
class TrackerInstance private constructor() {

    fun getTrackerId(): String? {
        return uuid
    }

    fun getApplication(): Application? {
        application?.let {
            return it
        }
        LogUtils.i("[EventTrackerInstance] Get application failed, have you initContext before?")
        return null
    }

    fun stopTracker() {
        EventTrackerCenter.EventTrackerStop(application)
    }

    fun sendEvent(event: String, data: Any?) {
        eventTrackerWebSocketService?.sendText(
            GsonUtils.toJson(
                WebSocketDataModel(
                    event,
                    uuid,
                    data
                )
            )
        )
    }

    fun initContext(myApplication: Application) {
        application = myApplication
        uuid = UUID.randomUUID().toString()
        EventTrackerCenter.EventTrackerStart(application)
        eventTrackerWebSocketService =
            EventTrackerWebSocketBackgroundService.getInstance(application)
        LogUtils.i("[EventTrackerInstance] Init event tracker finished: $uuid")
    }

    companion object {
        private var uuid: String? = null
        private var application: Application? = null

        @SuppressLint("StaticFieldLeak")
        private var eventTrackerWebSocketService: EventTrackerWebSocketBackgroundService? = null
        private var instance: TrackerInstance? = null
            get() {
                if (field == null) {
                    field = TrackerInstance()
                }
                return field
            }

        @Synchronized
        fun get(): TrackerInstance {
            return instance!!
        }
    }
}
