package com.tristana.sandroid.respModel.video.recommend

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author koala
 * @date 2023/6/24 18:12
 * @version 1.0
 * @description
 */
data class VideoRespDataModel(
    @SerializedName("aweme_list")
    val awemeList: ArrayList<AwemeDataModel>?,
    @SerializedName("has_more")
    val hasMore: Int?,
    @SerializedName("status_code")
    val statusCode: Int?
) : Serializable
