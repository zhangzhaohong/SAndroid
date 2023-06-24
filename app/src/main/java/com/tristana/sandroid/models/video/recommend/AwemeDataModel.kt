package com.tristana.sandroid.models.video.recommend

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author koala
 * @date 2023/6/24 18:13
 * @version 1.0
 * @description
 */
data class AwemeDataModel(
    @SerializedName("aweme_id")
    val awemeId: String?
) : Serializable
