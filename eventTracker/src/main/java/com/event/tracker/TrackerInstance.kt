package com.event.tracker

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.event.tracker.ws.EventTrackerCenter
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

    fun initContext(myApplication: Application) {
        application = myApplication
        uuid = UUID.randomUUID().toString()
        EventTrackerCenter.EventTrackerStart(application)
        LogUtils.i("[EventTrackerInstance] Init event tracker finished: $uuid")
    }

    companion object {
        private var uuid: String? = null
        private var application: Application? = null
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
