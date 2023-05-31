package com.event.tracker

import com.blankj.utilcode.util.LogUtils
import java.util.UUID

/**
 * @author koala
 * @date 2023/5/31 14:35
 * @version 1.0
 * @description
 */
class TrackerInstance private constructor() {

    fun getTrackerId(): String? {
        LogUtils.i("[EventTrackerInstance] current tracker: $uuid")
        return uuid
    }

    companion object {
        private var uuid: String? = null
        private var instance: TrackerInstance? = null
            get() {
                if (field == null) {
                    field = TrackerInstance()
                    uuid = UUID.randomUUID().toString()
                    LogUtils.i("[EventTrackerInstance] Init event tracker finished: $uuid")
                }
                return field
            }

        @Synchronized
        fun get(): TrackerInstance {
            return instance!!
        }
    }
}
