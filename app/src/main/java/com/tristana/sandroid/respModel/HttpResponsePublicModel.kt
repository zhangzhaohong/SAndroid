package com.tristana.sandroid.respModel

import java.io.Serializable

/**
 * @author koala
 * @date 2023/6/24 17:58
 * @version 1.0
 * @description
 */
data class HttpResponsePublicModel(
    val code: Int,
    val message: String,
    val data: Any?
): Serializable
