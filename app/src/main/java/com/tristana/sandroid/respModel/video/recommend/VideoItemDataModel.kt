package com.tristana.sandroid.respModel.video.recommend

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * @author koala
 * @version 1.0
 * @date 2023/6/27 18:44
 * @description
 */
class VideoItemDataModel(
    @SerializedName("play_addr")
    val playAddr: VideoPlayInfoModel? = VideoPlayInfoModel(ArrayList(), 0, 0),
    val cover: CoverDataModel?
    ) : Serializable