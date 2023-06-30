package com.tristana.sandroid.respModel.video.recommend

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
    val awemeId: String?,
    val author: AuthorDataModel?,
    val desc: String?,
    @SerializedName("cell_room")
    val cellRoom: Any?,
    val video: VideoItemDataModel?,
    @SerializedName("share_url")
    val shareUrl: String?,
    @SerializedName("video_path")
    var videoPath: String?,
    @SerializedName("view_position")
    var viewPosition: Int
) : Serializable
