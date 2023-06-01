package com.event.tracker.ws.model

/**
 * @author koala
 * @date 2023/5/31 20:37
 * @version 1.0
 * @description
 */
data class EventTrackerDataModel(
    val route: String?,
    val platform: String = "Android"
)
