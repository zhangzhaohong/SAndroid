package com.event.tracker.ws.model

/**
 * @author koala
 * @date 2023/5/31 20:32
 * @version 1.0
 * @description
 */
data class WebSocketDataModel(
    val event: String,
    val sid: String?,
    val data: Any?
)
